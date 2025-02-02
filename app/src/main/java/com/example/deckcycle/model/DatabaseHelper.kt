package com.example.deckcycle.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "deckcycle.db"
        private const val DATABASE_VERSION = 3

        // Table names
        const val TABLE_DECK = "Deck"
        const val TABLE_WORD = "Word"
        const val TABLE_STATS = "DeckStats"

        // Columns for Deck table
        const val COLUMN_DECK_ID = "id"
        const val COLUMN_DECK_NAME = "name"

        // Columns for Word table
        const val COLUMN_WORD_ID = "id"
        const val COLUMN_WORD_DECK_ID = "deck_id"
        const val COLUMN_WORD_WORD1 = "word1"
        const val COLUMN_WORD_WORD2 = "word2"

        // Columns for Stats table
        const val COLUMN_STATS_DECK_ID = "deck_id"
        const val COLUMN_STATS_STUDY_SESSIONS = "study_sessions"
        const val COLUMN_STATS_TOTAL_ATTEMPTS = "total_attempts"
        const val COLUMN_STATS_CORRECT_ANSWERS = "correct_answers"
        const val COLUMN_STATS_INCORRECT_ANSWERS = "incorrect_answers"
        const val COLUMN_STATS_LAST_STUDIED = "last_studied"
        const val COLUMN_STATS_TIME_SPENT = "time_spent"
        const val COLUMN_STATS_WORDS_FLIPPED = "words_flipped"
        const val COLUMN_STATS_WORDS_REPEATED = "words_repeated"
        const val COLUMN_STATS_QUIZ_CORRECT = "quiz_correct"
        const val COLUMN_STATS_QUIZ_WRONG = "quiz_wrong"
        const val COLUMN_STATS_WRITTEN_CORRECT = "words_written_correctly"
        const val COLUMN_STATS_WRITTEN_WRONG = "words_written_wrong"
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
        val createStatsTable = """
            CREATE TABLE $TABLE_STATS (
                $COLUMN_STATS_DECK_ID INTEGER PRIMARY KEY,
                $COLUMN_STATS_STUDY_SESSIONS INTEGER DEFAULT 0,
                $COLUMN_STATS_TOTAL_ATTEMPTS INTEGER DEFAULT 0,
                $COLUMN_STATS_CORRECT_ANSWERS INTEGER DEFAULT 0,
                $COLUMN_STATS_INCORRECT_ANSWERS INTEGER DEFAULT 0,
                $COLUMN_STATS_LAST_STUDIED TEXT,
                $COLUMN_STATS_TIME_SPENT INTEGER DEFAULT 0,
                $COLUMN_STATS_WORDS_FLIPPED INTEGER DEFAULT 0,
                $COLUMN_STATS_WORDS_REPEATED INTEGER DEFAULT 0,
                $COLUMN_STATS_QUIZ_CORRECT INTEGER DEFAULT 0,
                $COLUMN_STATS_QUIZ_WRONG INTEGER DEFAULT 0,
                $COLUMN_STATS_WRITTEN_CORRECT INTEGER DEFAULT 0,
                $COLUMN_STATS_WRITTEN_WRONG INTEGER DEFAULT 0,
                FOREIGN KEY ($COLUMN_STATS_DECK_ID) REFERENCES $TABLE_DECK($COLUMN_DECK_ID) ON DELETE CASCADE
            )
        """
        db.execSQL(createDeckTable)
        db.execSQL(createWordTable)
        db.execSQL(createStatsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle schema changes if required
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
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


    fun getNextDeckId(): Long {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT MAX($COLUMN_DECK_ID) FROM $TABLE_DECK", null)
        var nextId = 1L  // Default if no decks exist
        if (cursor.moveToFirst()) {
            nextId = cursor.getLong(0) + 1
        }
        cursor.close()
        return nextId
    }

    // Ensure a deck has a stats entry
    private fun ensureStatsEntry(deckId: Long) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_STATS WHERE $COLUMN_STATS_DECK_ID = ?", arrayOf(deckId.toString()))
        if (!cursor.moveToFirst()) {
            val values = ContentValues().apply {
                put(COLUMN_STATS_DECK_ID, deckId)
            }
            db.insert(TABLE_STATS, null, values)
        }
        cursor.close()
    }

    fun incrementStudySession(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_STUDY_SESSIONS = $COLUMN_STATS_STUDY_SESSIONS + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun updateStats(deckId: Long, isCorrect: Boolean) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        val correctCol = if (isCorrect) COLUMN_STATS_CORRECT_ANSWERS else COLUMN_STATS_INCORRECT_ANSWERS
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_TOTAL_ATTEMPTS = $COLUMN_STATS_TOTAL_ATTEMPTS + 1, $correctCol = $correctCol + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun updateTimeSpent(deckId: Long, timeSpent: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_TIME_SPENT = $COLUMN_STATS_TIME_SPENT + $timeSpent WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun updateLastStudied(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Only date displayed
        val dateStr = dateFormat.format(Date())
        val values = ContentValues().apply {
            put(COLUMN_STATS_LAST_STUDIED, dateStr)
        }
        db.update(TABLE_STATS, values, "$COLUMN_STATS_DECK_ID = ?", arrayOf(deckId.toString()))
    }

    fun getDeckStats(deckId: Long): DeckStats {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_STATS WHERE $COLUMN_STATS_DECK_ID = ?", arrayOf(deckId.toString()))
        return if (cursor.moveToFirst()) {
            val stats = DeckStats(
                studySessions = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_STUDY_SESSIONS)),
                totalAttempts = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_TOTAL_ATTEMPTS)),
                correctAnswers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_CORRECT_ANSWERS)),
                incorrectAnswers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_INCORRECT_ANSWERS)),
                lastStudied = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATS_LAST_STUDIED)),
                timeSpent = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STATS_TIME_SPENT)),
                wordsFlipped = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_WORDS_FLIPPED)),
                quizCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_QUIZ_CORRECT)),
                quizWrong = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATS_QUIZ_WRONG)),
                wordsWrittenCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_STATS_WRITTEN_CORRECT)),
                wordsWrittenWrong = cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_STATS_WRITTEN_WRONG)),
            )
            cursor.close()
            stats
        } else {
            cursor.close()
            DeckStats()
        }
    }

    fun incrementWordsFlipped(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WORDS_FLIPPED = $COLUMN_STATS_WORDS_FLIPPED + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun incrementWordsRepeated(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WORDS_REPEATED = $COLUMN_STATS_WORDS_REPEATED + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun incrementQuizCorrect(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_QUIZ_CORRECT = $COLUMN_STATS_QUIZ_CORRECT + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun incrementQuizWrong(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_QUIZ_WRONG = $COLUMN_STATS_QUIZ_WRONG + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun incrementWrittenCorrect(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WRITTEN_CORRECT = $COLUMN_STATS_WRITTEN_CORRECT + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    fun incrementWrittenWrong(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WRITTEN_WRONG = $COLUMN_STATS_WRITTEN_WRONG + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }


}

data class DeckStats(
    val studySessions: Int = 0,
    val totalAttempts: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val lastStudied: String? = null,
    val timeSpent: Long = 0,
    val wordsFlipped: Int = 0,
    val quizCorrect  : Int = 0,
    val quizWrong  : Int = 0,
    val wordsWrittenCorrect : Int = 0,
    val wordsWrittenWrong : Int = 0,
)

