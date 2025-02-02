package com.example.deckcycle.presenter
/**
 * Presenter responsible for handling mode selection and navigation.
 * It determines which mode to start based on the user's selection and deck information.
 */
class ModeSelectionPresenter {
    /**
     * Starts the selected mode based on the provided mode string and deck ID.
     * If the deck ID is invalid (null), an error message is passed to the callback.
     *
     * @param mode The mode to start. It could be "flip", "quiz", "write", or "stats".
     * @param deckId The ID of the deck to be used for the selected mode. Can be null.
     * @param callback A callback function that is invoked with a message (either error or success).
     */
    fun startMode(mode: String, deckId: Long?, callback: (String) -> Unit) {
        // Check if the deckId is valid
        if (deckId == null) {
            callback("Error: Invalid deck!")
            return
        }
    }
}

/**
 * View interface for mode selection.
 * This interface defines methods for navigating to different modes such as Flip Mode, Quiz Mode, Write Mode, and Stats.
 */
interface ModeSelectionView {
    fun navigateToFlipMode()
    fun navigateToQuizMode()
    fun navigateToWriteMode()
    fun navigateToStats()
}
