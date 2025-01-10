package com.example.deckcycle.presenter

import android.content.Context
import com.example.deckcycle.model.DatabaseHelper

interface EditDeckView {
    fun populateDeck(words: List<Pair<String, String>>)
    fun onSaveSuccess()
    fun onSaveFailure(error: String)
    fun onDeleteSuccess()
    fun onDeleteFailure(error: String)
}

class EditDeckPresenter(private val view: EditDeckView, context: Context) {

    private val dbHelper = DatabaseHelper(context)

    //Loads the deck details (name and words) from the database using the deck ID.
    fun loadDeck(deckId: Long) {
        try {
            val words = dbHelper.getWordsInDeck(deckId)
            view.populateDeck(words)
        } catch (e: Exception) {
            view.onSaveFailure("Error loading deck: ${e.message}")
        }
    }

    //Saves the updated deck details, including its name and words.
    fun saveDeck(deckId: Long, deckName: String, words: List<Pair<String, String>>) {
        try {
            // Handle the case where the deck name is blank
            val finalDeckName = if (deckName.isBlank()) {
                val existingDeckName = dbHelper.getAllDecks()
                    .find { it.first == deckId }?.second
                existingDeckName ?: "Untitled Deck"
            } else {
                deckName
            }

            // Update the deck name and words
            dbHelper.updateDeckName(deckId, finalDeckName)
            dbHelper.updateDeckWords(deckId, words)

            // Notify the view of a successful save
            view.onSaveSuccess()
        } catch (e: Exception) {
            view.onSaveFailure("Error saving deck: ${e.message}")
        }
    }

    //Deletes the deck from the database using the deck ID.

    fun deleteDeck(deckId: Long) {
        try {
            val result = dbHelper.deleteDeck(deckId)
            if (result) {
                view.onDeleteSuccess()
            } else {
                view.onDeleteFailure("Failed to delete the deck.")
            }
        } catch (e: Exception) {
            view.onDeleteFailure("Error deleting deck: ${e.message}")
        }
    }
}
