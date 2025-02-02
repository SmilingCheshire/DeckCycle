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

/**
 * Activity that allows users to create a new deck by adding word pairs.
 * It provides functionality for adding word fields dynamically and saving the deck.
 *
 * @see AddDeckPresenter The presenter that handles the business logic for saving the deck.
 */
class AddDeck : AppCompatActivity(), AddDeckView {

    private lateinit var presenter: AddDeckPresenter
    private lateinit var dynamicContainer: LinearLayout
    private lateinit var deckNameField: EditText // For the deck name

    /**
     * Called when the activity is created. Sets up the UI and handles button clicks.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        // Initialize the presenter and view components
        presenter = AddDeckPresenter(this, this)
        dynamicContainer = findViewById(R.id.dynamicContainer)
        deckNameField = findViewById(R.id.deckName)

        val btnToAddWord = findViewById<Button>(R.id.addWord)
        val btnSaveDeck = findViewById<Button>(R.id.saveDeck)

        // Add new word fields dynamically when the button is clicked
        btnToAddWord.setOnClickListener {
            addWordFields()
        }

        // Save the deck when the button is clicked
        btnSaveDeck.setOnClickListener {
            saveDeck()
        }

        // Navigate to the Lobby when the button is clicked
        val btnToLobby = findViewById<Button>(R.id.Lobby)
        btnToLobby.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }
    }

    /**
     * Adds input fields for a new word pair (word in two languages) to the UI dynamically.
     */
    private fun addWordFields() {
        val word1Field = EditText(this).apply {
            hint = "Word in Language 1"
        }
        val word2Field = EditText(this).apply {
            hint = "Word in Language 2"
        }

        // Add the fields to the dynamic container (LinearLayout)
        dynamicContainer.addView(word1Field)
        dynamicContainer.addView(word2Field)
    }

    /**
     * Collects the deck name and word pairs from the UI and passes them to the presenter for saving.
     */
    private fun saveDeck() {
        val wordPairs = mutableListOf<Pair<String, String>>()

        // Get the deck name from the input field
        val deckName = deckNameField.text.toString().trim()

        if (deckName.isBlank()) {
            Toast.makeText(this, "Deck name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Loop through the dynamic container to collect word pairs from the UI
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
            i += 2 // Move to the next pair of fields
        }

        // Call the presenter to handle saving the deck
        presenter.saveDeck(deckName, wordPairs)
    }

    /**
     * Updates the UI by adding a pre-populated word pair to the dynamic container.
     * This is called when a word pair is added via the presenter.
     *
     * @param word1 The first word in the pair.
     * @param word2 The second word in the pair.
     */
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

    /**
     * Displays a success message and navigates to the Lobby when the deck is saved.
     */
    override fun onDeckSaved() {
        Toast.makeText(this, "Deck Saved!", Toast.LENGTH_SHORT).show()
        // Navigate to the Lobby after saving the deck
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
    }

    /**
     * Handles failure when saving the deck. The error message is provided but not currently implemented.
     *
     * @param error A string containing the error message.
     */
    override fun onSaveFailure(error: String) {
        // Optionally, show an error message or log the failure
    }
}