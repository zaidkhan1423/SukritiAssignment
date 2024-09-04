package com.zaid.sukritiassignment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {

    private val CHANNEL_ID = "example_channel_id"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Example Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for example notifications"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(title: String, content: String): Notification {

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_play) // Use an appropriate drawable
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showNotification(title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotification(title, content)
        notificationManager.notify(1, notification)
    }
}

