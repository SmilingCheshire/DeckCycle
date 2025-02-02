package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.LogInPresenter
import com.example.deckcycle.presenter.LogInView

/**
 * The LogIn activity handles the user login process. It collects the user's email and password input,
 * sends it to the presenter for validation, and handles the outcome of the login process.
 *
 * @see LogInPresenter The presenter that performs the logic behind the login process.
 */
class LogIn : AppCompatActivity(), LogInView {

    private lateinit var presenter: LogInPresenter

    /**
     * Called when the activity is created. This method initializes the views for the login form,
     * sets up the presenter, and handles the login button click event.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        presenter = LogInPresenter(this)

        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Set up the login button click listener
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            presenter.performLogin(email, password) // Pass the email and password to the presenter
        }
    }

    /**
     * Called when the login is successful. Displays a success message and navigates to the Lobby screen.
     */
    override fun onLoginSuccess() {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
    }

    /**
     * Called when the login fails. Displays an error message with the provided error details.
     *
     * @param errorMessage The error message explaining why the login failed.
     */
    override fun onLoginFailure(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}

