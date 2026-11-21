
package com.example.photoreminder

import android.content.Context

object ReminderStore {

    private const val PREFS = "photo_reminder"
    private const val KEY_URIS = "image_uris"
    private const val KEY_RUNNING = "running"
    private const val KEY_INDEX = "image_index"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getUris(context: Context): List<String> =
        prefs(context).getStringSet(KEY_URIS, emptySet())?.toList() ?: emptyList()

    fun saveUris(context: Context, uris: List<String>) {
        prefs(context).edit()
            .putStringSet(KEY_URIS, uris.toSet())
            .apply()
    }

    fun isRunning(context: Context): Boolean =
        prefs(context).getBoolean(KEY_RUNNING, false)

    fun setRunning(context: Context, value: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_RUNNING, value)
            .apply()
    }

    fun nextIndex(context: Context, size: Int): Int {
        if (size <= 0) return 0

        val p = prefs(context)
        val next = (p.getInt(KEY_INDEX, -1) + 1) % size

        p.edit().putInt(KEY_INDEX, next).apply()

        return next
    }
}
