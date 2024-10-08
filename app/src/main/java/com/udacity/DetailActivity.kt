package com.udacity

import android.app.NotificationManager
import android.content.Intent
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

        val fileName = intent?.extras?.getString(KEY_FILE_NAME)
        val status = intent?.extras?.getString(KEY_STATUS)
        binding.includedLayout.fileName.text = fileName
        binding.includedLayout.status.text = status
        if (status == getString(R.string.success)) {
            binding.includedLayout.status.setTextColor(getColor(R.color.colorPrimaryDark))
        } else if (status == getString(R.string.fail)) {
            binding.includedLayout.status.setTextColor(getColor(R.color.colorAccent))
        }

        binding.includedLayout.okButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
