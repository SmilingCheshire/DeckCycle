package com.example.deckcycle.presenter

import android.content.Context
import android.widget.Toast
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.view.FlipMode

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

    init {
        loadWordsFromDatabase()
    }

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

    fun repeatWord() {
        if (currentWordIndex == -1) return

        val currentWord = wordPairs[currentWordIndex].first
        wordFrequency[currentWord] = (wordFrequency[currentWord] ?: 1) + 4
        databaseHelper.incrementWordsRepeated(deckId)
    }

    fun flipPair() {
        if (currentWordIndex == -1) return

        // Toggle between showing word1 and word2
        showingWord = !showingWord
        updateView()
        databaseHelper.incrementWordsFlipped(deckId)
    }

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
