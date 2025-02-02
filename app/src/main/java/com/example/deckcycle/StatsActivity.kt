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

class StatsActivity : AppCompatActivity() {

    private lateinit var presenter: StatsPresenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter
    private lateinit var homeButton: Button
    private lateinit var modesButton: Button
    private var deckId: Long? = null // Store the received deckId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        homeButton = findViewById(R.id.btnHome)
        modesButton = findViewById(R.id.btnSync)
        val deckId = intent.getLongExtra("deckId", -1)
        if (deckId == -1L) {
            finish()
            return
        }

        presenter = StatsPresenter(this, DatabaseHelper(this))
        recyclerView = findViewById(R.id.recyclerViewStats)
        recyclerView.layoutManager = LinearLayoutManager(this)

        presenter.loadDeckStats(deckId)

        modesButton.setOnClickListener {
            val intent = Intent(this, Sync::class.java).apply {
                putExtra("deckId", deckId)
            }
            startActivity(intent)
            finish()
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
            finish()
        }

    }

     fun displayStats(stats: List<Pair<String, String>>) {
        adapter = StatsAdapter(stats)
        recyclerView.adapter = adapter
    }
}
