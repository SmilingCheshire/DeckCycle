package com.example.deckcycle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R

class WordsAdapter(
    private var words: List<Pair<String, String>> = listOf()
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

    /**
     * Uses DiffUtil to update words efficiently.
     */
    fun updateWords(newWords: List<Pair<String, String>>) {
        val diffCallback = WordsDiffCallback(words, newWords)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        words = newWords
        diffResult.dispatchUpdatesTo(this) // Efficiently updates RecyclerView
    }

    /**
     * DiffUtil callback for calculating the difference between old and new word lists.
     */
    class WordsDiffCallback(
        private val oldList: List<Pair<String, String>>,
        private val newList: List<Pair<String, String>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition] // Compares items
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition] // Compares content
        }
    }
}