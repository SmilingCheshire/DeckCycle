package com.example.deckcycle.presenter

import android.os.Handler
import com.example.deckcycle.model.DatabaseHelper
import kotlin.random.Random

/**
 * Presenter responsible for handling the logic of the Write Mode.
 * This mode allows users to write the correct answer for each word in the deck.
 * It keeps track of the words, handles checking answers, moving to the next word, and updating statistics.
 */
class WriteModePresenter(private val databaseHelper: DatabaseHelper) {

    private var view: WriteModeView? = null
    private var words = mutableListOf<Pair<String, String>>()
    private var remainingWords = mutableListOf<Pair<String, String>>()
    private var currentIndex = 0
    private var deckId: Long = -1
    private val startTime = System.currentTimeMillis()

    /**
     * Attaches the view to the presenter, allowing interaction with the UI.
     *
     * @param view The `WriteModeView` instance responsible for UI interactions.
     */
    fun attachView(view: WriteModeView) {
        this.view = view
    }

    /**
     * Loads the words from the given deck and prepares them for the Write Mode session.
     * It initializes the deck and randomizes the order of the words.
     *
     * @param deckId The ID of the deck to load words from.
     */
    fun loadWords(deckId: Long) {
        this.deckId = deckId
        databaseHelper.incrementStudySession(deckId)
        databaseHelper.updateLastStudied(deckId)
        words = databaseHelper.getWordsInDeck(deckId).toMutableList()
        remainingWords = words.toMutableList()

        if (remainingWords.isNotEmpty()) {
            // Randomly select a starting word from remaining words
            currentIndex = Random.nextInt(remainingWords.size)
            view?.displayWord(remainingWords[currentIndex].first)
        } else {
            view?.showMessage("No words found in this deck!")
        }
    }

    /**
     * Checks the user's answer for the current word.
     * If the answer is correct, it removes the word from the remaining list, increments correct answers,
     * updates statistics, and moves to the next word after a delay.
     * If the answer is incorrect, it shows the correct answer and updates the statistics accordingly.
     *
     * @param answer The answer provided by the user.
     */
    fun checkAnswer(answer: String) {
        if (remainingWords.isEmpty()) return

        val currentWord = remainingWords[currentIndex]

        if (currentWord.second.equals(answer, ignoreCase = true)) {
            // Correct answer
            view?.highlightAnswer(true)
            remainingWords.removeAt(currentIndex)
            databaseHelper.incrementWrittenCorrect(deckId)
            databaseHelper.updateStats(deckId, true)

            if (remainingWords.isEmpty()) {
                view?.showMessage("Mode Completed!")
                view?.navigateToHome()
            } else {
                // Move to the next word after a short delay
                Handler().postDelayed({
                    nextWord()
                }, 1000)
            }
        } else {
            // Incorrect answer
            view?.highlightAnswer(false)
            view?.showCorrectAnswer(currentWord.second)
            databaseHelper.incrementWrittenWrong(deckId)
            databaseHelper.updateStats(deckId, false)
        }

        val timeSpentMillis  = System.currentTimeMillis() - startTime // Duration in milliseconds
        val timeSpent = timeSpentMillis / (1000 ) // Convert to minutes
        databaseHelper.updateTimeSpent(deckId, timeSpent)
    }

    /**
     * Loads the next word from the remaining words list.
     * If there are no words left, it navigates to the home screen.
     */
    fun nextWord() {
        if (remainingWords.isNotEmpty()) {
            currentIndex = Random.nextInt(remainingWords.size)
            view?.displayWord(remainingWords[currentIndex].first)
        } else {
            view?.navigateToHome()
        }
    }

    /**
     * Skips the current word and displays a new random word from the remaining words list.
     * If no words are left, it does nothing.
     */
    fun skipWord() {
        if (remainingWords.isNotEmpty()) {
            currentIndex = Random.nextInt(remainingWords.size)
            view?.displayWord(remainingWords[currentIndex].first)
        }
    }

    /**
     * Interface defining the necessary UI methods for interacting with the Write Mode view.
     */
    interface WriteModeView {
        /**
         * Displays the current word to the user for them to write the correct answer.
         *
         * @param word The word to be displayed to the user.
         */
        fun displayWord(word: String)

        /**
         * Highlights the answer based on whether the user was correct or not.
         *
         * @param isCorrect Indicates if the user's answer was correct.
         */
        fun highlightAnswer(isCorrect: Boolean)

        /**
         * Displays the correct answer if the user provided an incorrect response.
         *
         * @param correctAnswer The correct answer for the current word.
         */
        fun showCorrectAnswer(correctAnswer: String)

        /**
         * Shows a message to the user, typically used for feedback or completion messages.
         *
         * @param message The message to be displayed.
         */
        fun showMessage(message: String)

        /**
         * Navigates the user back to the home screen, typically after completing the mode or skipping words.
         */
        fun navigateToHome()
    }

}

