package com.example.photoreminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var status: TextView

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val saved = ReminderStore.getUris(this).toMutableSet()
            uris.forEach { uri ->
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) { }
                saved.add(uri.toString())
            }
            ReminderStore.saveUris(this, saved.toList())
            updateStatus()
            Toast.makeText(this, "${uris.size} عکس اضافه شد", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startReminder()
        else Toast.makeText(this, "برای نمایش اعلان باید اجازه اعلان را فعال کنید", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 48, 40, 32)
        }

        val title = TextView(this).apply {
            text = "یادآور عکس"
            textSize = 26f
        }

        status = TextView(this).apply {
            textSize = 17f
            setPadding(0, 28, 0, 28)
        }

        val choose = Button(this).apply {
            text = "انتخاب عکس از گوشی"
            setOnClickListener { imagePicker.launch(arrayOf("image/*")) }
        }

        val start = Button(this).apply {
            text = "شروع نمایش هر ۳ دقیقه"
            setOnClickListener { checkPermissionAndStart() }
        }

        val stop = Button(this).apply {
            text = "توقف"
            setOnClickListener {
                ReminderScheduler.cancel(this@MainActivity)
                updateStatus()
                Toast.makeText(this@MainActivity, "یادآور متوقف شد", Toast.LENGTH_SHORT).show()
            }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(choose)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
        updateStatus()
    }

    private fun checkPermissionAndStart() {
        if (ReminderStore.getUris(this).isEmpty()) {
            Toast.makeText(this, "ابتدا حداقل یک عکس انتخاب کنید", Toast.LENGTH_LONG).show()
            return
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startReminder()
        }
    }

    private fun startReminder() {
        ReminderScheduler.start(this)
        updateStatus()
        Toast.makeText(this, "یادآور فعال شد", Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus() {
        val count = ReminderStore.getUris(this).size
        val running = ReminderStore.isRunning(this)
        status.text = "عکس‌های انتخاب‌شده: $count\nوضعیت: ${if (running) "فعال" else "متوقف"}"
    }
}
