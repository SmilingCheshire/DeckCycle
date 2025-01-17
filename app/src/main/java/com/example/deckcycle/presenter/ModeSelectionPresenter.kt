package com.example.deckcycle.presenter

class ModeSelectionPresenter {

    fun startMode(mode: String, deckId: Long?, callback: (String) -> Unit) {
        if (deckId == null) {
            callback("Error: Invalid deck!")
            return
        }
    }
}


interface ModeSelectionView {
    fun navigateToFlipMode()
    fun navigateToQuizMode()
    fun navigateToWriteMode()
    fun navigateToStats()
}
