package com.example.deckcycle.presenter

interface StartingWindowView {
    fun navigateToLogin()
    fun navigateToRegistration()
}

class StartingWindowPresenter(private val view: StartingWindowView) {
    fun onLoginButtonClicked() {
        view.navigateToLogin()
    }

    fun onRegisterButtonClicked() {
        view.navigateToRegistration()
    }
}
