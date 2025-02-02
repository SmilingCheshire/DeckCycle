package com.example.deckcycle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R

class StatsAdapter(private val stats: List<Pair<String, String>>) : RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statName: TextView = view.findViewById(R.id.stat_name)
        val statValue: TextView = view.findViewById(R.id.stat_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, value) = stats[position]
        holder.statName.text = name
        holder.statValue.text = value
    }

    override fun getItemCount() = stats.size
}
