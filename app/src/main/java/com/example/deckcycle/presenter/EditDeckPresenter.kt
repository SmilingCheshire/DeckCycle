package com.example.deckcycle.presenter

import android.content.Context
import com.example.deckcycle.model.DatabaseHelper

/**
 * Interface that represents the view in the Edit Deck feature.
 * This interface defines methods for updating the UI based on the success or failure of actions
 * related to editing a deck, such as saving and deleting a deck.
 */
interface EditDeckView {
    fun populateDeck(words: List<Pair<String, String>>)
    fun onSaveSuccess()
    fun onSaveFailure(error: String)
    fun onDeleteSuccess()
    fun onDeleteFailure(error: String)
}
/**
 * Presenter class responsible for managing the logic of editing a deck.
 * It interacts with the database and updates the view according to the result of the operations.
 *
 * @param view The view that implements the EditDeckView interface, used for updating the UI.
 * @param context The context used to initialize the DatabaseHelper.
 */
class EditDeckPresenter(private val view: EditDeckView, context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Loads the deck details (name and words) from the database using the deck ID.
     * This method fetches the words of the deck and populates the view with this data.
     *
     * @param deckId The ID of the deck to be loaded.
     */
    fun loadDeck(deckId: Long) {
        try {
            val words = dbHelper.getWordsInDeck(deckId)
            view.populateDeck(words)
        } catch (e: Exception) {
            view.onSaveFailure("Error loading deck: ${e.message}")
        }
    }

    /**
     * Saves the updated deck details, including its name and words.
     * If the deck name is blank, it uses the existing deck name or defaults to "Untitled Deck".
     * The updated information is saved to the database, and the result is communicated to the view.
     *
     * @param deckId The ID of the deck to be saved.
     * @param deckName The new name of the deck.
     * @param words A list of words to be updated for the deck.
     */
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

    /**
     * Deletes the deck from the database using the deck ID.
     * If the deck is successfully deleted, the view is notified. If the deletion fails, the view
     * is notified of the failure with an error message.
     *
     * @param deckId The ID of the deck to be deleted.
     */
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