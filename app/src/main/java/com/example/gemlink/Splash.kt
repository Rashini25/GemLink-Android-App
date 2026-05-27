package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 🔥 Firebase test (writes data once when app opens)
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("test")

        ref.setValue("App Started")

        // 🎯 Button navigation
        val getStartedButton = findViewById<Button>(R.id.button)

        getStartedButton.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            finish()
        }
    }
}