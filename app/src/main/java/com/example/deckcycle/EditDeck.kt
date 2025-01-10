package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.EditDeckPresenter
import com.example.deckcycle.presenter.EditDeckView

class EditDeck : AppCompatActivity(), EditDeckView {

    private lateinit var presenter: EditDeckPresenter
    private lateinit var dynamicContainer: LinearLayout
    private lateinit var btnAddWord: Button
    private lateinit var btnSaveDeck: Button
    private lateinit var btnLobby: Button
    private lateinit var btnDeleteDeck: Button

    private var deckId: Long = -1L // Deck ID to be fetched from intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deck)

        dynamicContainer = findViewById(R.id.dynamicContainer)
        btnAddWord = findViewById(R.id.addWord)
        btnSaveDeck = findViewById(R.id.saveDeck)
        btnLobby = findViewById(R.id.Lobby)
        btnDeleteDeck = findViewById(R.id.deleteDeck)

        presenter = EditDeckPresenter(this, this)

        // Fetch deckId from intent
        deckId = intent.getLongExtra("deckId", -1L)
        if (deckId != -1L) {
            presenter.loadDeck(deckId) // Load deck from database
        } else {
            Toast.makeText(this, "Invalid deck ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Add a new word to the UI dynamically
        btnAddWord.setOnClickListener {
            addWordToUI("", "")
        }

        // Save the deck
        btnSaveDeck.setOnClickListener {
            saveDeck()
        }

        // Navigate back to Lobby
        btnLobby.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }

        // Delete the deck
        btnDeleteDeck.setOnClickListener {
            deleteDeck()
        }
    }

    override fun populateDeck(words: List<Pair<String, String>>) {
        dynamicContainer.removeAllViews() // Clear existing fields
        words.forEach { (word1, word2) ->
            addWordToUI(word1, word2) // Populate words in the UI
        }
    }

    private fun addWordToUI(word1: String, word2: String) {
        val wordPairLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 8, 8, 8)
        }

        // First word input
        val word1Input = EditText(this).apply {
            hint = "Word 1"
            setText(word1)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Second word input
        val word2Input = EditText(this).apply {
            hint = "Word 2"
            setText(word2)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Add views to the layout
        wordPairLayout.addView(word1Input)
        wordPairLayout.addView(word2Input)
        dynamicContainer.addView(wordPairLayout)
    }

    private fun saveDeck() {
        val updatedName = findViewById<EditText>(R.id.Name).text.toString()
        val words = mutableListOf<Pair<String, String>>()

        // Iterate over dynamicContainer children to extract words
        for (i in 0 until dynamicContainer.childCount) {
            val row = dynamicContainer.getChildAt(i) as LinearLayout
            val word1 = (row.getChildAt(0) as EditText).text.toString()
            val word2 = (row.getChildAt(1) as EditText).text.toString()
            if (word1.isNotBlank() && word2.isNotBlank()) {
                words.add(Pair(word1, word2))
            }
        }

        // Save the deck using the presenter
        presenter.saveDeck(deckId, updatedName,words)

        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish()
    }

    private fun deleteDeck() {
        presenter.deleteDeck(deckId)
    }

    override fun onSaveSuccess() {
        Toast.makeText(this, "Deck saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSaveFailure(error: String) {
        Toast.makeText(this, "Failed to save deck: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteSuccess() {
        Toast.makeText(this, "Deck deleted successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDeleteFailure(error: String) {
        Toast.makeText(this, "Failed to delete deck: $error", Toast.LENGTH_SHORT).show()
    }
}