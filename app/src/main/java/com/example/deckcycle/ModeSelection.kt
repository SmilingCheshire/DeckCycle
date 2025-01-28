package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.ModeSelectionPresenter

class ModeSelection : AppCompatActivity() {

    private val presenter = ModeSelectionPresenter()
    private var deckId: Long? = null // Store the received deckId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selection)

        // Retrieve the deckId from the intent extras
        deckId = intent.getLongExtra("deckId", -1).takeIf { it != -1L }

        if (deckId == null) {
            Toast.makeText(this, "Error: No deck selected!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if no valid deckId is provided
            return
        }

        val btnFlipMode = findViewById<Button>(R.id.FlipMode)
        val btnQuizMode = findViewById<Button>(R.id.QuizMode)
        val btnWriteMode = findViewById<Button>(R.id.WriteMode)
        val btnStats = findViewById<Button>(R.id.Stats)

        btnFlipMode.setOnClickListener {
            startModeActivity(FlipMode::class.java)
        }

        btnQuizMode.setOnClickListener {
            startModeActivity(QuizMode::class.java)
        }

        btnWriteMode.setOnClickListener {
            startModeActivity(WriteMode::class.java)
        }

        btnStats.setOnClickListener {
            //startModeActivity(Stats::class.java)
        }
    }

    private fun startModeActivity(modeClass: Class<*>) {
        val intent = Intent(this, modeClass).apply {
            putExtra("deckId", deckId)
        }
        startActivity(intent)
    }
}
