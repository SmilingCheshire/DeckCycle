package com.example.deckcycle.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val CHANNEL_ID = "learning_reminder"
    private const val NOTIFICATION_ID = 100

    fun showInstantNotification(context: Context) {
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

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
