package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    pendingIntent: PendingIntent,
    action: NotificationCompat.Action,
    applicationContext: Context
) {

    val notificationBuilder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .addAction(action)

    notify(NOTIFICATION_ID, notificationBuilder.build())
}

fun NotificationManager.createNotificationChannel(channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}