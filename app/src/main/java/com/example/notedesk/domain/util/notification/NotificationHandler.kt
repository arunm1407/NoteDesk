package com.example.notedesk.domain.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notesappfragment.R

class NotificationHandler internal constructor(private val context: Context) {

    fun showNotification(str: String) {

        val notification = NotificationCompat.Builder(context, "Other Notification")
            .setSmallIcon(R.drawable.documents)
            .setContentTitle("Attachment Notification")
            .setContentText(str)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOnlyAlertOnce(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel("Other Notification", IndentKeys.OTHERS, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notification.setChannelId("Other Notification")
        notificationManager.notify(0, notification.build())


    }


    fun cancelNotification()
    {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }

}
