package com.example.deckcycle.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserModel {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

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

