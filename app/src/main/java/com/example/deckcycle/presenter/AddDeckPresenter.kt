package com.example.deckcycle.presenter

import android.content.Context
import com.example.deckcycle.model.DatabaseHelper

/**
 * Presenter responsible for handling deck addition logic.
 * @param context The application context.
 * @param view The associated view interface for interaction.
 */
class AddDeckPresenter(private val context: Context, private val view: AddDeckView) {

    /**
     * Saves a deck with the given name and words.
     * @param deckName The name of the deck.
     * @param words The list of word pairs to be added to the deck.
     */
    fun saveDeck(deckName: String, words: List<Pair<String, String>>) {
        val dbHelper = DatabaseHelper(context)
        if (deckName.isBlank()) {
            view.onSaveFailure("Deck name cannot be empty.")
            return
        }

        try {
            val deckId = dbHelper.insertDeck(deckName)
            dbHelper.updateDeckWords(deckId, words)
            view.onDeckSaved()
        } catch (e: Exception) {
            view.onSaveFailure("Failed to save deck: ${e.message}")
        }
    }
}

/**
 * Interface defining the contract for views that interact with deck creation.
 */
interface AddDeckView {
    /**
     * Handles the UI update when a word pair is added.
     * @param word1 First word in the pair.
     * @param word2 Second word in the pair.
     */
    fun addWordToUI(word1: String, word2: String)
    /**
     * Called when the deck is successfully saved.
     */
    fun onDeckSaved()
    /**
     * Called when saving the deck fails.
     * @param error The error message.
     */
    fun onSaveFailure(error: String)
}