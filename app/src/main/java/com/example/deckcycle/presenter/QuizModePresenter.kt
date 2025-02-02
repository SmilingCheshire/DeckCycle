package com.example.deckcycle.presenter

import android.content.Intent
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.view.Lobby
import com.example.deckcycle.view.QuizMode

class QuizModePresenter(private val view: QuizMode, private val db: DatabaseHelper) {

    private var deckId: Long = -1
    private val availableWords = mutableListOf<Pair<String, String>>()
    private val usedWords = mutableSetOf<String>()
    private var currentWord: Pair<String, String>? = null
    private val startTime = System.currentTimeMillis()

    // Counters for correct and wrong answers
    private var correctAnswersCount = 0
    private var wrongAnswersCount = 0

    // List of incorrectly answered words with correct pairs
    private val wrongWordsList = mutableListOf<Pair<String, String>>()

    fun startQuiz(deckId: Long) {
        this.deckId = deckId
        availableWords.addAll(db.getWordsInDeck(deckId)) // Load all words from the deck
        // Record last studied
        db.updateLastStudied(deckId)
        db.incrementStudySession(deckId)
        loadNextWord()
    }

    fun loadNextWord() {
        if (availableWords.isEmpty()) {
            view.showEndOfQuizMessage(correctAnswersCount, wrongAnswersCount, wrongWordsList)
            return
        }

        // Filter out words already used as `word1`
        val remainingWords = availableWords.filter { it.first !in usedWords }
        if (remainingWords.isEmpty()) {
            view.showEndOfQuizMessage(correctAnswersCount, wrongAnswersCount, wrongWordsList)
            return
        }

        // Randomly select a word
        currentWord = remainingWords.random()
        currentWord?.let { word ->
            usedWords.add(word.first) // Mark word1 as used
            val correctOption = word.second

            // Create shuffled options
            val allOptions = availableWords.map { it.second }.distinct().shuffled()
            val options = (allOptions - correctOption).take(3).toMutableList()
            options.add(correctOption)
            options.shuffle()

            view.displayWord(word.first, options)
        }
    }

    fun onOptionSelected(selectedOption: String) {
        currentWord?.let { word ->
            val correctAnswer = word.second
            val isCorrect = selectedOption == correctAnswer
            val selectedButton = view.optionButtons.find { it.text == selectedOption }

            if (selectedButton != null) {
                view.showResult(isCorrect, selectedButton, correctAnswer)
            }

            if (isCorrect) {
                correctAnswersCount++
                availableWords.remove(word)
                db.incrementQuizCorrect(deckId)
                db.updateStats(deckId, true)
            } else {
                wrongAnswersCount++
                wrongWordsList.add(word) // Add to wrong answers list
                db.incrementQuizWrong(deckId)
                db.updateStats(deckId, false)
            }
        }
        val timeSpentMillis  = System.currentTimeMillis() - startTime // Duration in milliseconds
        val timeSpent = timeSpentMillis / (1000 ) // Convert to minutes
        db.updateTimeSpent(deckId, timeSpent)
    }

    fun onHomeClicked() {
        val intent = Intent(view, Lobby::class.java)
        view.startActivity(intent)
    }

}
