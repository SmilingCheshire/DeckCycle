package com.example.deckcycle.presenter

import android.content.Context
import android.util.Log
import com.example.deckcycle.model.DatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Presenter responsible for handling synchronization of decks between the local database and Firestore.
 * It provides functionality for uploading decks to the cloud, retrieving decks from the cloud, and deleting decks both locally and from the cloud.
 */
class SyncPresenter(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Deletes a deck from the local database.
     *
     * @param deckId The ID of the deck to be deleted from the local database.
     */
    fun deleteDeckLocally(deckId: Long) {
        dbHelper.deleteDeck(deckId)  // Deletes the deck from the local database
    }

    /**
     * Retrieves the current user's unique identifier (UID) from Firebase Authentication.
     *
     * @return The current user's UID if available, or null if the user is not authenticated.
     */
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid  // Returns the current user's UID
    }

    /**
     * Deletes a deck from Firestore cloud storage.
     * It first checks if the deck exists in the cloud, and if so, deletes it.
     *
     * @param deckId The ID of the deck to be deleted from Firestore.
     */
    fun deleteDeckFromCloud(deckId: Long) {
        val userId = getCurrentUserId() ?: return  // If user is not authenticated, exit
        val deckName = dbHelper.getDeckName(deckId) ?: return  // Fetch deck name from local database

        // Reference to the deck document in Firestore
        val deckRef = firestore.collection("users").document(userId).collection("decks").document(deckName)

        // Check if the deck exists in Firestore
        deckRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("DeleteDeck", "Deck exists in cloud: $deckName")
                    // Proceed with deletion
                    deckRef.delete()
                        .addOnSuccessListener {
                            Log.d("DeleteDeck", "Deck successfully deleted from cloud")
                        }
                        .addOnFailureListener { e ->
                            Log.e("DeleteDeck", "Error deleting deck", e)
                        }
                } else {
                    Log.d("DeleteDeck", "Deck does not exist in cloud: $deckName")
                }
            }
            .addOnFailureListener { e ->
                Log.e("DeleteDeck", "Error checking if deck exists in cloud", e)
            }
    }

    /**
     * Uploads a deck to Firestore cloud storage.
     * If the deck already exists, it merges the data with the existing document.
     *
     * @param deckId The ID of the deck to be uploaded to the cloud.
     */
    fun uploadDeckToCloud(deckId: Long) {
        val userId = getCurrentUserId() ?: return  // If user is not authenticated, exit
        val deckName = dbHelper.getDeckName(deckId) ?: return  // Fetch deck name from local database
        val words = dbHelper.getWordsInDeck(deckId)  // Get all words in the deck

        // Prepare deck data to be uploaded
        val deckData = hashMapOf(
            "name" to deckName,
            "words" to words.map { mapOf("word1" to it.first, "word2" to it.second) }
        )

        // Reference to the deck document in Firestore
        val deckRef = firestore.collection("users").document(userId).collection("decks").document(deckName)

        // Instead of checking if it exists, directly update the document
        deckRef.set(deckData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("UploadDeck", "Deck successfully uploaded/updated")
            }
            .addOnFailureListener { e ->
                Log.e("UploadDeck", "Error uploading deck", e)
            }
    }

    /**
     * Retrieves a deck from the cloud (Firestore) and updates the local database with the fetched words.
     *
     * This function fetches a deck by its ID from Firestore. The deck's name is used to locate the correct
     * document in the user's cloud storage. If the deck exists in the cloud, it is downloaded and
     * stored locally in the device's database. Any existing local deck with the same ID is replaced.
     *
     * @param deckId The ID of the deck to be retrieved from the cloud.
     * @param onComplete A callback function to be called when the operation is complete. It signifies
     *                   that the deck retrieval and local storage update have finished successfully.
     *
     * @throws IllegalArgumentException If the deck ID or deck name cannot be found, or if the user is
     *                                  not authenticated.
     */
    fun retrieveDeckFromCloud(deckId: Long, onComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return  // If user is not authenticated, exit
        val deckName = dbHelper.getDeckName(deckId) ?: return  // Fetch deck name from local database

        // Fetch the deck document from Firestore
        firestore.collection("users").document(userId).collection("decks").document(deckName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val words = document.get("words") as List<Map<String, String>>  // Get the words from the cloud

                    // Delete existing local deck
                    dbHelper.deleteDeck(deckId)

                    // Create new deck with the same name and get its new ID
                    val newDeckId = dbHelper.insertDeck(deckName)

                    // Insert words into the new deck
                    dbHelper.updateDeckWords(newDeckId, words.map { it["word1"]!! to it["word2"]!! })

                    onComplete() // Notify UI that the operation is complete
                }
            }
            .addOnFailureListener { e ->
                Log.e("RetrieveDeck", "Error retrieving deck from cloud", e)
            }
    }

}
