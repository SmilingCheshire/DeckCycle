package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R

/**
 * The QuizResults activity displays the results after a quiz is completed.
 * It shows the number of correct and incorrect answers, as well as a list of words
 * that were answered incorrectly.
 */
class QuizResults : AppCompatActivity() {

    private lateinit var lobbyButton: Button

    /**
     * Called when the activity is created. This method retrieves the quiz results passed
     * through the intent, displays the number of correct and incorrect answers, and
     * shows the list of incorrectly answered words. It also sets up a listener for
     * the "Lobby" button to navigate the user back to the Lobby screen.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_results)

        // Retrieve results from the Intent
        val correctCount = intent.getIntExtra("correctCount", 0)
        val wrongCount = intent.getIntExtra("wrongCount", 0)
        val wrongWords = intent.getSerializableExtra("wrongWords") as? List<Pair<String, String>>

        // Display results
        findViewById<TextView>(R.id.tv_correct_count).text = "Correct: $correctCount"
        findViewById<TextView>(R.id.tv_wrong_count).text = "Wrong: $wrongCount"

        // Display incorrectly answered words
        val wrongWordsText = wrongWords?.joinToString("\n") { "${it.first} -> ${it.second}" }
        findViewById<TextView>(R.id.tv_wrong_words).text = "Wrong Words:\n$wrongWordsText"

        // Initialize Lobby button and set its click listener
        lobbyButton = findViewById(R.id.btnToLobby) // Rename the variable to avoid confusion
        lobbyButton.setOnClickListener {
            val intent = Intent(this, Lobby::class.java) // Change to your actual lobby screen
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Finish QuizResults to prevent going back to it
        }

    }
}
