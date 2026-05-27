package com.example.gemlink

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Ratings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ratings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username      = intent.getStringExtra("USERNAME") ?: "User"
        val sellerName    = intent.getStringExtra("SELLER_NAME") ?: "Chamod Dinusha"

        val ivBack        = findViewById<ImageView>(R.id.iv_back_ratings)
        val ratingBar     = findViewById<RatingBar>(R.id.ratingBar_experience)
        val etFeedback    = findViewById<EditText>(R.id.et_feedback)
        val btnSubmit     = findViewById<Button>(R.id.btn_submit_rating)

        // ── Back ─────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Submit ───────────────────────────────────────────────────────
        btnSubmit.setOnClickListener {
            val stars    = ratingBar.rating.toInt()
            val feedback = etFeedback.text.toString().trim()

            // ── Validation ───────────────────────────────────────────────
            when {
                stars == 0 -> {
                    Toast.makeText(
                        this, "Please select a star rating",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                feedback.isEmpty() -> {
                    etFeedback.error = "Please tell us about your experience"
                    etFeedback.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                    val starLabel = when (stars) {
                        1    -> "⭐ Poor"
                        2    -> "⭐⭐ Fair"
                        3    -> "⭐⭐⭐ Good"
                        4    -> "⭐⭐⭐⭐ Very Good"
                        5    -> "⭐⭐⭐⭐⭐ Excellent"
                        else -> ""
                    }

                    // ── Success Dialog ───────────────────────────────────
                    AlertDialog.Builder(this)
                        .setTitle("Thank You! 🙏")
                        .setMessage(
                            "Your rating has been submitted!\n\n" +
                                    "👤 Seller  : $sellerName\n" +
                                    "⭐ Rating  : $starLabel\n\n" +
                                    "Your feedback helps us improve GemLink."
                        )
                        .setPositiveButton("Done") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }
    }
}