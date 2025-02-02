package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deckcycle.R
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.presenter.StatsPresenter
import com.example.deckcycle.util.StatsAdapter

/**
 * The StatsActivity displays statistics for a specific deck, such as correct/incorrect
 * answers and progress. It retrieves the deckId from the Intent, loads the deck's
 * statistics using the presenter, and presents them in a RecyclerView. The activity
 * also provides navigation buttons to return to the lobby or sync stats with another
 * mode.
 */
class StatsActivity : AppCompatActivity() {

    private lateinit var presenter: StatsPresenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter
    private lateinit var homeButton: Button
    private lateinit var modesButton: Button
    private var deckId: Long? = null // Store the received deckId

    /**
     * Called when the activity is created. This method initializes the presenter, sets up
     * the RecyclerView, and loads the deck's statistics. It also sets click listeners for
     * the home and modes buttons to allow navigation to other activities.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        homeButton = findViewById(R.id.btnHome)
        modesButton = findViewById(R.id.btnSync)
        val deckId = intent.getLongExtra("deckId", -1)
        if (deckId == -1L) {
            finish() // Exit the activity if no valid deckId is provided
            return
        }

        presenter = StatsPresenter(this, DatabaseHelper(this))
        recyclerView = findViewById(R.id.recyclerViewStats)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load the deck's statistics
        presenter.loadDeckStats(deckId)

        // Set up listeners for buttons
        modesButton.setOnClickListener {
            val intent = Intent(this, Sync::class.java).apply {
                putExtra("deckId", deckId)
            }
            startActivity(intent)
            finish() // Close this activity after navigating
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
            finish() // Close this activity after navigating
        }
    }

    /**
     * Displays the statistics of the deck in a RecyclerView. The stats are presented
     * as a list of pairs, where the first item is a label and the second is the value.
     *
     * @param stats The statistics to display as a list of pairs (label, value).
     */
    fun displayStats(stats: List<Pair<String, String>>) {
        adapter = StatsAdapter(stats)
        recyclerView.adapter = adapter
    }
}
