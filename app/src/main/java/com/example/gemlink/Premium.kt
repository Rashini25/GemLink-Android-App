package com.example.gemlink

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class Premium : AppCompatActivity() {

    private var selectedPlan: String = "Yearly"

    private lateinit var cardYearly: MaterialCardView
    private lateinit var card3Months: MaterialCardView
    private lateinit var card1Month: MaterialCardView
    private lateinit var btnContinue: Button
    private lateinit var btnBack: ImageView
    private lateinit var textEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_premium)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"

        // ── Bind Views ────────────────────────────────────────────────────
        cardYearly  = findViewById(R.id.cardYearly)
        card3Months = findViewById(R.id.card3Months)
        card1Month  = findViewById(R.id.card1Month)
        btnContinue = findViewById(R.id.button7)
        btnBack     = findViewById(R.id.iv_back_listing)
        textEmail   = findViewById(R.id.textEmail)

        // ── Strikethrough on old prices ───────────────────────────────────
        listOf(
            findViewById<TextView>(R.id.textYearlyOldPrice),
            findViewById<TextView>(R.id.text3MonthsOldPrice),
            findViewById<TextView>(R.id.text1MonthOldPrice)
        ).forEach { tv ->
            tv.paintFlags = tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        // ── Default Selection ─────────────────────────────────────────────
        selectCard(cardYearly, "Yearly")

        // ── Card Click Listeners ──────────────────────────────────────────
        cardYearly.setOnClickListener  { selectCard(cardYearly,  "Yearly")   }
        card3Months.setOnClickListener { selectCard(card3Months, "3 Months") }
        card1Month.setOnClickListener  { selectCard(card1Month,  "1 Month")  }

        // ── Back Button ───────────────────────────────────────────────────
        btnBack.setOnClickListener { finish() }

        // ── Email Click ───────────────────────────────────────────────────
        textEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("gemlinkhelp@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "GemLink Premium Support")
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }

        // ── Continue to Purchase ──────────────────────────────────────────
        btnContinue.setOnClickListener {
            val intent = Intent(this, Card::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("SELECTED_PLAN", selectedPlan)
            }
            startActivity(intent)
        }
    }

    // ── Card Selection Logic ──────────────────────────────────────────────
    private fun selectCard(selectedCard: MaterialCardView, planName: String) {
        selectedPlan = planName

        resetCard(cardYearly)
        resetCard(card3Months)
        resetCard(card1Month)

        selectedCard.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.card_selected)
        )
        selectedCard.cardElevation = 10f
        selectedCard.strokeWidth   = 3
        selectedCard.strokeColor   = ContextCompat.getColor(this, R.color.card_stroke)
    }

    private fun resetCard(card: MaterialCardView) {
        card.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.card_default)
        )
        card.cardElevation = 4f
        card.strokeWidth   = 0
    }
}