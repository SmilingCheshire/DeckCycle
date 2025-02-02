package com.example.deckcycle.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * Helper class for managing notifications in the app.
 * This object provides a method to show an instant notification to the user.
 * It also ensures the creation of a notification channel for devices running Android 8.0 (Oreo) or above.
 */
object NotificationHelper {

    // Unique identifier for the notification channel
    private const val CHANNEL_ID = "learning_reminder"

    // Unique identifier for the notification
    private const val NOTIFICATION_ID = 100

    /**
     * Displays an instant notification to the user with a welcome message.
     * This method creates a notification channel if necessary (for Android 8.0 and above),
     * and shows a notification with a simple title and message.
     *
     * @param context The context of the application, used to access system services and display the notification.
     * @throws IllegalArgumentException If the provided context is null or invalid.
     * @see [NotificationManager]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun showInstantNotification(context: Context) {
        // Get the system's notification manager to manage notifications
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the Notification Channel (for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Instant Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for instant notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create and display the notification instantly
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Welcome!")
            .setContentText("Let's start to learn!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Notify the system to display the notification
        notificationManager.notify(NOTIFICATION_ID, notification)

        // Ensuring the notification channel exists (in case the channel wasn't created previously)
        val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (existingChannel == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Instant Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for instant notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
