package com.example.gemlink

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Listing : AppCompatActivity() {

    // ── Firebase ──────────────────────────────────────────────────────────
    private val database = FirebaseDatabase.getInstance()

    private var selectedImageUri: Uri? = null
    private var quantity = 1

    // ── Image Picker Launcher ────────────────────────────────────────────
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                val ivAddPhoto = findViewById<ImageView>(R.id.imageView100)
                ivAddPhoto.setImageURI(selectedImageUri)
                ivAddPhoto.setPadding(0, 0, 0, 0)
                ivAddPhoto.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                findViewById<TextView>(R.id.tv_photo_hint).text =
                    "photos 1/10  Choose your listing's main photo first"
                Toast.makeText(this, "✅ Photo added!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listing)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username    = intent.getStringExtra("USERNAME") ?: ""

        // Safety check
        if (username.isEmpty()) {
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        val ivBack      = findViewById<ImageView>(R.id.iv_back_listing)
        val ivAddPhoto  = findViewById<ImageView>(R.id.imageView100)
        val tvAddPhotos = findViewById<TextView>(R.id.textView105)
        val etGemName   = findViewById<EditText>(R.id.editTextText20)
        val etGemType   = findViewById<EditText>(R.id.editTextText21)
        val etQuantity  = findViewById<EditText>(R.id.editTextText22)
        val etWeight    = findViewById<EditText>(R.id.editTextWeight)
        val etPrice     = findViewById<EditText>(R.id.editTextText23)
        val etLocation  = findViewById<EditText>(R.id.editTextText24)
        val tvPublish   = findViewById<TextView>(R.id.textView106)
        val tvQtyPlus   = findViewById<TextView>(R.id.tv_qty_plus)
        val tvQtyMinus  = findViewById<TextView>(R.id.tv_qty_minus)

        // ── Back ─────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Quantity +/- buttons ─────────────────────────────────────────
        etQuantity.setText(quantity.toString())

        tvQtyPlus.setOnClickListener {
            quantity++
            etQuantity.setText(quantity.toString())
        }

        tvQtyMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                etQuantity.setText(quantity.toString())
            }
        }

        // ── Add Photo ────────────────────────────────────────────────────
        val photoClickListener = android.view.View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle("📷 Add Photos")
                .setMessage("Choose how to add your gem photo:")
                .setPositiveButton("📁 Gallery") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    imagePickerLauncher.launch(intent)
                }
                .setNegativeButton("📷 Camera") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    imagePickerLauncher.launch(intent)
                }
                .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        ivAddPhoto.setOnClickListener(photoClickListener)
        tvAddPhotos.setOnClickListener(photoClickListener)

        // ── Gem Type Dropdown ─────────────────────────────────────────────
        val gemTypes = arrayOf(
            "Sapphire", "Ruby", "Emerald", "Hessonite (Gomed)",
            "Cat's Eye", "Yellow Sapphire", "Blue Sapphire",
            "Alexandrite", "Amethyst", "Spinel", "Tourmaline", "Other"
        )

        etGemType.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Gem Type")
                .setItems(gemTypes) { dialog, which ->
                    etGemType.setText(gemTypes[which])
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Location Dropdown ─────────────────────────────────────────────
        val locations = arrayOf(
            "Rathnapura", "Balangoda", "Pelmadulla", "Kalawana",
            "Nivithigala", "Eheliyagoda", "Kuruwita", "Elahera", "Other"
        )

        etLocation.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Location")
                .setItems(locations) { dialog, which ->
                    etLocation.setText(locations[which])
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Publish ──────────────────────────────────────────────────────
        tvPublish.setOnClickListener {
            val gemName  = etGemName.text.toString().trim()
            val gemType  = etGemType.text.toString().trim()
            val qty      = etQuantity.text.toString().trim()
            val weight   = etWeight.text.toString().trim()
            val price    = etPrice.text.toString().trim()
            val location = etLocation.text.toString().trim()

            // ── Validation ───────────────────────────────────────────────
            when {
                selectedImageUri == null -> {
                    AlertDialog.Builder(this)
                        .setTitle("Photo Required 📷")
                        .setMessage(
                            "Please add at least one photo of your gem before publishing."
                        )
                        .setPositiveButton("Add Photo") { dialog, _ ->
                            dialog.dismiss()
                            ivAddPhoto.performClick()
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .show()
                    return@setOnClickListener
                }
                gemName.isEmpty() -> {
                    etGemName.error = "Please enter the gem name"
                    etGemName.requestFocus()
                    return@setOnClickListener
                }
                gemType.isEmpty() -> {
                    etGemType.error = "Please select the gem type"
                    etGemType.requestFocus()
                    return@setOnClickListener
                }
                qty.isEmpty() || qty.toIntOrNull() == null || qty.toInt() <= 0 -> {
                    etQuantity.error = "Please enter a valid quantity"
                    etQuantity.requestFocus()
                    return@setOnClickListener
                }
                weight.isEmpty() -> {
                    etWeight.error = "Please enter the weight"
                    etWeight.requestFocus()
                    return@setOnClickListener
                }
                price.isEmpty() || price.toDoubleOrNull() == null || price.toDouble() <= 0 -> {
                    etPrice.error = "Please enter a valid price"
                    etPrice.requestFocus()
                    return@setOnClickListener
                }
                location.isEmpty() -> {
                    etLocation.error = "Please select the location"
                    etLocation.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                    // ── Preview before publishing ────────────────────────
                    AlertDialog.Builder(this)
                        .setTitle("📋 Review Your Listing")
                        .setMessage(
                            "Please review before publishing:\n\n" +
                                    "💎 Gem Name  : $gemName\n" +
                                    "🔮 Gem Type  : $gemType\n" +
                                    "🔢 Quantity  : $qty\n" +
                                    "⚖️ Weight    : $weight ct\n" +
                                    "💰 Price     : LKR $price\n" +
                                    "📍 Location  : $location\n" +
                                    "👤 Seller    : $username\n\n" +
                                    "Are you sure you want to publish?"
                        )
                        .setPositiveButton("✅ Publish Now") { dialog, _ ->
                            dialog.dismiss()
                            saveListingToFirebase(
                                username = username,
                                gemName  = gemName,
                                gemType  = gemType,
                                qty      = qty,
                                weight   = weight,
                                price    = price,
                                location = location
                            )
                        }
                        .setNegativeButton("✏️ Edit") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Save listing
    //
    // Path: listings → username → uniqueId → { all gem fields }
    //
    // We use push() to generate a unique ID for each listing
    // so multiple listings from the same user don't overwrite each other.
    // YourListing.kt reads from this same path to show the user's listings.
    // ════════════════════════════════════════════════════════════════════
    private fun saveListingToFirebase(
        username: String,
        gemName:  String,
        gemType:  String,
        qty:      String,
        weight:   String,
        price:    String,
        location: String
    ) {
        // Format today's date for display in YourListing
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Build the data map — matches what YourListing reads
        val listingData = mapOf(
            "gemName"     to gemName,
            "gemType"     to gemType,
            "quantity"    to qty,
            "weight"      to weight,
            "price"       to "LKR $price",
            "location"    to location,
            "listedDate"  to today,
            "status"      to "active",      // used by YourListing to show Sold badge
            "seller"      to username,
            "timestamp"   to System.currentTimeMillis()  // for ordering by newest first
        )

        // listings → username → push() unique key → data
        val listingRef = database
            .getReference("listings")
            .child(username)
            .push()   // generates a unique ID like -OAbc123xyz

        listingRef.setValue(listingData)
            .addOnSuccessListener {
                showPublishSuccessDialog(gemName, username)
            }
            .addOnFailureListener { e ->
                AlertDialog.Builder(this)
                    .setTitle("Publish Failed ❌")
                    .setMessage(
                        "Could not publish your listing.\n\n${e.message}\n\n" +
                                "Please check your internet connection and try again."
                    )
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
    }

    // ── Publish Success Dialog ───────────────────────────────────────────
    private fun showPublishSuccessDialog(gemName: String, username: String) {
        AlertDialog.Builder(this)
            .setTitle("🎉 Published Successfully!")
            .setMessage(
                "Your gem listing has been published!\n\n" +
                        "💎 $gemName is now live on GemLink Marketplace.\n\n" +
                        "Buyers can now view and purchase your gem.\n" +
                        "You will be notified when someone is interested."
            )
            .setPositiveButton("Go to Marketplace") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, Marketplace::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Add Another Listing") { dialog, _ ->
                dialog.dismiss()
                recreate()
            }
            .setCancelable(false)
            .show()
    }
}