package com.example.deckcycle.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Class responsible for user authentication using Firebase Authentication.
 * It handles user registration, login, and storing additional user data in Firebase Database.
 */
class UserModel {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    /**
     * Registers a new user with Firebase Authentication.
     * This method creates a new user with the provided email and password and stores the username
     * in Firebase Realtime Database under the user's unique ID.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param username Chosen username for the user.
     * @param callback A callback function that returns a success status (Boolean) and an optional error message (String?).
     */
    fun registerUser(email: String, password: String, username: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save the username in the database
                    val userId = auth.currentUser?.uid
                    val userMap = mapOf("username" to username, "email" to email)
                    userId?.let {
                        database.child("users").child(it).setValue(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    callback(true, null)
                                } else {
                                    callback(false, dbTask.exception?.message)
                                }
                            }
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
    /**
     * Logs in an existing user with Firebase Authentication.
     * This method authenticates the user using the provided email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param callback A callback function that returns a success status (Boolean) and an optional error message (String?).
     */
    fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}

