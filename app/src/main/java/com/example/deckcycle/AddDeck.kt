package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.AddDeckPresenter
import com.example.deckcycle.presenter.AddDeckView

class AddDeck : AppCompatActivity(), AddDeckView {

    private lateinit var presenter: AddDeckPresenter
    private lateinit var dynamicContainer: LinearLayout
    private lateinit var deckNameField: EditText // For the deck name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        presenter = AddDeckPresenter(this, this)
        dynamicContainer = findViewById(R.id.dynamicContainer)
        deckNameField = findViewById(R.id.deckName) // Add a field for deck name in your layout

        val btnToAddWord = findViewById<Button>(R.id.addWord)
        val btnSaveDeck = findViewById<Button>(R.id.saveDeck)

        // Add new word fields dynamically
        btnToAddWord.setOnClickListener {
            addWordFields()
        }

        // Save the deck when the button is clicked
        btnSaveDeck.setOnClickListener {
            saveDeck()
        }

        val btnToLobby = findViewById<Button>(R.id.Lobby)
        btnToLobby.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }
    }

    private fun addWordFields() {
        val word1Field = EditText(this).apply {
            hint = "Word in Language 1"
        }
        val word2Field = EditText(this).apply {
            hint = "Word in Language 2"
        }

        // Add the fields to the container
        dynamicContainer.addView(word1Field)
        dynamicContainer.addView(word2Field)
    }

    private fun saveDeck() {
        val wordPairs = mutableListOf<Pair<String, String>>()

        // Get the deck name from the input field
        val deckName = deckNameField.text.toString().trim()

        if (deckName.isBlank()) {
            Toast.makeText(this, "Deck name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Loop through the dynamicContainer to find word pairs
        var i = 0
        while (i < dynamicContainer.childCount) {
            val word1Field = dynamicContainer.getChildAt(i) as? EditText
            val word2Field = dynamicContainer.getChildAt(i + 1) as? EditText

            if (word1Field != null && word2Field != null) {
                val word1 = word1Field.text.toString()
                val word2 = word2Field.text.toString()

                // Only add non-empty word pairs to the list
                if (word1.isNotBlank() && word2.isNotBlank()) {
                    wordPairs.add(word1 to word2)
                }
            }
            i += 2 // Move to the next pair
        }

        // Call presenter to handle the deck name and word pairs
        presenter.saveDeck(deckName, wordPairs)
    }

    override fun addWordToUI(word1: String, word2: String) {
        val word1View = EditText(this).apply {
            setText(word1) // Prepopulate with the word
        }
        val word2View = EditText(this).apply {
            setText(word2) // Prepopulate with the word
        }

        dynamicContainer.addView(word1View)
        dynamicContainer.addView(word2View)
    }

    override fun onDeckSaved() {
        Toast.makeText(this, "Deck Saved!", Toast.LENGTH_SHORT).show()
        // Optionally, navigate back to the Lobby or clear inputs
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
    }

    override fun onSaveFailure(error: String) {
    }
}