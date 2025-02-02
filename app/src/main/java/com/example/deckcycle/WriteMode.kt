package com.example.deckcycle.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.deckcycle.R
import com.example.deckcycle.model.DatabaseHelper
import com.example.deckcycle.presenter.WriteModePresenter

/**
 * The WriteMode activity provides a writing-based quiz mode where the user is shown a word and
 * must type the correct translation or equivalent answer. It features functionality to check answers,
 * skip words, move to the next word, and navigate to the home screen (Lobby).
 *
 * This activity manages:
 * - Displaying a word for the user to answer.
 * - Accepting the user's written answer and checking if it's correct.
 * - Providing feedback on the user's answer.
 * - Navigating to the home screen or skipping the current word.
 */
class WriteMode : AppCompatActivity(), WriteModePresenter.WriteModeView {

    private lateinit var presenter: WriteModePresenter
    private lateinit var tvWord: TextView
    private lateinit var etAnswer: EditText
    private lateinit var btnSkip: Button
    private lateinit var btnHome: Button
    private lateinit var btnNext: Button
    private lateinit var tvCorrectAnswer: TextView

    /**
     * Called when the activity is created. It initializes the UI elements, sets up listeners for
     * buttons, and calls the presenter to load words from the database.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_mode)

        // Initialize UI elements
        tvWord = findViewById(R.id.tvWord)
        etAnswer = findViewById(R.id.etAnswer)
        btnSkip = findViewById(R.id.btnSkip)
        btnHome = findViewById(R.id.btnHome)
        btnNext = findViewById(R.id.btnNext)
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer)

        // Retrieve deckId from the Intent and validate
        val deckId = intent.getLongExtra("deckId", -1).takeIf { it != -1L }
        if (deckId == null) {
            Toast.makeText(this, "Invalid deck", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize presenter and load words
        val databaseHelper = DatabaseHelper(this)
        presenter = WriteModePresenter(databaseHelper)
        presenter.attachView(this)
        presenter.loadWords(deckId)

        // Set listeners for the buttons
        btnSkip.setOnClickListener { presenter.skipWord() }
        btnHome.setOnClickListener { navigateToHome() }
        btnNext.setOnClickListener {
            presenter.nextWord()
            toggleButtons(isNextVisible = false)
        }

        // Set listener for the EditText to handle done action
        etAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkAnswer()
                true
            } else {
                false
            }
        }
    }

    /**
     * Checks the user's written answer and submits it to the presenter for validation.
     */
    private fun checkAnswer() {
        val answer = etAnswer.text.toString().trim()
        if (answer.isNotEmpty()) {
            etAnswer.isEnabled = false // Disable the EditText after submission
            presenter.checkAnswer(answer)
            toggleButtons(isNextVisible = true)
        }
    }

    /**
     * Toggles visibility of the "Next" and "Skip" buttons.
     *
     * @param isNextVisible Boolean flag indicating whether the "Next" button should be visible.
     */
    private fun toggleButtons(isNextVisible: Boolean) {
        btnSkip.visibility = if (isNextVisible) View.GONE else View.VISIBLE
        btnNext.visibility = if (isNextVisible) View.VISIBLE else View.GONE
    }

    /**
     * Displays the word for the user to answer.
     *
     * @param word The word to be displayed.
     */
    override fun displayWord(word: String) {
        tvWord.text = word
        etAnswer.setText("") // Clear the answer field
        etAnswer.isEnabled = true // Re-enable the EditText for the new word
        etAnswer.requestFocus() // Focus on the EditText for user convenience
        tvCorrectAnswer.visibility = View.GONE // Hide the correct answer
    }

    /**
     * Highlights the word or correct answer in green (if correct) or red (if incorrect).
     *
     * @param isCorrect A boolean flag indicating whether the answer was correct.
     */
    override fun highlightAnswer(isCorrect: Boolean) {
        val highlightColor = if (isCorrect) Color.GREEN else Color.RED
        val defaultColor = ContextCompat.getColor(this, R.color.text_color)

        if (isCorrect) {
            // Highlight the word in green if the answer was correct
            tvWord.setTextColor(highlightColor)
        } else {
            // Highlight the correct answer in red if the user's answer was wrong
            tvCorrectAnswer.setTextColor(highlightColor)
        }

        // Reset the color after a delay
        Handler().postDelayed({
            tvWord.setTextColor(defaultColor)
            tvCorrectAnswer.setTextColor(defaultColor)
        }, 1000) // 1-second delay to allow the highlight to be visible
    }

    /**
     * Displays the correct answer if the user's answer was incorrect.
     *
     * @param correctAnswer The correct answer to be shown.
     */
    override fun showCorrectAnswer(correctAnswer: String) {
        tvCorrectAnswer.text = "$correctAnswer"
        tvCorrectAnswer.visibility = View.VISIBLE
    }

    /**
     * Displays a message to the user in a Toast.
     *
     * @param message The message to be shown.
     */
    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Navigates back to the home screen (Lobby).
     */
    override fun navigateToHome() {
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish() // Finish the current activity to avoid returning to it when pressing back
    }
}
