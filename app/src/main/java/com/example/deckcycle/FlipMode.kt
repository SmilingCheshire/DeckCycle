package com.example.deckcycle.view


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.FlipModePresenter

class FlipMode : AppCompatActivity() {

    private lateinit var presenter: FlipModePresenter
    private lateinit var wordTextView: TextView
    private lateinit var imageView: ImageView
    internal lateinit var homeButton: Button
    internal lateinit var repeatButton: Button
    internal lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_mode)

        // Initialize views
        wordTextView = findViewById(R.id.wordTextView)
        imageView = findViewById(R.id.imageview)
        homeButton = findViewById(R.id.homeButton)
        repeatButton = findViewById(R.id.repeatButton)
        nextButton = findViewById(R.id.nextButton)

        // Retrieve deckId
        val deckId = intent.getLongExtra("deckId", -1).takeIf { it != -1L }

        if (deckId == null) {
            Toast.makeText(this, "Error: No deck selected!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        presenter = FlipModePresenter(deckId, this)

        // Set listeners
        wordTextView.setOnClickListener { presenter.flipPair() }
        imageView.setOnClickListener { presenter.flipPair() }

        repeatButton.setOnClickListener { presenter.repeatWord() }
        nextButton.setOnClickListener { presenter.nextWord() }
        homeButton.setOnClickListener {
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }

        // Initialize the first word
        presenter.nextWord()
    }

    fun updateWord(word: String, imageResId: Int) {
        wordTextView.text = word
        imageView.setImageResource(imageResId)
    }

}
