package com.example.deckcycle.presenter
import com.example.deckcycle.model.UserModel


interface LogInView {
    fun onLoginSuccess()
    fun onLoginFailure(errorMessage: String)
}

class LogInPresenter(private val view: LogInView) {
    private val userModel = UserModel()

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
