package com.example.bubService.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bubService.R

class NotificationHelper(
    private val context: Context,
    private val channelId: String,
    private val channelName: String,
) {
    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT   // IMPORTANCE_NONE recreate the notification if update
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    fun createNotificationChannel(channelId:String? = null) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            channelId ?: this.channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT   // IMPORTANCE_NONE recreate the notification if update
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    fun initNotificationBuilder(
        smallIcon: Int? = null,
        contentTitle: String = "",
        contentText: String = "",
        addAction: List<NotificationCompat.Action> = emptyList()

    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId).setOngoing(true).apply {
            smallIcon?.let { setSmallIcon(it) }
            setContentTitle(contentTitle)
            setContentText(contentText)
            setPriority(NotificationCompat.PRIORITY_MAX)
            setCategory(Notification.CATEGORY_SERVICE)
            addAction.forEach { addAction(it) }
            setSilent(true)
            setAutoCancel(true)
        }
    }
}