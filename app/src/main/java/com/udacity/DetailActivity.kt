package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()

        val fileName = intent?.extras?.getString("fileName")
        val status = intent?.extras?.getString("status")
        binding.includedLayout.fileName.text = fileName
        binding.includedLayout.status.text = status
    }
}
