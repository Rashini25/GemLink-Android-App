package com.example.gemlink

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class Profile : AppCompatActivity() {

    // ── Firebase ──────────────────────────────────────────────────────────
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val plan     = intent.getStringExtra("PLAN")     ?: "FREE"

        // ── Bind Views ────────────────────────────────────────────────────
        val ivBack              = findViewById<ImageView>(R.id.iv_back_profile)
        val ivProfilePhoto      = findViewById<ImageView>(R.id.imageView81)  // ← your profile photo ImageView id
        val tvUsername          = findViewById<TextView>(R.id.textView92)
        val ratingBar           = findViewById<RatingBar>(R.id.ratingBar)
        val layoutSettings      = findViewById<LinearLayout>(R.id.imageView82)
        val layoutPassword      = findViewById<LinearLayout>(R.id.imageView83)
        val layoutNotifications = findViewById<LinearLayout>(R.id.imageView84)
        val layoutAddressBook   = findViewById<LinearLayout>(R.id.imageView85)
        val layoutListings      = findViewById<LinearLayout>(R.id.imageView86)
        val layoutFeedback      = findViewById<LinearLayout>(R.id.imageView87)
        val btnLogout           = findViewById<Button>(R.id.button13)

        // ── Bottom Nav ────────────────────────────────────────────────────
        val ivNavHome    = findViewById<LinearLayout>(R.id.iv_nav_home_profile)
        val ivNavShop    = findViewById<LinearLayout>(R.id.iv_nav_shop_profile)
        val ivNavCart    = findViewById<LinearLayout>(R.id.iv_nav_cart_profile)
        val ivNavProfile = findViewById<LinearLayout>(R.id.iv_nav_profile_profile)

        // ── Display Data ──────────────────────────────────────────────────
        tvUsername.text  = username
        ratingBar.rating = 4.0f

        // ════════════════════════════════════════════════════════════════
        // FIREBASE — Load profile data (name + photo) from Realtime Database
        // Reads users → username → firstName, lastName, profilePhotoBase64
        // ════════════════════════════════════════════════════════════════
        loadProfileData(username, tvUsername, ivProfilePhoto)

        // ── Back ──────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Menu Items ────────────────────────────────────────────────────
        layoutSettings.setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        layoutPassword.setOnClickListener {
            val intent = Intent(this, Password::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        layoutNotifications.setOnClickListener {
            val intent = Intent(this, Notifications::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        layoutAddressBook.setOnClickListener {
            val intent = Intent(this, Address::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        layoutListings.setOnClickListener {
            val intent = Intent(this, YourListing::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        layoutFeedback.setOnClickListener {
            val intent = Intent(this, Feedback::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Log Out ───────────────────────────────────────────────────────
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log Out 👋")
                .setMessage("Are you sure you want to log out of GemLink?")
                .setPositiveButton("Yes, Log Out") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Splash::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .show()
        }

        // ── Bottom Navigation ─────────────────────────────────────────────
        ivNavHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }

        ivNavShop.setOnClickListener {
            val intent = Intent(this, Marketplace::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        ivNavCart.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }

        ivNavProfile.setOnClickListener {
            // Already on Profile
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Load profile name + photo
    //
    // Reads firstName + lastName from Firebase and updates the username
    // TextView to show the real full name if saved.
    // Reads profilePhotoBase64, decodes it and loads into the ImageView.
    // Falls back to showing the username string if no name is saved yet.
    // ════════════════════════════════════════════════════════════════════
    private fun loadProfileData(
        username     : String,
        tvUsername   : TextView,
        ivProfilePhoto: ImageView
    ) {
        database.getReference("users")
            .child(username)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                // ── Show full name if saved, else fall back to username ───
                val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                val lastName  = snapshot.child("lastName").getValue(String::class.java)  ?: ""
                val fullName  = "$firstName $lastName".trim()
                if (fullName.isNotEmpty()) {
                    tvUsername.text = fullName
                }

                // ── Load profile photo from Base64 ───────────────────────
                val base64 = snapshot.child("profilePhotoBase64")
                    .getValue(String::class.java) ?: ""
                if (base64.isNotEmpty()) {
                    loadBase64IntoImageView(base64, ivProfilePhoto)
                }
            }
            .addOnFailureListener {
                // Silent fail — screen still works with username and default photo
            }
    }

    // ════════════════════════════════════════════════════════════════════
    // Decode Base64 string → Bitmap and display with Glide
    // ════════════════════════════════════════════════════════════════════
    private fun loadBase64IntoImageView(base64String: String, imageView: ImageView) {
        try {
            val bytes  = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Glide.with(this)
                .load(bitmap)
                .circleCrop()
                .placeholder(R.drawable.a)   // your default profile drawable
                .error(R.drawable.a)
                .into(imageView)
        } catch (e: Exception) {
            // Keep default photo on error — silent fail
        }
    }
}