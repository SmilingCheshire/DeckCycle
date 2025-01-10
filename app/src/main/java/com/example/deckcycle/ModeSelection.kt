package com.example.deckcycle.view

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.ModeSelectionPresenter

class ModeSelection : AppCompatActivity() {

    private val presenter = ModeSelectionPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selection)

        val btnFlipMode = findViewById<Button>(R.id.FlipMode)
        val btnQuizMode = findViewById<Button>(R.id.QuizMode)
        val btnWriteMode = findViewById<Button>(R.id.WriteMode)
        val btnStats = findViewById<Button>(R.id.Stats)

        btnFlipMode.setOnClickListener {
            presenter.startMode("Flip") { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        btnQuizMode.setOnClickListener {
            presenter.startMode("Quiz") { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        btnWriteMode.setOnClickListener {
            presenter.startMode("Write") { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        btnStats.setOnClickListener {
            presenter.startMode("Stats") { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
