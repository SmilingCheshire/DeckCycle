package com.example.deckcycle.presenter

import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.model.DeckStats
import com.example.deckcycle.view.StatsActivity
/**
 * Presenter responsible for managing the statistics of a deck.
 * It fetches the deck statistics from the database and formats them to display on the Stats screen.
 */
class StatsPresenter(private val view: StatsActivity, private val dbHelper: DatabaseHelper) {

    /**
     * Loads the statistics for a specific deck using its `deckId` from the database.
     * The data is formatted and passed to the view for display.
     *
     * @param deckId The unique identifier for the deck whose stats are being loaded.
     */
    fun loadDeckStats(deckId: Long) {
        val stats = dbHelper.getDeckStats(deckId)  // Fetches the stats from the database
        val formattedStats = formatStats(stats)  // Formats the stats into a displayable format
        view.displayStats(formattedStats)  // Passes the formatted stats to the view for display
    }

    /**
     * Formats the raw deck statistics into a list of key-value pairs for display.
     *
     * @param stats The deck statistics to format.
     * @return A list of key-value pairs where the key is the stat's name and the value is the stat's value as a string.
     */
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

    /**
     * Calculates the accuracy of the deck based on the total attempts and correct answers.
     * If no attempts have been made, returns "N/A".
     *
     * @param stats The deck statistics containing total attempts and correct answers.
     * @return A string representing the accuracy percentage (e.g., "85.45%"), or "N/A" if no attempts were made.
     */
    private fun calculateAccuracy(stats: DeckStats): String {
        return if (stats.totalAttempts == 0) "N/A"
        else "%.2f%%".format((stats.correctAnswers.toDouble() / stats.totalAttempts) * 100)
    }
}
