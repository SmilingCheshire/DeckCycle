package com.example.deckcycle.presenter

import com.example.deckcycle.model.UserModel

/**
 * Presenter responsible for handling the registration logic.
 * It validates the user's input, performs checks for email and password, and interacts with the `UserModel`
 * to register the user.
 */
class RegistrationPresenter(private val view: RegistrationView) {
    private val userModel = UserModel()

    /**
     * Handles the registration process by validating the user's input.
     * It checks if all fields are filled, if the email format is correct, if the passwords match,
     * and if the password meets the required criteria. If all validations pass, the registration process is initiated.
     * If any validation fails, an error message is shown to the user.
     *
     * @param email The user's email address.
     * @param password The user's chosen password.
     * @param repeatPassword The confirmation password entered by the user.
     * @param username The username chosen by the user.
     */
    fun handleRegistration(email: String, password: String, repeatPassword: String, username: String) {
        // Validate that none of the fields are empty
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || username.isEmpty()) {
            view.showError("All fields are required!")
            return
        }

        // Validate email format using regex
        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())) {
            view.showError("Invalid email format!")
            return
        }

        // Ensure the passwords match
        if (password != repeatPassword) {
            view.showError("Passwords do not match!")
            return
        }

        // Validate password strength (at least 8 characters, one uppercase, one number, one special character)
        if (!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=]).{8,}$".toRegex())) {
            view.showError(
                "Password must be at least 8 characters long, include an uppercase letter, a number, and a special character."
            )
            return
        }

        // Proceed with user registration
        userModel.registerUser(email, password, username) { success, errorMessage ->
            if (success) {
                // If registration is successful, navigate to the Lobby screen
                view.navigateToLobby()
            } else {
                // If registration fails, show an error message
                view.showError(errorMessage ?: "Registration failed.")
            }
        }
    }

}

/**
 * Interface representing the view for the registration process.
 * This view is responsible for displaying errors and navigating to the Lobby screen upon successful registration.
 */
interface RegistrationView {
    /**
     * Displays an error message to the user.
     *
     * @param message The error message to be shown.
     */
    fun showError(message: String)

    /**
     * Navigates to the Lobby screen after a successful registration.
     */
    fun navigateToLobby()
}

