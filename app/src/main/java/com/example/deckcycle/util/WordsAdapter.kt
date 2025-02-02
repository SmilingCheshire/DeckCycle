package com.example.deckcycle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R

class WordsAdapter(
    private var words: List<Pair<String, String>>,
) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word1TextView: TextView = view.findViewById(R.id.word1TextView)
        val word2TextView: TextView = view.findViewById(R.id.word2TextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val (word1, word2) = words[position]
        holder.word1TextView.text = word1
        holder.word2TextView.text = word2
    }

    override fun getItemCount(): Int = words.size

    fun updateWords(newWords: List<Pair<String, String>>) {
        words = newWords
        notifyDataSetChanged()
    }

}
