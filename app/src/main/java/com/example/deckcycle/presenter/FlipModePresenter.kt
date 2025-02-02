package com.example.deckcycle.presenter

import android.content.Context
import android.widget.Toast
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.view.FlipMode

/**
 * Presenter class for managing the logic of flipping words in a deck.
 * The class handles word display, flipping between words, and tracking word frequencies.
 * It also interacts with the database to update word statistics and track time spent.
 *
 * @param deckId The ID of the deck whose words are being managed.
 * @param context The context used to initialize the presenter, which is required for interacting with UI components and the database.
 */
class FlipModePresenter(
    private val deckId: Long,
    private val context: Context
) {
    private val databaseHelper = DatabaseHelper(context)
    private val wordPairs = mutableListOf<Pair<String, String>>() // Pair of words (word1, word2)
    private val wordFrequency = mutableMapOf<String, Int>() // Word to frequency map
    private var currentWordIndex: Int = -1
    private var showingWord: Boolean = true
    private val startTime = System.currentTimeMillis()

    /**
     * Initializes the FlipModePresenter by loading words from the database.
     * It also increments the study session count and records the last studied time.
     */
    init {
        loadWordsFromDatabase()
    }

    /**
     * Loads the words from the database for the given deckId and initializes
     * the word frequency map. Also increments the study session and updates the last studied time.
     */
    private fun loadWordsFromDatabase() {
        // Fetch words for the given deckId
        val words = databaseHelper.getWordsInDeck(deckId)
        wordPairs.addAll(words)
        databaseHelper.incrementStudySession(deckId)

        // Initialize word frequency map
        wordPairs.forEach { (word1, _) ->
            wordFrequency[word1] = 1
        }
        // Record last studied
        databaseHelper.updateLastStudied(deckId)
    }
    /**
     * Moves to the next word in the deck, randomly selecting from the word pool.
     * It ensures buttons are disabled during the operation and re-enables them afterward.
     */
    fun nextWord() {
        if (wordPairs.isEmpty()) {
            (context as FlipMode).updateWord("No words available", 0)
            return
        }

        // Disable buttons to avoid rapid clicks
        (context as FlipMode).apply {
            homeButton.isEnabled = false
            repeatButton.isEnabled = false
            nextButton.isEnabled = false
        }

        // Perform the operation
        try {
            val wordPool = wordPairs.flatMap { pair ->
                List(wordFrequency[pair.first] ?: 1) { pair }
            }
            val nextPair = wordPool.random()
            currentWordIndex = wordPairs.indexOf(nextPair)
            showingWord = true
            updateView()
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading next word: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            // Re-enable buttons after operation
            (context as FlipMode).apply {
                homeButton.isEnabled = true
                repeatButton.isEnabled = true
                nextButton.isEnabled = true
            }
        }

        // Choose a word randomly, with adjusted frequency
        val wordPool = wordPairs.flatMap { pair ->
            List(wordFrequency[pair.first] ?: 1) { pair }
        }

        val nextPair = wordPool.random()
        currentWordIndex = wordPairs.indexOf(nextPair)
        showingWord = true
        updateView()
    }
    /**
     * Repeats the current word by incrementing its frequency.
     * Also updates the database to reflect the number of times the word has been repeated.
     */
    fun repeatWord() {
        if (currentWordIndex == -1) return

        val currentWord = wordPairs[currentWordIndex].first
        wordFrequency[currentWord] = (wordFrequency[currentWord] ?: 1) + 4
        databaseHelper.incrementWordsRepeated(deckId)
    }
    /**
     * Flips the current word pair, toggling between showing the first word and the second word.
     * Updates the UI and increments the count of flipped words in the database.
     */
    fun flipPair() {
        if (currentWordIndex == -1) return

        // Toggle between showing word1 and word2
        showingWord = !showingWord
        updateView()
        databaseHelper.incrementWordsFlipped(deckId)
    }
    /**
     * Updates the view with the current word (either word1 or word2, based on the state of `showingWord`).
     * Also updates the time spent on studying the deck in the database.
     */
    private fun updateView() {
        if (currentWordIndex == -1) return

        val pair = wordPairs[currentWordIndex]
        val wordToShow = if (showingWord) pair.first else pair.second

        // Update only the text while keeping the image as-is
        (context as FlipMode).updateWord(wordToShow, 0) // Pass 0 to avoid changing the image
        val timeSpentMillis  = System.currentTimeMillis() - startTime // Duration in milliseconds
        val timeSpent = timeSpentMillis / (1000 ) // Convert to minutes
        databaseHelper.updateTimeSpent(deckId, timeSpent)
    }
}
