package com.example.deckcycle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R

/**
 * Adapter class for displaying a list of statistics in a RecyclerView.
 * The adapter binds each stat (a pair of name and value) to a corresponding view in the RecyclerView.
 *
 * @param stats A list of pairs, where each pair contains a stat name and its corresponding value.
 */
class StatsAdapter(private val stats: List<Pair<String, String>>) : RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    /**
     * ViewHolder class that represents each item (stat) in the RecyclerView.
     * It holds references to the UI elements for displaying the stat name and value.
     *
     * @param view The view representing a single item in the RecyclerView (a stat).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statName: TextView = view.findViewById(R.id.stat_name)  // The name of the stat
        val statValue: TextView = view.findViewById(R.id.stat_value)  // The value of the stat
    }

    /**
     * Called when a new ViewHolder is created. It inflates the item layout and returns a ViewHolder.
     *
     * @param parent The parent ViewGroup that will hold the item views.
     * @param viewType The type of the view (not used in this case).
     * @return A ViewHolder that holds references to the stat's UI elements.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout and return a ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stat, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called to bind the data to a specific ViewHolder.
     * It updates the stat name and value for the given position in the list.
     *
     * @param holder The ViewHolder that holds the UI elements for the item.
     * @param position The position of the item in the stats list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, value) = stats[position]
        holder.statName.text = name  // Set the stat name text
        holder.statValue.text = value  // Set the stat value text
    }

    /**
     * Returns the total number of items (stats) in the list.
     *
     * @return The size of the stats list.
     */
    override fun getItemCount() = stats.size
}
