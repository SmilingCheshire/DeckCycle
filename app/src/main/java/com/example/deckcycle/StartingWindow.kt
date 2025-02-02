package com.example.deckcycle.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.StartingWindowPresenter
import com.example.deckcycle.presenter.StartingWindowView
import com.example.deckcycle.util.NotificationHelper

/**
 * The StartingWindow activity serves as the entry point of the application. It displays
 * two buttons: one for logging in and one for registering a new account. The activity
 * manages the logic for navigating to the Login or Registration screen and also handles
 * requesting notification permissions on devices with Android versions greater than or equal to Tiramisu (Android 13).
 */
class StartingWindow : AppCompatActivity(), StartingWindowView {
    private lateinit var presenter: StartingWindowPresenter

    /**
     * Called when the activity is created. This method initializes the view components,
     * such as the login and register buttons, and sets their click listeners. It also
     * requests notification permissions on Android 13 and later, and shows a notification
     * immediately when either of the buttons is clicked.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting_window)

        presenter = StartingWindowPresenter(this)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        // Set click listeners for the login and register buttons
        loginButton.setOnClickListener {
            NotificationHelper.showInstantNotification(this) // Show notification immediately
            presenter.onLoginButtonClicked()
        }

        registerButton.setOnClickListener {
            NotificationHelper.showInstantNotification(this) // Show notification immediately
            presenter.onRegisterButtonClicked()
        }
    }

    /**
     * Navigates the user to the Login activity when the login button is clicked.
     */
    override fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

    /**
     * Navigates the user to the Registration activity when the register button is clicked.
     */
    override fun navigateToRegistration() {
        val intent = Intent(this, Registration::class.java)
        startActivity(intent)
    }
}

