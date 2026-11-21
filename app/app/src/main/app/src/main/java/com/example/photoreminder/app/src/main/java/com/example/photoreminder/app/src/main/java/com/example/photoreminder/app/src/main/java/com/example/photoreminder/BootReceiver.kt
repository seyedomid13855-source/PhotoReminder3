
package com.example.photoreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED &&
            ReminderStore.isRunning(context)
        ) {
            ReminderScheduler.schedule(context)
        }
    }
}
