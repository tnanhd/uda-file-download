package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadId: Long = 0

    private var downloadOption: DownloadSrc? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                binding.includedLayout.customButton.setState(ButtonState.Completed)

                var downloadStatus = getString(R.string.fail)
                val cursor = context.getSystemService(DownloadManager::class.java).query(
                    DownloadManager.Query().setFilterById(downloadId)
                )
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadStatus = context.getString(R.string.success)
                    }
                }

                val detailIntent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra(KEY_FILE_NAME, downloadOption?.title)
                    putExtra(KEY_STATUS, downloadStatus)
                }
                pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                action = NotificationCompat.Action.Builder(
                    R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.notification_action_text),
                    pendingIntent
                ).build()

                notificationManager.sendNotification(
                    context.getString(R.string.notification_description, downloadOption?.title),
                    pendingIntent,
                    action,
                    context
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        requestNotificationsPermissionFromUser()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.includedLayout.customButton.setOnClickListener {
            notificationManager.cancelNotifications()

            if (downloadOption == null) {
                Toast.makeText(this, "Please select an option to download", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            binding.includedLayout.customButton.setState(ButtonState.Clicked)
            download()
        }

        binding.includedLayout.downloadOptions.setOnCheckedChangeListener { _, checkedId ->
            downloadOption = when (checkedId) {
                R.id.download_glide_option -> DownloadSrc.GLIDE
                R.id.download_loadapp_option -> DownloadSrc.LOAD_APP
                R.id.download_retrofit_option -> DownloadSrc.RETROFIT
                else -> null
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun requestNotificationsPermissionFromUser() {
        // For API <= 32, declared POST_NOTIFICATION permission in Manifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_denied_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun download() {
        val request = DownloadManager.Request(Uri.parse(downloadOption?.url))
            .setTitle(downloadOption?.shortName)
            .setDescription(downloadOption?.title)
            .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "")
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
    }
}

enum class DownloadSrc(val url: String, val title: String, val shortName: String) {
    GLIDE(
        "https://github.com/bumptech/glide/archive/master.zip",
        "Glide: Image Loading Library by BumpTech",
        "Glide"
    ),
    // url is wrong -> status = fail
    LOAD_APP(
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zi",
        "LoadApp - Current repository by Udacity",
        "LoadApp"
    ),
    RETROFIT(
        "https://github.com/square/retrofit/archive/master.zip",
        "Retrofit - Type-safe HTTP client for Android and Java by Square, Inc",
        "Retrofit"
    )
}

const val NOTIFICATION_PERMISSION_REQUEST_CODE = 0