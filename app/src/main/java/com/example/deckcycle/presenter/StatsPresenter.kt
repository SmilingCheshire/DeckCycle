package com.example.deckcycle.presenter

import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.model.DeckStats
import com.example.deckcycle.view.StatsActivity

class StatsPresenter(private val view: StatsActivity, private val dbHelper: DatabaseHelper) {

    fun loadDeckStats(deckId: Long) {
        val stats = dbHelper.getDeckStats(deckId)
        val formattedStats = formatStats(stats)
        view.displayStats(formattedStats)
    }

    private fun formatStats(stats: DeckStats): List<Pair<String, String>> {
        return listOf(
            "Study Sessions" to stats.studySessions.toString(),
            "Total Attempts" to stats.totalAttempts.toString(),
            "Correct Answers" to stats.correctAnswers.toString(),
            "Incorrect Answers" to stats.incorrectAnswers.toString(),
            "Words Flipped" to stats.wordsFlipped.toString(),
            "Quiz Correct" to stats.quizCorrect.toString(),
            "Quiz Wrong" to stats.quizWrong.toString(),
            "Words Written Correctly" to stats.wordsWrittenCorrect.toString(),
            "Words Written Wrong" to stats.wordsWrittenWrong.toString(),
            "Accuracy" to calculateAccuracy(stats),
            "Last Studied" to (stats.lastStudied ?: "Never"),
            "Time Spent (min)" to (stats.timeSpent / 60).toString()

        )
    }

    private fun calculateAccuracy(stats: DeckStats): String {
        return if (stats.totalAttempts == 0) "N/A"
        else "%.2f%%".format((stats.correctAnswers.toDouble() / stats.totalAttempts) * 100)
    }


}
