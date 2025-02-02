package com.example.deckcycle.presenter
import com.example.deckcycle.model.UserModel

/**
 * View interface for handling the login process.
 * Provides methods to notify the UI of successful or failed login attempts.
 */
interface LogInView {
    fun onLoginSuccess()
    fun onLoginFailure(errorMessage: String)
}
/**
 * Presenter class responsible for handling the login logic.
 * It interacts with the `UserModel` to perform login and communicates results to the view.
 *
 * @param view The view interface responsible for updating the UI based on login results.
 */
class LogInPresenter(private val view: LogInView) {
    private val userModel = UserModel()
    /**
     * Performs the login operation with the given email and password.
     * If either the email or password is empty, it will trigger a login failure.
     * If the login attempt is successful, the UI will be notified through the view.
     * If the login attempt fails, the UI will be notified with an error message.
     *
     * @param email The email address entered by the user.
     * @param password The password entered by the user.
     */
    fun performLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onLoginFailure("Email and Password are required!")
            return
        }

        userModel.loginUser(email, password) { success, errorMessage ->
            if (success) {
                view.onLoginSuccess()
            } else {
                view.onLoginFailure(errorMessage ?: "Login failed.")
            }
        }
    }
}
