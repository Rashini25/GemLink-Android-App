package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Hessonite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hessonite)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username     = intent.getStringExtra("USERNAME") ?: "User"

        val ivSellerIcon = findViewById<ImageView>(R.id.imageView68)
        val btnBuyNow    = findViewById<Button>(R.id.button10)
        val btnAddToCart = findViewById<Button>(R.id.button11)

        // ── Seller Icon → Chamod Contact Page ────────────────────────────
        ivSellerIcon.setOnClickListener {
            val intent = Intent(this, Chamod::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Buy Now ───────────────────────────────────────────────────────
        btnBuyNow.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Buy Now 💎")
                .setMessage(
                    "You are about to purchase:\n\n" +
                            "💎 Hessonite (Gomed)\n" +
                            "📍 Rathnapura\n" +
                            "💰 LKR 1,500,000.00\n" +
                            "👤 Seller: Chamod\n\n" +
                            "Proceed to payment?"
                )
                .setPositiveButton("Proceed") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Card::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Add to Cart ───────────────────────────────────────────────────
        btnAddToCart.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Added to Cart ✅")
                .setMessage(
                    "💎 Hessonite has been added to your cart!\n\n" +
                            "💰 LKR 1,500,000.00\n\n" +
                            "Would you like to continue shopping or view your cart?"
                )
                .setPositiveButton("Continue Shopping") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("Go to Cart") { dialog, _ ->
                    dialog.dismiss()
                    // ✅ Fixed — now correctly goes to Cart screen
                    val intent = Intent(this, Cart::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                }
                .show()
        }
    }
}