package com.example.gemlink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Subscription : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_subscription)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val freeSelectButton    = findViewById<Button>(R.id.button6)
        val premiumSelectButton = findViewById<Button>(R.id.button8)
        val tvHaveProblem       = findViewById<TextView>(R.id.tv_have_problem)
        val tvSupportEmail      = findViewById<TextView>(R.id.tv_support_email)
        val llEmailRow          = findViewById<LinearLayout>(R.id.ll_email_row)
        val username            = intent.getStringExtra("USERNAME") ?: "User"

        // ── Free Plan ────────────────────────────────────────────────────
        freeSelectButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Free Plan Selected ✅")
                .setMessage("You have selected the Free plan.\nYou can upgrade to Premium anytime!")
                .setPositiveButton("Continue") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Ads::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("PLAN", "FREE")
                    startActivity(intent)
                    finish()
                }
                .setCancelable(false)
                .show()
        }

        // ── Premium Plan ─────────────────────────────────────────────────
        premiumSelectButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Premium Plan 👑")
                .setMessage(
                    "You are about to upgrade to Premium!\n\n" +
                            "• Unlimited browsing\n" +
                            "• High-resolution images\n" +
                            "• Smart filters\n" +
                            "• No ads\n" +
                            "• Price alerts\n" +
                            "• Deal notifications\n\n" +
                            "Proceed to payment?"
                )
                .setPositiveButton("Proceed to Payment") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Premium::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                }
                .setNegativeButton("Not Now") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // ── Have any problem? → Email ────────────────────────────────────
        val emailClickListener = android.view.View.OnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:gemlinkhelp@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "GemLink Subscription Support")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hello GemLink Support,\n\n" +
                            "I need help with my subscription.\n\n" +
                            "Username: $username"
                )
            }
            startActivity(emailIntent)
        }

        tvHaveProblem.setOnClickListener(emailClickListener)
        tvSupportEmail.setOnClickListener(emailClickListener)
        llEmailRow.setOnClickListener(emailClickListener)
    }
}