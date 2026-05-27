package com.example.gemlink

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val plan     = intent.getStringExtra("PLAN") ?: "FREE"

        // ── Bind Views ────────────────────────────────────────────────────
        val helloText    = findViewById<TextView>(R.id.textView49)
        val ivProfile    = findViewById<ImageView>(R.id.imageView43)
        val ivBell       = findViewById<ImageView>(R.id.imageView45)
        val tvSeeAll     = findViewById<TextView>(R.id.textView83)
        val searchView   = findViewById<SearchView>(R.id.searchView)
        val ivBuyer      = findViewById<ImageView>(R.id.imageView46)
        val ivSeller     = findViewById<ImageView>(R.id.imageView47)
        val ivRetailer   = findViewById<ImageView>(R.id.imageView48)

        val ivNavHome    = findViewById<LinearLayout>(R.id.imageView52)
        val ivNavShop    = findViewById<LinearLayout>(R.id.imageView53)
        val ivNavCart    = findViewById<LinearLayout>(R.id.imageView54)
        val ivNavProfile = findViewById<LinearLayout>(R.id.imageView55)

        helloText.text = "👋 Hello, $username"

        // ── Load Profile Photo from Firebase ──────────────────────────────
        loadProfilePhoto(username, ivProfile)

        // ── Firebase: Load Subscription Plan ─────────────────────────────
        FirebaseDatabase.getInstance()
            .getReference("subscriptions")
            .child(username)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val planFromDb = snapshot.child("plan").value.toString()
                    helloText.text = "👋 Hello, $username ($planFromDb)"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }

        // ── Categories Data ───────────────────────────────────────────────
        val categories = mapOf(
            "gem miner"              to "⛏️ Gem Miner",
            "miner"                  to "⛏️ Gem Miner",
            "gem seller"             to "🤝 Gem Seller",
            "seller"                 to "🤝 Gem Seller",
            "jewellery manufacturer" to "💍 Jewellery Manufacturer",
            "jewellery"              to "💍 Jewellery Manufacturer",
            "manufacturer"           to "💍 Jewellery Manufacturer",
            "retailer"               to "🛒 Retailer",
            "wholesaler"             to "📦 Wholesaler",
            "buyer"                  to "👤 Buyer"
        )

        val categoryDescriptions = mapOf(
            "⛏️ Gem Miner"              to "Connect with miners.",
            "🤝 Gem Seller"             to "Browse sellers.",
            "💍 Jewellery Manufacturer" to "Find manufacturers.",
            "🛒 Retailer"               to "Explore retailers.",
            "📦 Wholesaler"             to "Connect wholesalers.",
            "👤 Buyer"                  to "Access marketplace."
        )

        // ── Search Helpers ────────────────────────────────────────────────
        fun navigateToCategory(category: String) {
            val intent = Intent(this, Category::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("SELECTED_CATEGORY", category)
            startActivity(intent)
        }

        fun showCategory(category: String) {
            AlertDialog.Builder(this)
                .setTitle(category)
                .setMessage(categoryDescriptions[category] ?: "")
                .setPositiveButton("Browse") { d, _ ->
                    d.dismiss()
                    navigateToCategory(category)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── Search ────────────────────────────────────────────────────────
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val input = query?.lowercase()?.trim() ?: ""
                val match = categories[input]
                if (match != null) {
                    showCategory(match)
                }
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?) = false
        })

        // ── Profile Icon ──────────────────────────────────────────────────
        ivProfile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("PLAN", plan)
            })
        }

        // ── Bell ──────────────────────────────────────────────────────────
        ivBell.setOnClickListener {
            startActivity(Intent(this, Notifications::class.java).apply {
                putExtra("USERNAME", username)
            })
        }

        // ── See All ───────────────────────────────────────────────────────
        tvSeeAll.setOnClickListener {
            startActivity(Intent(this, Category::class.java).apply {
                putExtra("USERNAME", username)
            })
        }

        // ── Category Icons ────────────────────────────────────────────────
        ivBuyer.setOnClickListener {
            startActivity(Intent(this, Miners::class.java).apply {
                putExtra("USERNAME", username)
            })
        }

        ivSeller.setOnClickListener {
            startActivity(Intent(this, Sellers::class.java).apply {
                putExtra("USERNAME", username)
            })
        }

        ivRetailer.setOnClickListener {
            startActivity(Intent(this, Retailers::class.java).apply {
                putExtra("USERNAME", username)
            })
        }

        // ── Bottom Navigation ─────────────────────────────────────────────
        ivNavHome.setOnClickListener {
            // Already on Home
        }

        ivNavShop.setOnClickListener {
            startActivity(Intent(this, Marketplace::class.java).apply {
                putExtra("USERNAME", username)
            })
        }

        ivNavCart.setOnClickListener {
            startActivity(Intent(this, Cart::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("PLAN", plan)
            })
        }

        ivNavProfile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("PLAN", plan)
            })
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Load profile photo from Realtime Database
    // Reads profilePhotoBase64, decodes it and loads into the ImageView
    // Falls back to default drawable on error or if no photo saved yet
    // ════════════════════════════════════════════════════════════════════
    private fun loadProfilePhoto(username: String, imageView: ImageView) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(username)
            .child("profilePhotoBase64")
            .get()
            .addOnSuccessListener { snapshot ->
                val base64 = snapshot.getValue(String::class.java) ?: ""
                if (base64.isNotEmpty()) {
                    try {
                        val bytes  = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        Glide.with(this)
                            .load(bitmap)
                            .circleCrop()
                            .placeholder(R.drawable.a)
                            .into(imageView)
                    } catch (e: Exception) {
                        // Keep default photo on error — silent fail
                    }
                }
            }
            .addOnFailureListener {
                // Keep default photo on failure — silent fail
            }
    }
}