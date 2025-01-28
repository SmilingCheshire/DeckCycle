package com.example.deckcycle.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.presenter.QuizModePresenter

class QuizMode : AppCompatActivity() {

    private lateinit var presenter: QuizModePresenter
    private lateinit var wordTextView: TextView
    internal lateinit var optionButtons: List<TextView>
    private lateinit var homeButton: Button
    private var deckId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_mode)

        // Retrieve deckId from the Intent
        deckId = intent.getLongExtra("deckId", -1).takeIf { it != -1L }
        if (deckId == null) {
            Toast.makeText(this, "Error: No deck selected!", Toast.LENGTH_SHORT).show()
            finish() // Exit the activity if no valid deckId is provided
            return
        }

        // Initialize presenter and views
        presenter = QuizModePresenter(this, DatabaseHelper(this))
        wordTextView = findViewById(R.id.tv_word)

        // Initialize the option TextViews
        val option1 = findViewById<TextView>(R.id.et_option1)
        val option2 = findViewById<TextView>(R.id.et_option2)
        val option3 = findViewById<TextView>(R.id.et_option3)
        val option4 = findViewById<TextView>(R.id.et_option4)
        homeButton = findViewById(R.id.homeButton)

        // Store option buttons in a list
        optionButtons = listOf(option1, option2, option3, option4)

        // Set up listeners for option buttons
        optionButtons.forEach { button ->
            button.setOnClickListener {
                presenter.onOptionSelected(button.text.toString())
            }
        }

        // Home button listener to return to the lobby
        homeButton.setOnClickListener {
            presenter.onHomeClicked()
        }

        // Start the quiz
        deckId?.let {
            presenter.startQuiz(it)
        }
    }

    fun displayWord(word: String, options: List<String>) {
        wordTextView.text = word
        optionButtons.forEachIndexed { index, button ->
            button.text = options.getOrNull(index) ?: ""
            button.setBackgroundColor(Color.TRANSPARENT) // Reset button colors
        }
    }

    fun showResult(isCorrect: Boolean, button: TextView?, correctAnswer: String) {
        button?.setBackgroundColor(if (isCorrect) Color.GREEN else Color.RED)

        if (!isCorrect) {
            val correctButton = optionButtons.find { it.text == correctAnswer }
            correctButton?.setBackgroundColor(Color.GREEN)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            presenter.loadNextWord()
        }, 500)
    }

    fun showEndOfQuizMessage(
        correctCount: Int,
        wrongCount: Int,
        wrongWords: List<Pair<String, String>>
    ) {
        // Pass results to the results activity
        val intent = Intent(this, QuizResults::class.java).apply {
            putExtra("correctCount", correctCount)
            putExtra("wrongCount", wrongCount)
            putExtra("wrongWords", ArrayList(wrongWords)) // Pass as Serializable
        }
        startActivity(intent)
        finish() // Exit this activity
    }
}
