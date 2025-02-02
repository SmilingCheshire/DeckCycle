package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.presenter.SyncPresenter
import com.example.deckcycle.util.WordsAdapter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * The Sync activity manages the synchronization of words between the local deck and
 * the cloud. It allows the user to upload, retrieve, and delete decks from the cloud,
 * as well as view the words in the local deck and the cloud deck. It uses a `SyncPresenter`
 * to handle synchronization logic and a `WordsAdapter` to display the words in `RecyclerView`s.
 *
 * This activity provides options to:
 * - View local and cloud words.
 * - Upload the deck to the cloud.
 * - Retrieve the deck from the cloud.
 * - Delete the deck locally and from the cloud.
 * - Navigate to the lobby.
 */
class Sync : AppCompatActivity() {
    private lateinit var syncPresenter: SyncPresenter
    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private var deckId: Long? = null // Store the received deckId

    private lateinit var cloudRecyclerView: RecyclerView
    private lateinit var cloudWordsAdapter: WordsAdapter

    /**
     * Called when the activity is created. This method initializes the presenter,
     * sets up the `RecyclerView`s for local and cloud words, and handles button
     * clicks for deleting, uploading, retrieving decks, and navigating to the lobby.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        // Retrieve the deckId from the Intent and handle invalid deckId
        deckId = intent.getLongExtra("deckId", -1)
        if (deckId == -1L) {
            finish() // Exit if deckId is invalid
            return
        }

        // Initialize RecyclerViews and their adapters
        recyclerView = findViewById(R.id.localDeckWordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        databaseHelper = DatabaseHelper(this)
        syncPresenter = SyncPresenter(this)

        cloudRecyclerView = findViewById(R.id.cloudDeckWordsRecyclerView)
        cloudRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load local and cloud words
        loadWords()
        loadCloudWords()

        // Set up button click listeners
        findViewById<Button>(R.id.deleteDeckButton).setOnClickListener {
            deckId?.let {
                syncPresenter.deleteDeckLocally(it)
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
            deckId?.let { deckId ->
                syncPresenter.retrieveDeckFromCloud(deckId) {
                    // Once data is retrieved and updated, show the success message and navigate
                    runOnUiThread {
                        showMessage("Data Successfully Retrieved!")  // Show success message
                        startActivity(Intent(this, Lobby::class.java))  // Navigate to Lobby
                        finish()  // Close current activity
                    }
                }
            }
        }


        findViewById<Button>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, Lobby::class.java))
            finish()
        }
    }

    /**
     * Displays a short Toast message to the user.
     *
     * This function shows a brief message to the user using a Toast. The message appears on the screen for
     * a short period and then disappears.
     *
     * @param message The message to be displayed to the user in the Toast.
     */
    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    /**
     * Loads the local words for the current deck and displays them in the local `RecyclerView`.
     */
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

    /**
     * Loads the words from the cloud for the current deck and displays them in the cloud `RecyclerView`.
     * The cloud words are retrieved from Firestore based on the current user and deck.
     */
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
