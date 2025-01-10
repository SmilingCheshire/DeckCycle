package com.example.deckcycle.presenter

import android.content.Context
import com.example.deckcycle.model.DatabaseHelper

class AddDeckPresenter(private val context: Context, private val view: AddDeckView) {

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

interface AddDeckView {
    fun addWordToUI(word1: String, word2: String)
    fun onDeckSaved()
    fun onSaveFailure(error: String)
}