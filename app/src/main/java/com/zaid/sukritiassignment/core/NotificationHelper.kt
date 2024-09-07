package com.zaid.sukritiassignment.core

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.zaid.sukritiassignment.MainActivity
import com.zaid.sukritiassignment.R
import com.zaid.sukritiassignment.core.Const.CHANNEL_ID
import com.zaid.sukritiassignment.core.broadcast_receiver.MusicNotificationReceiver
import javax.inject.Inject
import javax.inject.Singleton

object Const {
    const val CHANNEL_ID = "channel_id"
}

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {

    private fun getNotification(
        title: String,
        content: String,
        isMusicPlaying: Boolean
    ): Notification {

        val style = androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2) // Media notification Style

        val notificationIcon =
            if (isMusicPlaying) R.drawable.ic_pause_btn else R.drawable.ic_play_btn

        // Create an Intent to open the app from the notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // PendingIntent to open the app
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//         PendingIntent for Previous button action
        val previousIntent = Intent(context, MusicNotificationReceiver::class.java).apply {
            action = Action.ACTION_PREVIOUS.toString()
            Log.e("GetNotificationZak", "ACTION_PREVIOUS")
        }
        val previousPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//         PendingIntent for PauseAndPlay button action
        val pauseAndPlayIntent = Intent(context, MusicNotificationReceiver::class.java).apply {
            action = Action.ACTION_PAUSE_PLAY.toString()
            putExtra("isMusicPlaying", isMusicPlaying)
            Log.e("GetNotificationZak", "ACTION_PAUSE_PLAY")
        }
        val pauseAndPlayPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            pauseAndPlayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//         PendingIntent for Next button action
        val nextIntent = Intent(context, MusicNotificationReceiver::class.java).apply {
            action = Action.ACTION_NEXT.toString()
            Log.e("GetNotificationZak", "ACTION_NEXT")
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_skip_back, "Previous", previousPendingIntent) // Previous button
            .addAction(notificationIcon, if (isMusicPlaying) "Pause" else "Play", pauseAndPlayPendingIntent) // Pause And Play
            .addAction(R.drawable.ic_skip_forward, "Next", nextPendingIntent) // Next button
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(notificationIcon)
            .setContentIntent(pendingIntent)  // Attach the PendingIntent to open the app
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(false)
            .setOngoing(true)
            .setStyle(style)
            .build()
    }


    fun showNotification(title: String, content: String, isMusicPlaying: Boolean) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotification(title, content, isMusicPlaying)
        notificationManager.notify(1, notification)
    }

    // Function to hide the notification
    fun hideNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1) // Cancel the notification with the same ID
    }

    enum class Action{
        ACTION_NEXT,ACTION_PREVIOUS,ACTION_PAUSE_PLAY
    }

}



