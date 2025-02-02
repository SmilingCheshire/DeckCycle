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

/**
 * Activity that represents the "Flip Mode" in the app, where the user can flip through word pairs
 * from a selected deck. The user can also repeat words, move to the next word, or go back to the home screen.
 *
 * @see FlipModePresenter The presenter that handles the logic for flipping word pairs and updating the UI.
 */
class FlipMode : AppCompatActivity() {

    private lateinit var presenter: FlipModePresenter
    private lateinit var wordTextView: TextView
    private lateinit var imageView: ImageView
    internal lateinit var homeButton: Button
    internal lateinit var repeatButton: Button
    internal lateinit var nextButton: Button

    /**
     * Called when the activity is created. Initializes the UI components, sets up listeners for buttons,
     * and prepares the presenter to start flipping through words.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_mode)

        // Initialize views
        wordTextView = findViewById(R.id.wordTextView)
        imageView = findViewById(R.id.imageview)
        homeButton = findViewById(R.id.homeButton)
        repeatButton = findViewById(R.id.repeatButton)
        nextButton = findViewById(R.id.nextButton)

        // Retrieve deckId from the intent extra, and check if it's valid
        val deckId = intent.getLongExtra("deckId", -1).takeIf { it != -1L }

        if (deckId == null) {
            // Display error if no deck ID is provided
            Toast.makeText(this, "Error: No deck selected!", Toast.LENGTH_SHORT).show()
            finish() // Finish activity if deckId is invalid
            return
        }

        // Initialize the presenter with the deckId and the current activity context
        presenter = FlipModePresenter(deckId, this)

        // Set listeners for user interaction
        wordTextView.setOnClickListener { presenter.flipPair() }
        imageView.setOnClickListener { presenter.flipPair() }

        repeatButton.setOnClickListener { presenter.repeatWord() }
        nextButton.setOnClickListener { presenter.nextWord() }
        homeButton.setOnClickListener {
            // Navigate to the Lobby activity when the home button is clicked
            val intent = Intent(this, Lobby::class.java)
            startActivity(intent)
        }

        // Initialize the first word by calling nextWord on the presenter
        presenter.nextWord()
    }

    /**
     * Updates the displayed word and image in the UI.
     *
     * @param word The word to display in the TextView.
     * @param imageResId The resource ID of the image to display in the ImageView.
     */
    fun updateWord(word: String, imageResId: Int) {
        wordTextView.text = word
        imageView.setImageResource(imageResId)
    }
}

