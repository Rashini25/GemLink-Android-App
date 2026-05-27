package com.example.gemlink

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Ads : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ads)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve username passed from Subscription
        val username = intent.getStringExtra("USERNAME") ?: "User"

        val videoView = findViewById<VideoView>(R.id.videoView2)

        // Load abc.mp4 from res/raw folder
        val videoUri = Uri.parse("android.resource://$packageName/raw/abc")
        videoView.setVideoURI(videoUri)

        // Auto-play when ready
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false
            videoView.start()
        }

        // When video finishes → go to Home
        videoView.setOnCompletionListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", "FREE")
            startActivity(intent)
            finish()
        }

        // If video fails to load → skip to Home anyway
        videoView.setOnErrorListener { _, _, _ ->
            val intent = Intent(this, Home::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", "FREE")
            startActivity(intent)
            finish()
            true
        }
    }
}