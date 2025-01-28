package com.example.deckcycle.presenter

import android.os.Handler
import com.example.deckcycle.model.DatabaseHelper
import kotlin.random.Random

class WriteModePresenter(private val databaseHelper: DatabaseHelper) {

    private var view: WriteModeView? = null
    private var words = mutableListOf<Pair<String, String>>()
    private var remainingWords = mutableListOf<Pair<String, String>>()
    private var currentIndex = 0

    fun attachView(view: WriteModeView) {
        this.view = view
    }

    fun loadWords(deckId: Long) {
        words = databaseHelper.getWordsInDeck(deckId).toMutableList()
        remainingWords = words.toMutableList()
        if (remainingWords.isNotEmpty()) {
            currentIndex = Random.nextInt(remainingWords.size)
            view?.displayWord(remainingWords[currentIndex].first)
        } else {
            view?.showMessage("No words found in this deck!")
        }
    }

    fun checkAnswer(answer: String) {
        if (remainingWords.isEmpty()) return

        val currentWord = remainingWords[currentIndex]

        if (currentWord.second.equals(answer, ignoreCase = true)) {
            view?.highlightAnswer(true)
            remainingWords.removeAt(currentIndex)

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
            view?.highlightAnswer(false)
            view?.showCorrectAnswer(currentWord.second)
        }
    }

    fun nextWord() {
        if (remainingWords.isNotEmpty()) {
            currentIndex = Random.nextInt(remainingWords.size)
            view?.displayWord(remainingWords[currentIndex].first)
        } else {
            view?.navigateToHome()
        }
    }

    fun skipWord() {
        if (remainingWords.isNotEmpty()) {
            currentIndex = Random.nextInt(remainingWords.size)
            view?.displayWord(remainingWords[currentIndex].first)
        }
    }

    interface WriteModeView {
        fun displayWord(word: String)
        fun highlightAnswer(isCorrect: Boolean)
        fun showCorrectAnswer(correctAnswer: String)
        fun showMessage(message: String)
        fun navigateToHome()
    }
}
