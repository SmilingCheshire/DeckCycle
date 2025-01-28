package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R

class QuizResults : AppCompatActivity() {

    private lateinit var Lobby: Button

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
        Lobby = findViewById(R.id.Lobby)
        Lobby.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }
    }
}
