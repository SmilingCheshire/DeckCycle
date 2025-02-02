package com.example.deckcycle.util

/**
 * Interface that defines the contract for displaying statistics in the view layer.
 * Any class implementing this interface will be responsible for displaying the statistics data to the user.
 */
interface StatsView {

    /**
     * Displays the statistics in the view.
     *
     * @param stats A list of pairs where each pair contains a stat name (String) and its corresponding value (String).
     * The data will be shown in the UI, typically in a list or other relevant UI component.
     */
    fun displayStats(stats: List<Pair<String, String>>)
}

