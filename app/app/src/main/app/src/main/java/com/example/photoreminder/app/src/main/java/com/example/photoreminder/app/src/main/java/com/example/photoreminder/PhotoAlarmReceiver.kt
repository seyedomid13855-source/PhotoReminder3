
package com.example.photoreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PhotoAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (!ReminderStore.isRunning(context)) return

        val uris = ReminderStore.getUris(context)

        if (uris.isNotEmpty()) {
            val index = ReminderStore.nextIndex(context, uris.size)
            showNotification(context, Uri.parse(uris[index]))
        }

        ReminderScheduler.schedule(context)
    }

    private fun showNotification(context: Context, uri: Uri) {

        val channelId = "photo_reminder_channel"

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "یادآور عکس",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            manager.createNotificationChannel(channel)
        }

        val bitmap = try {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setContentTitle("یادآور عکس")
            .setContentText("زمان دیدن عکس انتخابی شماست")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (bitmap != null) {
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
            )
        }

        try {
            NotificationManagerCompat.from(context)
                .notify(9001, builder.build())
        } catch (e: SecurityException) {
            // Notification permission is not granted.
        }
    }
}
