package com.example.deckcycle.presenter

import android.content.Context
import com.example.deckcycle.model.DatabaseHelper
/**
 * Presenter class responsible for managing the logic of the Lobby screen.
 * It handles the loading of deck data from the database and updating the view accordingly.
 *
 * @param context The context used for database access and interacting with the application environment.
 * @param view The view interface used to refresh the deck list in the Lobby UI.
 */
class LobbyPresenter(private val context: Context, private val view: LobbyView) {
    private val dbHelper = DatabaseHelper(context)
    /**
     * Loads the list of deck names from the database and updates the view to display them.
     * It retrieves all available decks and passes the list of deck names to the view for rendering.
     */
    fun loadDecks() {
        val decks = dbHelper.getAllDecks().map { it.second }
        view.refreshDeckList(decks)
    }
}

/**
 * View interface for the Lobby screen.
 * Provides a method to refresh the list of decks displayed in the UI.
 */
interface LobbyView {
    fun refreshDeckList(decks: List<String>)
}