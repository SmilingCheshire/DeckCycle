package com.example.deckcycle.presenter

import android.content.Context
import android.util.Log
import com.example.deckcycle.model.DatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SyncPresenter(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val firestore = FirebaseFirestore.getInstance()

        fun deleteDeckLocally(deckId: Long) {dbHelper.deleteDeck(deckId)}

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun deleteDeckFromCloud(deckId: Long) {
        val userId = getCurrentUserId() ?: return
        val deckName = dbHelper.getDeckName(deckId) ?: return

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

    fun uploadDeckToCloud(deckId: Long) {
        val userId = getCurrentUserId() ?: return
        val deckName = dbHelper.getDeckName(deckId) ?: return
        val words = dbHelper.getWordsInDeck(deckId)

        val deckData = hashMapOf(
            "name" to deckName,
            "words" to words.map { mapOf("word1" to it.first, "word2" to it.second) }
        )

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

    fun retrieveDeckFromCloud(deckId: Long) {
        val userId = getCurrentUserId() ?: return
        val deckName = dbHelper.getDeckName(deckId) ?: return

        firestore.collection("users").document(userId).collection("decks").document(deckName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val words = document.get("words") as List<Map<String, String>>

                    // Delete existing local deck
                    dbHelper.deleteDeck(deckId)

                    // Create new deck with the same name and get its new ID
                    val newDeckId = dbHelper.insertDeck(deckName)

                    // Insert words into the new deck
                    dbHelper.updateDeckWords(newDeckId, words.map { it["word1"]!! to it["word2"]!! })
                }
            }
    }



}
