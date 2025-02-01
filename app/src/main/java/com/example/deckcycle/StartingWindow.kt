package com.example.deckcycle.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.deckcycle.R
import com.example.deckcycle.presenter.StartingWindowPresenter
import com.example.deckcycle.presenter.StartingWindowView
import com.example.deckcycle.util.NotificationHelper

class StartingWindow : AppCompatActivity(), StartingWindowView {
    private lateinit var presenter: StartingWindowPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting_window)

        presenter = StartingWindowPresenter(this)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            NotificationHelper.showInstantNotification(this) // Show notification immediately
            presenter.onLoginButtonClicked()
        }

        registerButton.setOnClickListener {
            NotificationHelper.showInstantNotification(this) // Show notification immediately
            presenter.onRegisterButtonClicked()
        }
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

    override fun navigateToRegistration() {
        val intent = Intent(this, Registration::class.java)
        startActivity(intent)
    }
}
