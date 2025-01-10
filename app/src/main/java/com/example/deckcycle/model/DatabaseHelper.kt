package com.example.deckcycle.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "deckcycle.db"
        private const val DATABASE_VERSION = 1

        // Table names
        const val TABLE_DECK = "Deck"
        const val TABLE_WORD = "Word"

        // Columns for Deck table
        const val COLUMN_DECK_ID = "id"
        const val COLUMN_DECK_NAME = "name"

        // Columns for Word table
        const val COLUMN_WORD_ID = "id"
        const val COLUMN_WORD_DECK_ID = "deck_id"
        const val COLUMN_WORD_WORD1 = "word1"
        const val COLUMN_WORD_WORD2 = "word2"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createDeckTable = """
            CREATE TABLE $TABLE_DECK (
                $COLUMN_DECK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DECK_NAME TEXT NOT NULL
            )
        """
        val createWordTable = """
            CREATE TABLE $TABLE_WORD (
                $COLUMN_WORD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORD_DECK_ID INTEGER NOT NULL,
                $COLUMN_WORD_WORD1 TEXT NOT NULL,
                $COLUMN_WORD_WORD2 TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_WORD_DECK_ID) REFERENCES $TABLE_DECK($COLUMN_DECK_ID) ON DELETE CASCADE
            )
        """
        db.execSQL(createDeckTable)
        db.execSQL(createWordTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle schema changes if required
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DECK")
        onCreate(db)
    }

    // Inserts a new deck and returns the ID of the new deck
    fun insertDeck(deckName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DECK_NAME, deckName)
        }
        return db.insert(TABLE_DECK, null, values)
    }

    // Inserts a new word into a deck
    fun insertWord(deckId: Long, word1: String, word2: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WORD_DECK_ID, deckId)
            put(COLUMN_WORD_WORD1, word1)
            put(COLUMN_WORD_WORD2, word2)
        }
        return db.insert(TABLE_WORD, null, values)
    }

    // Fetches all decks with their IDs and names
    fun getAllDecks(): List<Pair<Long, String>> {
        val db = readableDatabase
        val cursor = db.query(TABLE_DECK, null, null, null, null, null, null)
        val decks = mutableListOf<Pair<Long, String>>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_DECK_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_DECK_NAME))
                decks.add(id to name)
            }
            close()
        }
        return decks
    }
    // Fetches all words in a specific deck
    fun getWordsInDeck(deckId: Long): List<Pair<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_WORD,
            arrayOf(COLUMN_WORD_WORD1, COLUMN_WORD_WORD2),
            "$COLUMN_WORD_DECK_ID = ?",
            arrayOf(deckId.toString()),
            null,
            null,
            null
        )
        val words = mutableListOf<Pair<String, String>>()
        with(cursor) {
            while (moveToNext()) {
                val word1 = getString(getColumnIndexOrThrow(COLUMN_WORD_WORD1))
                val word2 = getString(getColumnIndexOrThrow(COLUMN_WORD_WORD2))
                words.add(word1 to word2)
            }
            close()
        }
        return words
    }

    // Updates words in a specific deck
    fun updateDeckWords(deckId: Long, words: List<Pair<String, String>>) {
        val db = writableDatabase
        db.delete(TABLE_WORD, "$COLUMN_WORD_DECK_ID = ?", arrayOf(deckId.toString()))

        words.forEach { (word1, word2) ->
            val values = ContentValues().apply {
                put(COLUMN_WORD_DECK_ID, deckId)
                put(COLUMN_WORD_WORD1, word1)
                put(COLUMN_WORD_WORD2, word2)
            }
            db.insert(TABLE_WORD, null, values)
        }
    }

    fun updateDeckName(deckId: Long, newName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DECK_NAME, newName)
        }
        return db.update(TABLE_DECK, values, "$COLUMN_DECK_ID = ?", arrayOf(deckId.toString())) > 0
    }

    fun deleteDeck(deckId: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_DECK, "$COLUMN_DECK_ID = ?", arrayOf(deckId.toString())) > 0
    }

    fun getDeckName(deckId: Long): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_DECK,
            arrayOf(COLUMN_DECK_NAME),
            "$COLUMN_DECK_ID = ?",
            arrayOf(deckId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DECK_NAME))
            cursor.close()
            name
        } else {
            cursor.close()
            null
        }
    }

}
