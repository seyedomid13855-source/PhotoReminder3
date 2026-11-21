
package com.example.photoreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

object ReminderScheduler {

    private const val REQUEST_CODE = 4102
    private const val INTERVAL_MS = 3 * 60 * 1000L

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, PhotoAlarmReceiver::class.java)

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun start(context: Context) {
        ReminderStore.setRunning(context, true)
        schedule(context)
    }

    fun schedule(context: Context) {
        if (!ReminderStore.isRunning(context)) return

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarm.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + INTERVAL_MS,
            pendingIntent(context)
        )
    }

    fun cancel(context: Context) {
        ReminderStore.setRunning(context, false)

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent(context))
    }
}
