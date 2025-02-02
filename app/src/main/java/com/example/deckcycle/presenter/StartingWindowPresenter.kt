package com.example.deckcycle.presenter

/**
 * Interface representing the view for the starting window of the application.
 * This interface defines the methods for navigation between the starting window,
 * login screen, and registration screen.
 */
interface StartingWindowView {
    /**
     * Navigates the user to the Login screen.
     */
    fun navigateToLogin()

    /**
     * Navigates the user to the Registration screen.
     */
    fun navigateToRegistration()
}

/**
 * Presenter responsible for handling the logic in the starting window of the application.
 * It listens for user interactions (such as clicking the Login or Register buttons)
 * and directs the view to navigate to the appropriate screen.
 */
class StartingWindowPresenter(private val view: StartingWindowView) {

    /**
     * Called when the user clicks the Login button. This method triggers navigation to the Login screen.
     */
    fun onLoginButtonClicked() {
        view.navigateToLogin()
    }

    /**
     * Called when the user clicks the Register button. This method triggers navigation to the Registration screen.
     */
    fun onRegisterButtonClicked() {
        view.navigateToRegistration()
    }
}

