package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.ModeSelectionPresenter

/**
 * The ModeSelection activity presents different modes for interacting with a selected deck.
 * The user can choose between Flip Mode, Quiz Mode, Write Mode, and Stats mode.
 * The activity retrieves the selected deck's ID and passes it to the appropriate mode activity.
 */
class ModeSelection : AppCompatActivity() {

    private val presenter = ModeSelectionPresenter()
    private var deckId: Long? = null // Store the received deckId

    /**
     * Called when the activity is created. This method retrieves the deckId from the intent extras,
     * checks its validity, and sets up the buttons for each available mode.
     * Each button click launches the corresponding mode activity with the deckId passed as an extra.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
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

        // Set up button click listeners to launch the appropriate mode activity
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
            startModeActivity(StatsActivity::class.java)
        }
    }

    /**
     * Launches a mode activity and passes the deckId as an extra in the intent.
     *
     * @param modeClass The class of the mode activity to be launched.
     */
    private fun startModeActivity(modeClass: Class<*>) {
        val intent = Intent(this, modeClass).apply {
            putExtra("deckId", deckId)
        }
        startActivity(intent)
    }
}

