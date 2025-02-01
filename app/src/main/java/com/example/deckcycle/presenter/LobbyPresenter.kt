package com.example.deckcycle.presenter

import android.content.Context
import com.example.deckcycle.model.DatabaseHelper

class LobbyPresenter(private val context: Context, private val view: LobbyView) {
    private val dbHelper = DatabaseHelper(context)

    fun loadDecks() {
        val decks = dbHelper.getAllDecks().map { it.second }
        view.refreshDeckList(decks)
    }
}


interface LobbyView {
    fun refreshDeckList(decks: List<String>)
}