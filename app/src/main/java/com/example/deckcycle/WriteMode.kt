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

class WriteMode : AppCompatActivity(), WriteModePresenter.WriteModeView {

    private lateinit var presenter: WriteModePresenter
    private lateinit var tvWord: TextView
    private lateinit var etAnswer: EditText
    private lateinit var btnSkip: Button
    private lateinit var btnHome: Button
    private lateinit var btnNext: Button
    private lateinit var tvCorrectAnswer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_mode)

        tvWord = findViewById(R.id.tvWord)
        etAnswer = findViewById(R.id.etAnswer)
        btnSkip = findViewById(R.id.btnSkip)
        btnHome = findViewById(R.id.btnHome)
        btnNext = findViewById(R.id.btnNext)
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer)


        val deckId = intent.getLongExtra("deckId", -1).takeIf { it != -1L }

        if (deckId == null) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val databaseHelper = DatabaseHelper(this)
        presenter = WriteModePresenter(databaseHelper)
        presenter.attachView(this)
        presenter.loadWords(deckId)

        btnSkip.setOnClickListener { presenter.skipWord() }
        btnHome.setOnClickListener { navigateToHome() }
        btnNext.setOnClickListener {
            presenter.nextWord()
            toggleButtons(isNextVisible = false)
        }

        etAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkAnswer()
                true
            } else {
                false
            }
        }
    }

    private fun checkAnswer() {
        val answer = etAnswer.text.toString().trim()
        if (answer.isNotEmpty()) {
            etAnswer.isEnabled = false // Disable the EditText
            presenter.checkAnswer(answer)
            toggleButtons(isNextVisible = true)
        }
    }


    private fun toggleButtons(isNextVisible: Boolean) {
        btnSkip.visibility = if (isNextVisible) View.GONE else View.VISIBLE
        btnNext.visibility = if (isNextVisible) View.VISIBLE else View.GONE
    }

    override fun displayWord(word: String) {
        tvWord.text = word
        etAnswer.setText("")
        etAnswer.isEnabled = true // Re-enable the EditText for the new word
        etAnswer.requestFocus() // Focus back on the EditText for convenience
        tvCorrectAnswer.visibility = View.GONE // Hide the correct answer
    }


    override fun highlightAnswer(isCorrect: Boolean) {
        val highlightColor = if (isCorrect) Color.GREEN else Color.RED
        val defaultColor = ContextCompat.getColor(this, R.color.text_color)

        if (isCorrect) {
            // Highlight with the appropriate color
            tvWord.setTextColor(highlightColor)
        } else {
            tvCorrectAnswer.setTextColor(highlightColor)
        }

        // Reset the color after a delay
        Handler().postDelayed({
            tvWord.setTextColor(defaultColor)
            tvCorrectAnswer.setTextColor(defaultColor)
        }, 1000) // 1-second delay to allow the highlight to be visible
    }

    override fun showCorrectAnswer(correctAnswer: String) {
        tvCorrectAnswer.text = "$correctAnswer"
        tvCorrectAnswer.visibility = View.VISIBLE
    }


    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToHome() {
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish()
    }
}
