package com.example.deckcycle.presenter

import com.example.deckcycle.model.UserModel

class RegistrationPresenter(private val view: RegistrationView) {
    private val userModel = UserModel()

    fun handleRegistration(email: String, password: String, repeatPassword: String, username: String) {
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || username.isEmpty()) {
            view.showError("All fields are required!")
            return
        }

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())) {
            view.showError("Invalid email format!")
            return
        }

        if (password != repeatPassword) {
            view.showError("Passwords do not match!")
            return
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=]).{8,}$".toRegex())) {
            view.showError(
                "Password must be at least 8 characters long, include an uppercase letter, a number, and a special character."
            )
            return
        }

        userModel.registerUser(email, password, username) { success, errorMessage ->
            if (success) {
                view.navigateToLobby()
            } else {
                view.showError(errorMessage ?: "Registration failed.")
            }
        }
    }

}

interface RegistrationView {
    fun showError(message: String)
    fun navigateToLobby()
}
