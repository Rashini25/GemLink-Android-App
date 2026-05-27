package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Category : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username     = intent.getStringExtra("USERNAME") ?: "User"
        val ivBack       = findViewById<ImageView>(R.id.imageView44)
        val ivGemMiner   = findViewById<ImageView>(R.id.imageView74)
        val ivGemSeller  = findViewById<ImageView>(R.id.imageView75)
        val ivJewellery  = findViewById<ImageView>(R.id.imageView76)
        val ivRetailer   = findViewById<ImageView>(R.id.imageView77)
        val ivWholesaler = findViewById<ImageView>(R.id.imageView78)
        val ivBuyer      = findViewById<ImageView>(R.id.imageView79)

        // ── Back Button ──────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Gem Miner → Miners screen ────────────────────────────────────
        ivGemMiner.setOnClickListener {
            val intent = Intent(this, Miners::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Gem Seller → Sellers screen ✅ Updated ───────────────────────
        ivGemSeller.setOnClickListener {
            val intent = Intent(this, Sellers::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        ivJewellery.setOnClickListener {
            val intent = Intent(this, Jewelleries::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        ivRetailer.setOnClickListener {
            val intent = Intent(this, Retailers::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Category Click Handler ───────────────────────────────────────
        fun showCategoryDialog(title: String, description: String) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("Browse $title") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Marketplace::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("CATEGORY", title)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        ivWholesaler.setOnClickListener {
            showCategoryDialog(
                "📦 Wholesaler",
                "Connect with gem wholesalers.\nBulk pricing available for large orders.\nVerified business accounts only."
            )
        }

        ivBuyer.setOnClickListener {
            showCategoryDialog(
                "👤 Buyer",
                "Register as a buyer on GemLink.\nAccess the full gem marketplace.\nSafe and secure transactions guaranteed."
            )
        }
    }
}