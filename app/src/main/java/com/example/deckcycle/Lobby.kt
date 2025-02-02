package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.presenter.LobbyPresenter
import com.example.deckcycle.presenter.LobbyView

/**
 * The Lobby activity represents the main screen where the user can view all available decks,
 * add a new deck, and edit or select existing decks to play with.
 *
 * @see LobbyPresenter The presenter that handles the logic of loading and refreshing deck data.
 */
class Lobby : AppCompatActivity(), LobbyView {

    private lateinit var presenter: LobbyPresenter
    private lateinit var dynamicContainer: LinearLayout

    /**
     * Called when the activity is created. This method initializes the views, sets up the necessary listeners,
     * and loads the list of available decks from the presenter.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        dynamicContainer = findViewById(R.id.dynamicContainer)
        val btnToAddDeck = findViewById<Button>(R.id.addDeck)

        // Initialize the presenter and load the decks
        presenter = LobbyPresenter(this, this)
        presenter.loadDecks()

        // Navigate to AddDeck activity without creating a placeholder deck
        btnToAddDeck.setOnClickListener {
            val intent = Intent(this, AddDeck::class.java)
            startActivity(intent)
        }
    }

    /**
     * Refreshes the list of decks displayed in the lobby by dynamically creating UI elements
     * for each deck and adding them to the `dynamicContainer`.
     *
     * @param decks A list of deck names to display in the lobby.
     */
    override fun refreshDeckList(decks: List<String>) {
        dynamicContainer.removeAllViews()

        decks.forEach { deckName ->
            // Create a horizontal layout for each deck row
            val deckRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
            }

            // Create a TextView for the deck name
            val deckView = TextView(this).apply {
                text = deckName
                textSize = 18f
                setPadding(16, 16, 16, 16)
                setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            }

            // Add an OnClickListener to the TextView (optional for mode selection)
            deckView.setOnClickListener {
                val intent = Intent(this@Lobby, ModeSelection::class.java)
                val deckId = getDeckIdByName(deckName) // Get the deck's ID
                intent.putExtra("deckId", deckId) // Pass the deck ID
                startActivity(intent)
            }

            // Create a Button for editing the deck
            val editDeckButton = Button(this).apply {
                text = "Edit"
                textSize = 16f
                setPadding(16, 8, 8, 8)
            }

            // Set the OnClickListener for the Edit button
            editDeckButton.setOnClickListener {
                val intent = Intent(this@Lobby, EditDeck::class.java).apply {
                    val deckId = getDeckIdByName(deckName)
                    putExtra("deckId", deckId) // Pass the deck ID
                }
                startActivity(intent)
            }

            // Add the TextView and Edit button to the row layout
            deckRow.addView(deckView)
            deckRow.addView(editDeckButton)

            // Add the row layout to the dynamic container
            dynamicContainer.addView(deckRow)
        }
    }

    /**
     * Retrieves the deck ID associated with a given deck name from the database.
     *
     * @param deckName The name of the deck.
     * @return The ID of the deck, or null if no deck with the given name exists.
     */
    private fun getDeckIdByName(deckName: String): Long? {
        val dbHelper = DatabaseHelper(this)
        val decks = dbHelper.getAllDecks()
        return decks.find { it.second == deckName }?.first
    }

    /**
     * Called when the activity is resumed. This method reloads the list of decks to ensure that
     * any updates made to the decks are reflected in the UI.
     */
    override fun onResume() {
        super.onResume()
        presenter.loadDecks() // Reload decks to fetch updated names
    }
}

