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

/**
 * Activity that allows users to edit an existing deck. Users can add or remove word pairs,
 * save changes to the deck, and delete the deck entirely.
 *
 * @see EditDeckPresenter The presenter that handles the logic for loading, saving, and deleting the deck.
 */
class EditDeck : AppCompatActivity(), EditDeckView {

    private lateinit var presenter: EditDeckPresenter
    private lateinit var dynamicContainer: LinearLayout
    private lateinit var btnAddWord: Button
    private lateinit var btnSaveDeck: Button
    private lateinit var btnLobby: Button
    private lateinit var btnDeleteDeck: Button

    private var deckId: Long = -1L // Deck ID to be fetched from intent

    /**
     * Called when the activity is created. Initializes the UI, sets up listeners for buttons,
     * and loads the deck if a valid deck ID is provided.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deck)

        // Initialize view components
        dynamicContainer = findViewById(R.id.dynamicContainer)
        btnAddWord = findViewById(R.id.addWord)
        btnSaveDeck = findViewById(R.id.saveDeck)
        btnLobby = findViewById(R.id.Lobby)
        btnDeleteDeck = findViewById(R.id.deleteDeck)

        // Initialize the presenter
        presenter = EditDeckPresenter(this, this)

        // Fetch deck ID from intent extras
        deckId = intent.getLongExtra("deckId", -1L)
        if (deckId != -1L) {
            presenter.loadDeck(deckId) // Load deck from the database
        } else {
            Toast.makeText(this, "Invalid deck ID", Toast.LENGTH_SHORT).show()
            finish() // Finish activity if deckId is invalid
        }

        // Add a new word pair to the UI when clicked
        btnAddWord.setOnClickListener {
            addWordToUI("", "") // Add empty word fields
        }

        // Save the deck when clicked
        btnSaveDeck.setOnClickListener {
            saveDeck()
        }

        // Navigate back to the Lobby when clicked
        btnLobby.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }

        // Delete the deck when clicked
        btnDeleteDeck.setOnClickListener {
            deleteDeck()
        }
    }

    /**
     * Populates the deck with the words fetched from the database.
     * This method clears any existing word pairs in the UI and adds the new ones.
     *
     * @param words The list of word pairs to display in the UI.
     */
    override fun populateDeck(words: List<Pair<String, String>>) {
        dynamicContainer.removeAllViews() // Clear existing word fields
        words.forEach { (word1, word2) ->
            addWordToUI(word1, word2) // Populate each word pair in the UI
        }
    }

    /**
     * Adds a pair of word input fields to the UI dynamically.
     * Each pair consists of two `EditText` fields for the user to input words.
     *
     * @param word1 The first word to prepopulate in the input field.
     * @param word2 The second word to prepopulate in the input field.
     */
    private fun addWordToUI(word1: String, word2: String) {
        val wordPairLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 8, 8, 8)
        }

        // First word input field
        val word1Input = EditText(this).apply {
            hint = "Word 1"
            setText(word1)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Second word input field
        val word2Input = EditText(this).apply {
            hint = "Word 2"
            setText(word2)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Add both inputs to the layout
        wordPairLayout.addView(word1Input)
        wordPairLayout.addView(word2Input)

        // Add the word pair layout to the container
        dynamicContainer.addView(wordPairLayout)
    }

    /**
     * Collects all word pairs from the UI and saves the deck with the new name and word pairs.
     */
    private fun saveDeck() {
        val updatedName = findViewById<EditText>(R.id.Name).text.toString()
        val words = mutableListOf<Pair<String, String>>()

        // Iterate through the dynamic container to extract word pairs
        for (i in 0 until dynamicContainer.childCount) {
            val row = dynamicContainer.getChildAt(i) as LinearLayout
            val word1 = (row.getChildAt(0) as EditText).text.toString()
            val word2 = (row.getChildAt(1) as EditText).text.toString()

            // Only add non-empty word pairs to the list
            if (word1.isNotBlank() && word2.isNotBlank()) {
                words.add(Pair(word1, word2))
            }
        }

        // Save the updated deck using the presenter
        presenter.saveDeck(deckId, updatedName, words)

        // Navigate back to the Lobby
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Deletes the deck from the database.
     * The deletion is handled by the presenter.
     */
    private fun deleteDeck() {
        presenter.deleteDeck(deckId)
    }

    /**
     * Called when the deck is successfully saved.
     * Displays a success message and finishes the activity.
     */
    override fun onSaveSuccess() {
        Toast.makeText(this, "Deck saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * Called when saving the deck fails.
     * Displays an error message with the reason for the failure.
     *
     * @param error The error message explaining why the save operation failed.
     */
    override fun onSaveFailure(error: String) {
        Toast.makeText(this, "Failed to save deck: $error", Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when the deck is successfully deleted.
     * Displays a success message and navigates back to the Lobby.
     */
    override fun onDeleteSuccess() {
        Toast.makeText(this, "Deck deleted successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Called when deleting the deck fails.
     * Displays an error message with the reason for the failure.
     *
     * @param error The error message explaining why the delete operation failed.
     */
    override fun onDeleteFailure(error: String) {
        Toast.makeText(this, "Failed to delete deck: $error", Toast.LENGTH_SHORT).show()
    }
}