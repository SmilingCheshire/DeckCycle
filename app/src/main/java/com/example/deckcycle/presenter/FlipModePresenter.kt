package com.example.deckcycle.presenter

import android.content.Context
import android.widget.Toast
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.view.FlipMode

/**
 * The FlipModePresenter class is responsible for managing the logic and flow of the Flip Mode in a flashcard application.
 * It handles loading word pairs from the database, determining the next word to display, and tracking word repetition
 * and flipping activities.
 *
 * This presenter works closely with the `FlipMode` view to update the UI and interact with the local database to track
 * study progress.
 *
 * @param deckId The ID of the deck currently being studied.
 * @param context The context for accessing resources and UI elements. This should be an instance of `FlipMode`.
 */
class FlipModePresenter(
    private val deckId: Long,
    private val context: Context
) {
    private val databaseHelper = DatabaseHelper(context)
    private val wordPairs = mutableListOf<Pair<String, String>>() // List of word pairs (word1, word2)
    private val wordFrequency = mutableMapOf<String, Int>() // Map to track word frequency during repetition
    private var currentWordIndex: Int = -1 // Index of the currently displayed word pair
    private var showingWord: Boolean = true // Track whether we are showing the first or second word in the pair
    private val startTime = System.currentTimeMillis() // Track the time since the start of the session

    private val seenWords = mutableSetOf<String>() // Set of words that have been shown already
    private val repeatedWords = mutableSetOf<String>() // Set of words marked for repetition

    private val maxFrequency = 3 // Maximum frequency boost for a word
    private val defaultFrequency = 1 // Default frequency for words

    init {
        loadWordsFromDatabase()
    }

    /**
     * Loads words from the database into the presenter.
     * This method populates `wordPairs` with words and initializes the frequency map.
     * It also increments the study session and updates the last studied timestamp.
     */
    private fun loadWordsFromDatabase() {
        val words = databaseHelper.getWordsInDeck(deckId)
        wordPairs.addAll(words)
        databaseHelper.incrementStudySession(deckId)

        wordPairs.forEach { (word1, _) ->
            wordFrequency[word1] = defaultFrequency
        }

        databaseHelper.updateLastStudied(deckId)
    }

    /**
     * Fetches the next word to be displayed in the flip mode.
     * The method ensures that the next word is either a new word or a word marked for repetition.
     * Once a word is selected, it updates the UI through the `FlipMode` view.
     */
    fun nextWord() {
        if (wordPairs.isEmpty()) {
            (context as FlipMode).updateWord("No words available", 0)
            return
        }

        // Disable buttons temporarily
        (context as FlipMode).apply {
            homeButton.isEnabled = false
            repeatButton.isEnabled = false
            nextButton.isEnabled = false
        }

        try {
            // Filter available words (either not seen yet or marked for repetition)
            val availableWords = wordPairs.filter { it.first !in seenWords || it.first in repeatedWords }

            if (availableWords.isEmpty()) {
                seenWords.clear() // Reset seen words when all have been seen
                nextWord()
                return
            }

            // Select next word based on frequency
            val wordPool = availableWords.flatMap { pair ->
                val frequency = wordFrequency[pair.first] ?: defaultFrequency
                List(frequency) { pair }
            }

            val nextPair = wordPool.random()
            currentWordIndex = wordPairs.indexOf(nextPair)
            showingWord = true
            seenWords.add(nextPair.first) // Mark word as seen
            updateView()
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading next word: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            // Re-enable buttons after operation is complete
            (context as FlipMode).apply {
                homeButton.isEnabled = true
                repeatButton.isEnabled = true
                nextButton.isEnabled = true
            }
        }
    }

    /**
     * Marks the current word for repetition and increases its frequency.
     * The word will be shown more frequently in the future based on its frequency.
     */
    fun repeatWord() {
        if (currentWordIndex == -1) return

        val currentWord = wordPairs[currentWordIndex].first

        // Increase word frequency, but cap it to `maxFrequency`
        val newFrequency = (wordFrequency[currentWord] ?: defaultFrequency) + 2
        wordFrequency[currentWord] = newFrequency.coerceAtMost(maxFrequency)

        repeatedWords.add(currentWord) // Mark word for repetition
        databaseHelper.incrementWordsRepeated(deckId)
    }

    /**
     * Flips the current word pair (shows either the word or its translation).
     * This method toggles between showing the first and second word in the pair.
     */
    fun flipPair() {
        if (currentWordIndex == -1) return

        showingWord = !showingWord
        updateView()
        databaseHelper.incrementWordsFlipped(deckId)
    }

    /**
     * Updates the view with the current word or translation.
     * It also tracks the time spent in the current session and updates it in the database.
     */
    private fun updateView() {
        if (currentWordIndex == -1) return

        val pair = wordPairs[currentWordIndex]
        val wordToShow = if (showingWord) pair.first else pair.second

        (context as FlipMode).updateWord(wordToShow, 0) // Update the UI with the current word
        val timeSpentMillis = System.currentTimeMillis() - startTime
        val timeSpent = timeSpentMillis / 1000
        databaseHelper.updateTimeSpent(deckId, timeSpent) // Update the time spent in the session
    }
}