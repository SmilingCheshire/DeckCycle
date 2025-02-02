package com.example.deckcycle.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class for managing local database operations related to deck management.
 * This class extends [SQLiteOpenHelper] and provides methods to handle CRUD operations
 * for decks, words, and study statistics.
 *
 * @param context The application context used to initialize the database helper.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        /** Name of the database. */
        private const val DATABASE_NAME = "deckcycle.db"
        /** Version of the database, used for migrations. */
        private const val DATABASE_VERSION = 3

        /** Table name for storing deck information. */
        const val TABLE_DECK = "Deck"
        const val TABLE_WORD = "Word"
        const val TABLE_STATS = "DeckStats"

        /** Columns for Deck table */
        const val COLUMN_DECK_ID = "id"
        const val COLUMN_DECK_NAME = "name"

        /** Columns for Word table */
        const val COLUMN_WORD_ID = "id"
        const val COLUMN_WORD_DECK_ID = "deck_id"
        const val COLUMN_WORD_WORD1 = "word1"
        const val COLUMN_WORD_WORD2 = "word2"

        /** Columns for Stats table */
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

    /**
     * Called when the database is created for the first time.
     * This method initializes the database schema by creating necessary tables.
     *
     * @param db The SQLite database instance where tables will be created.
     */
    override fun onCreate(db: SQLiteDatabase) {
        /**
         * SQL statement to create the Deck table.
         * This table stores information about different decks.
         *
         * Columns:
         * - COLUMN_DECK_ID: Unique identifier for each deck (Primary Key, Auto Incremented).
         * - COLUMN_DECK_NAME: Name of the deck (Non-null).
         */
        val createDeckTable = """
            CREATE TABLE $TABLE_DECK (
                $COLUMN_DECK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DECK_NAME TEXT NOT NULL
            )
        """

        /**
         * SQL statement to create the Word table.
         * This table stores words associated with different decks.
         *
         * Columns:
         * - COLUMN_WORD_ID: Unique identifier for each word (Primary Key, Auto Incremented).
         * - COLUMN_WORD_DECK_ID: Foreign key referencing the Deck table.
         * - COLUMN_WORD_WORD1: First word in the pair (Non-null).
         * - COLUMN_WORD_WORD2: Second word in the pair (Non-null).
         *
         * Foreign Keys:
         * - COLUMN_WORD_DECK_ID references COLUMN_DECK_ID in TABLE_DECK.
         * - ON DELETE CASCADE ensures words are deleted when the associated deck is deleted.
         */
        val createWordTable = """
            CREATE TABLE $TABLE_WORD (
                $COLUMN_WORD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORD_DECK_ID INTEGER NOT NULL,
                $COLUMN_WORD_WORD1 TEXT NOT NULL,
                $COLUMN_WORD_WORD2 TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_WORD_DECK_ID) REFERENCES $TABLE_DECK($COLUMN_DECK_ID) ON DELETE CASCADE
            )
        """

        /**
         * SQL statement to create the Stats table.
         * This table stores statistics related to each deck.
         *
         * Columns:
         * - COLUMN_STATS_DECK_ID: Primary key referencing the Deck table.
         * - COLUMN_STATS_STUDY_SESSIONS: Number of times the deck was studied (Default: 0).
         * - COLUMN_STATS_TOTAL_ATTEMPTS: Total number of attempts made (Default: 0).
         * - COLUMN_STATS_CORRECT_ANSWERS: Number of correct answers (Default: 0).
         * - COLUMN_STATS_INCORRECT_ANSWERS: Number of incorrect answers (Default: 0).
         * - COLUMN_STATS_LAST_STUDIED: Timestamp of the last study session.
         * - COLUMN_STATS_TIME_SPENT: Total time spent studying (Default: 0).
         * - COLUMN_STATS_WORDS_FLIPPED: Number of words flipped (Default: 0).
         * - COLUMN_STATS_WORDS_REPEATED: Number of words repeated (Default: 0).
         * - COLUMN_STATS_QUIZ_CORRECT: Number of correct quiz answers (Default: 0).
         * - COLUMN_STATS_QUIZ_WRONG: Number of incorrect quiz answers (Default: 0).
         * - COLUMN_STATS_WRITTEN_CORRECT: Number of correct written answers (Default: 0).
         * - COLUMN_STATS_WRITTEN_WRONG: Number of incorrect written answers (Default: 0).
         *
         * Foreign Keys:
         * - COLUMN_STATS_DECK_ID references COLUMN_DECK_ID in TABLE_DECK.
         * - ON DELETE CASCADE ensures stats are removed when the associated deck is deleted.
         */
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

        // Execute SQL statements to create tables
        db.execSQL(createDeckTable)
        db.execSQL(createWordTable)
        db.execSQL(createStatsTable)
    }

    /**
     * Upgrades the database schema by dropping and recreating the necessary tables.
     * This method is called when the database version is incremented.
     *
     * @param db The SQLiteDatabase instance to apply the upgrade to.
     * @param oldVersion The old version number of the database.
     * @param newVersion The new version number of the database.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle schema changes if required
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DECK")
        onCreate(db)
    }

    /**
     * Inserts a new deck into the database.
     *
     * @param deckName The name of the deck to be inserted.
     * @return The ID of the inserted deck.
     */
    fun insertDeck(deckName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DECK_NAME, deckName)
        }
        return db.insert(TABLE_DECK, null, values)
    }

    /**
     * Inserts a new word into a specific deck.
     *
     * @param deckId The ID of the deck to insert the word into.
     * @param word1 The first word in the word pair.
     * @param word2 The second word in the word pair.
     * @return The ID of the inserted word.
     */
    fun insertWord(deckId: Long, word1: String, word2: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WORD_DECK_ID, deckId)
            put(COLUMN_WORD_WORD1, word1)
            put(COLUMN_WORD_WORD2, word2)
        }
        return db.insert(TABLE_WORD, null, values)
    }

    /**
     * Retrieves all decks from the database.
     * This method queries the `TABLE_DECK` and returns a list of pairs where each pair
     * contains the deck ID and the deck's name.
     *
     * @return A list of pairs containing deck IDs and names.
     */
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

    /**
     * Fetches all words in a specific deck.
     * This method queries the `TABLE_WORD` to retrieve words associated with the given deck ID.
     *
     * @param deckId The ID of the deck to fetch words for.
     * @return A list of pairs where each pair contains two words (word1 and word2).
     */
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

    /**
     * Updates the words in a specific deck by deleting existing words and inserting new ones.
     * This method first deletes all words associated with the deck ID and then inserts the new words.
     *
     * @param deckId The ID of the deck whose words are to be updated.
     * @param words A list of pairs containing the new words (word1 and word2) to be inserted.
     */
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

    /**
     * Updates the name of a specific deck.
     *
     * @param deckId The ID of the deck to update.
     * @param newName The new name to assign to the deck.
     * @return True if the update was successful, false otherwise.
     */
    fun updateDeckName(deckId: Long, newName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DECK_NAME, newName)
        }
        return db.update(TABLE_DECK, values, "$COLUMN_DECK_ID = ?", arrayOf(deckId.toString())) > 0
    }

    /**
     * Deletes a specific deck from the database.
     *
     * @param deckId The ID of the deck to delete.
     * @return True if the deck was successfully deleted, false otherwise.
     */
    fun deleteDeck(deckId: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_DECK, "$COLUMN_DECK_ID = ?", arrayOf(deckId.toString())) > 0
    }

    /**
     * Retrieves the name of a specific deck.
     *
     * @param deckId The ID of the deck to retrieve the name for.
     * @return The name of the deck, or null if the deck does not exist.
     */
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

    /**
     * Retrieves the next available deck ID.
     * This method queries the database to find the current maximum deck ID and increments it by 1.
     * If no decks exist, it defaults to 1.
     *
     * @return The next available deck ID.
     */
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

    /**
     * Ensures that there is an entry in the stats table for the given deck ID.
     * If no entry exists, it inserts a new one.
     *
     * @param deckId The ID of the deck for which to ensure a stats entry.
     */
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

    /**
     * Increments the study session count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `study_sessions` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the study sessions.
     */
    fun incrementStudySession(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_STUDY_SESSIONS = $COLUMN_STATS_STUDY_SESSIONS + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }

    /**
     * Updates the stats for a deck, incrementing either the correct or incorrect answer count.
     * This method also increments the total attempts count.
     *
     * @param deckId The ID of the deck for which to update stats.
     * @param isCorrect Boolean indicating if the answer was correct or not.
     *                  True increments correct answers, false increments incorrect answers.
     */
    fun updateStats(deckId: Long, isCorrect: Boolean) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        val correctCol = if (isCorrect) COLUMN_STATS_CORRECT_ANSWERS else COLUMN_STATS_INCORRECT_ANSWERS
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_TOTAL_ATTEMPTS = $COLUMN_STATS_TOTAL_ATTEMPTS + 1, $correctCol = $correctCol + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Updates the time spent for a specific deck by adding the given time.
     *
     * @param deckId The ID of the deck to update.
     * @param timeSpent The amount of time to add to the deck's time spent.
     */
    fun updateTimeSpent(deckId: Long, timeSpent: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_TIME_SPENT = $COLUMN_STATS_TIME_SPENT + $timeSpent WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Updates the last studied date for the given deck ID.
     * The date is stored in the format "yyyy-MM-dd".
     *
     * @param deckId The ID of the deck to update.
     */
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
    /**
     * Retrieves the stats for a specific deck.
     * This method queries the database for the stats associated with the given deck ID and returns a `DeckStats` object.
     * If no stats exist, a default `DeckStats` object is returned.
     *
     * @param deckId The ID of the deck to retrieve stats for.
     * @return A `DeckStats` object containing the deck's stats.
     */
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
    /**
     * Increments the words flipped count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `words_flipped` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the words flipped.
     */
    fun incrementWordsFlipped(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WORDS_FLIPPED = $COLUMN_STATS_WORDS_FLIPPED + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Increments the words repeated count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `words_repeated` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the words repeated.
     */
    fun incrementWordsRepeated(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WORDS_REPEATED = $COLUMN_STATS_WORDS_REPEATED + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Increments the quiz correct count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `quiz_correct` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the quiz correct answers.
     */
    fun incrementQuizCorrect(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_QUIZ_CORRECT = $COLUMN_STATS_QUIZ_CORRECT + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Increments the quiz wrong count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `quiz_wrong` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the quiz wrong answers.
     */
    fun incrementQuizWrong(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_QUIZ_WRONG = $COLUMN_STATS_QUIZ_WRONG + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Increments the written correct count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `written_correct` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the written correct answers.
     */
    fun incrementWrittenCorrect(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WRITTEN_CORRECT = $COLUMN_STATS_WRITTEN_CORRECT + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }
    /**
     * Increments the written wrong count for the given deck ID.
     * This method ensures a stats entry exists and then increments the `written_wrong` count in the database.
     *
     * @param deckId The ID of the deck for which to increment the written wrong answers.
     */
    fun incrementWrittenWrong(deckId: Long) {
        ensureStatsEntry(deckId)
        val db = writableDatabase
        db.execSQL("UPDATE $TABLE_STATS SET $COLUMN_STATS_WRITTEN_WRONG = $COLUMN_STATS_WRITTEN_WRONG + 1 WHERE $COLUMN_STATS_DECK_ID = $deckId")
    }


}
/**
 * Data class representing the statistics for a specific deck.
 * Contains various metrics such as the number of study sessions, total attempts,
 * correct and incorrect answers, and time spent.
 */
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

