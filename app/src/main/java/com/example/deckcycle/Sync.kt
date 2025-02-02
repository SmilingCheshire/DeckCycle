package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.presenter.SyncPresenter
import com.example.deckcycle.util.WordsAdapter
import com.google.firebase.firestore.FirebaseFirestore

class Sync : AppCompatActivity() {
    private lateinit var syncPresenter: SyncPresenter
    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private var deckId: Long? = null // Store the received deckId

    private lateinit var cloudRecyclerView: RecyclerView
    private lateinit var cloudWordsAdapter: WordsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        // Assign deckId from intent
        deckId = intent.getLongExtra("deckId", -1)
        if (deckId == -1L) {
            finish() // Exit if deckId is invalid
            return
        }

        recyclerView = findViewById(R.id.localDeckWordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        syncPresenter = SyncPresenter(this)

        cloudRecyclerView = findViewById(R.id.cloudDeckWordsRecyclerView)
        cloudRecyclerView.layoutManager = LinearLayoutManager(this)

        loadWords()
        loadCloudWords()

        findViewById<Button>(R.id.deleteDeckButton).setOnClickListener {
            deckId?.let {syncPresenter.deleteDeckLocally(it)
                loadWords() // Refresh RecyclerView after deletion
            }
            deckId?.let { syncPresenter.deleteDeckFromCloud(it) }

            startActivity(Intent(this, Lobby::class.java))
            finish()
        }

        findViewById<Button>(R.id.uploadDeckButton).setOnClickListener {
            deckId?.let { syncPresenter.uploadDeckToCloud(it) }
            loadCloudWords()
            loadWords()
        }

        findViewById<Button>(R.id.retrieveDeckButton).setOnClickListener {
            deckId?.let { syncPresenter.retrieveDeckFromCloud(it) }
            loadCloudWords()
            loadWords()
        }

        findViewById<Button>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, Lobby::class.java))
            finish()
        }
    }

    private fun loadWords() {
        deckId?.let {
            val words = databaseHelper.getWordsInDeck(it)
            if (!::wordsAdapter.isInitialized) {
                wordsAdapter = WordsAdapter(words)
                recyclerView.adapter = wordsAdapter
            } else {
                wordsAdapter.updateWords(words) // Update the list
            }
        }
    }

    private fun loadCloudWords() {
        val userId = syncPresenter.getCurrentUserId() ?: return
        val firestore = FirebaseFirestore.getInstance()

        val deckName = databaseHelper.getDeckName(deckId!!) ?: return

        firestore.collection("users")
            .document(userId)
            .collection("decks")
            .document(deckName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val wordsList = document.get("words") as List<Map<String, String>>
                    val wordPairs = wordsList.map { it["word1"]!! to it["word2"]!! }

                    if (!::cloudWordsAdapter.isInitialized) {
                        cloudWordsAdapter = WordsAdapter(wordPairs)
                        cloudRecyclerView.adapter = cloudWordsAdapter
                    } else {
                        cloudWordsAdapter.updateWords(wordPairs)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CloudDataLoad", "Error loading cloud data", exception)
            }
    }

}
