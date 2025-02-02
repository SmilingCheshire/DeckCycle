package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.RegistrationPresenter
import com.example.deckcycle.presenter.RegistrationView

/**
 * The Registration activity handles user registration, where users can enter
 * their username, email, password, and confirm password to create a new account.
 * It validates the inputs and communicates with the presenter to handle the registration logic.
 */
class Registration : AppCompatActivity(), RegistrationView {

    private lateinit var presenter: RegistrationPresenter
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var etUsername: EditText

    /**
     * Called when the activity is created. Initializes the views, sets up the button listeners,
     * and invokes the presenter to handle user registration when the register button is clicked.
     * It also handles the navigation to the login screen if the user already has an account.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        presenter = RegistrationPresenter(this)

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etRepeatPassword = findViewById(R.id.etRepeatPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val repeatPassword = etRepeatPassword.text.toString().trim()

            presenter.handleRegistration(email, password, repeatPassword, username)
        }

        val btnToLogin = findViewById<Button>(R.id.btnLogin)
        btnToLogin.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
    }

    /**
     * Shows an error message in a Toast when the registration fails or when the input is invalid.
     *
     * @param message The error message to be displayed.
     */
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Navigates to the Lobby activity after a successful registration.
     */
    override fun navigateToLobby() {
        val intent = Intent(this, Lobby::class.java)
        startActivity(intent)
        finish()
    }
}
