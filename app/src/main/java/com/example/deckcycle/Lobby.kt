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

class Lobby : AppCompatActivity(), LobbyView {

    private lateinit var presenter: LobbyPresenter
    private lateinit var dynamicContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        dynamicContainer = findViewById(R.id.dynamicContainer)
        val btnToAddDeck = findViewById<Button>(R.id.addDeck)

        presenter = LobbyPresenter(this, this)
        presenter.loadDecks()

        // Navigate to AddDeck activity without creating a placeholder deck
        btnToAddDeck.setOnClickListener {
            val intent = Intent(this, AddDeck::class.java)
            startActivity(intent)
        }
    }

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

    private fun getDeckIdByName(deckName: String): Long? {
        val dbHelper = DatabaseHelper(this)
        val decks = dbHelper.getAllDecks()
        return decks.find { it.second == deckName }?.first
    }

    override fun onResume() {
        super.onResume()
        presenter.loadDecks() // Reload decks to fetch updated names
    }
}
