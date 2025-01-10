package com.example.deckcycle.presenter

class ModeSelectionPresenter {
    fun startMode(mode: String, callback: (String) -> Unit) {
        callback("Starting $mode Mode!")
    }
}

interface ModeSelectionView {
    fun navigateToFlipMode()
    fun navigateToQuizMode()
    fun navigateToWriteMode()
    fun navigateToStats()
}
