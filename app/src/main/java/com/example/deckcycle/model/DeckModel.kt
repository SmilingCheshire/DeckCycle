package com.example.deckcycle.model

class DeckModel {
    private val decks = mutableListOf<String>()

    fun addDeck(name: String) {
        decks.add(name)
    }

    fun getDecks(): List<String> = decks
}
