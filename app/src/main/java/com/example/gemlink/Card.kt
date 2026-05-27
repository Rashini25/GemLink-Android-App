package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class Card : AppCompatActivity() {

    private var selectedCardType = "Debit Card"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"

        val tvCardType = findViewById<TextView>(R.id.textView82)
        val ivDropdown = findViewById<ImageView>(R.id.imageView72)
        val etNameOnCard = findViewById<EditText>(R.id.editTextText6)
        val etCardNumber = findViewById<EditText>(R.id.editTextNumber)
        val etExpiryDate = findViewById<EditText>(R.id.editTextDate)
        val etCvv = findViewById<EditText>(R.id.editTextNumberSigned)
        val btnConfirm = findViewById<Button>(R.id.button12)
        val tvHaveProblem = findViewById<TextView>(R.id.textView89)

        // ── Card Types ───────────────────────────────────────
        val cardTypes = arrayOf(
            "💳  Debit Card",
            "💰  Credit Card",
            "🏦  Bank Transfer",
            "📱  Mobile Payment",
            "🅿️  PayPal"
        )

        val showDropdown = {
            AlertDialog.Builder(this)
                .setTitle("Select Payment Method")
                .setItems(cardTypes) { dialog, which ->
                    selectedCardType = cardTypes[which]
                    tvCardType.text = selectedCardType
                    dialog.dismiss()

                    if (selectedCardType.contains("Bank Transfer") ||
                        selectedCardType.contains("Mobile Payment")) {
                        etCvv.visibility = View.GONE
                    } else {
                        etCvv.visibility = View.VISIBLE
                    }
                }
                .show()
            Unit
        }

        tvCardType.setOnClickListener { showDropdown() }
        ivDropdown.setOnClickListener { showDropdown() }

        // ── Format Card Number ───────────────────────────────
        etCardNumber.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                isFormatting = true

                val clean = s.toString().replace(" ", "")
                val formatted = StringBuilder()

                for (i in clean.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(clean[i])
                }

                etCardNumber.setText(formatted)
                etCardNumber.setSelection(formatted.length)
                isFormatting = false
            }
        })

        // ── Format Expiry Date ───────────────────────────────
        etExpiryDate.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                isFormatting = true

                val clean = s.toString().replace("/", "")
                val formatted = if (clean.length >= 2) {
                    clean.substring(0, 2) + "/" + clean.substring(2)
                } else clean

                etExpiryDate.setText(formatted)
                etExpiryDate.setSelection(formatted.length)
                isFormatting = false
            }
        })

        // ── Confirm Payment ──────────────────────────────────
        btnConfirm.setOnClickListener {

            val name = etNameOnCard.text.toString().trim()
            val cardNumber = etCardNumber.text.toString().trim()
            val expiry = etExpiryDate.text.toString().trim()
            val cvv = etCvv.text.toString().trim()

            if (name.isEmpty()) {
                etNameOnCard.error = "Enter name"
                return@setOnClickListener
            }

            if (cardNumber.replace(" ", "").length < 16) {
                etCardNumber.error = "Invalid card"
                return@setOnClickListener
            }

            if (expiry.length < 5) {
                etExpiryDate.error = "Invalid expiry"
                return@setOnClickListener
            }

            if (etCvv.visibility == View.VISIBLE && cvv.length < 3) {
                etCvv.error = "Invalid CVV"
                return@setOnClickListener
            }

            // ── SUCCESS ───────────────────────────────────────
            AlertDialog.Builder(this)
                .setTitle("Payment Successful 🎉")
                .setMessage(
                    "Welcome to GemLink Premium, $username!\n\n" +
                            "💳 Card ending: ${cardNumber.takeLast(4)}\n" +
                            "✅ Subscription Activated"
                )
                .setPositiveButton("Go to Home") { dialog, _ ->
                    dialog.dismiss()

                    // 🔥 SAVE ONLY SUBSCRIPTION DATA
                    val database = FirebaseDatabase.getInstance()
                    val ref = database.getReference("subscriptions")

                    val data = mapOf(
                        "username" to username,
                        "plan" to "PREMIUM",
                        "status" to "ACTIVE"
                    )

                    ref.child(username).setValue(data)

                    val intent = Intent(this, Home::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("PLAN", "PREMIUM")
                    startActivity(intent)
                    finish()
                }
                .setCancelable(false)
                .show()
        }

        // ── Help Section ─────────────────────────────────────
        tvHaveProblem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Need Help?")
                .setMessage("Contact support below:")
                .setPositiveButton("Email Support") { _, _ ->

                    val emailIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("support@gemlink.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Payment Support")
                        putExtra(Intent.EXTRA_TEXT, "Username: $username")
                    }

                    startActivity(Intent.createChooser(emailIntent, "Send Email"))
                }
                .setNeutralButton("Call Support") { _, _ ->
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = android.net.Uri.parse("tel:+94000000000")
                    startActivity(callIntent)
                }
                .setNegativeButton("Close", null)
                .show()
        }
    }
}