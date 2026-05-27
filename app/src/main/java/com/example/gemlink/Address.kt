package com.example.gemlink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class Address : AppCompatActivity() {

    // ── Firebase ──────────────────────────────────────────────────────────
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_address)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username       = intent.getStringExtra("USERNAME") ?: ""

        // Safety check — if username is missing, go back to Login
        if (username.isEmpty()) {
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        val etHomeAddress  = findViewById<EditText>(R.id.editTextText19)
        val btnUpdateSave  = findViewById<Button>(R.id.button15)
        val ivLocationIcon = findViewById<ImageView>(R.id.imageView98)
        val tvLocation     = findViewById<TextView>(R.id.textView104)
        val btnConfirm     = findViewById<Button>(R.id.button16)

        // ── Load existing saved address from Firebase on screen open ─────
        loadSavedAddress(username, etHomeAddress, tvLocation)

        // ── Location Icon → Open Google Maps ────────────────────────────
        ivLocationIcon.setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:6.9271,80.7718?q=6.9271,80.7718(Gem+Mining+Region+Sri+Lanka)")
            )
            mapIntent.setPackage("com.google.android.apps.maps")

            // If Google Maps is installed, open it; otherwise open browser maps
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/?q=6.9271,80.7718")
                )
                startActivity(browserIntent)
            }

            tvLocation.text = "📍 Location selected via Google Maps"
        }

        // ── Update and Save button → saves address only to Firebase ─────
        btnUpdateSave.setOnClickListener {
            val address = etHomeAddress.text.toString().trim()

            // Validation
            if (address.isEmpty()) {
                etHomeAddress.error = "Please enter your home or company address"
                etHomeAddress.requestFocus()
                return@setOnClickListener
            }
            if (address.length < 10) {
                etHomeAddress.error = "Please enter a complete address (at least 10 characters)"
                etHomeAddress.requestFocus()
                return@setOnClickListener
            }

            // Disable button while saving
            btnUpdateSave.isEnabled = false
            btnUpdateSave.text      = "Saving..."

            // ── Save address to Firebase ─────────────────────────────────
            // Path: users → username → address
            database.getReference("users")
                .child(username)
                .child("address")
                .setValue(address)
                .addOnSuccessListener {
                    btnUpdateSave.isEnabled = true
                    btnUpdateSave.text      = "Update & Save"

                    AlertDialog.Builder(this)
                        .setTitle("Address Saved ✅")
                        .setMessage(
                            "Your address has been saved successfully!\n\n" +
                                    "📍 Address:\n$address\n\n" +
                                    "You can update this anytime from your profile."
                        )
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                .addOnFailureListener { e ->
                    btnUpdateSave.isEnabled = true
                    btnUpdateSave.text      = "Update & Save"

                    AlertDialog.Builder(this)
                        .setTitle("Save Failed ❌")
                        .setMessage(
                            "Could not save your address.\n\n${e.message}\n\n" +
                                    "Please check your internet connection and try again."
                        )
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
        }

        // ── Confirm button → saves address + location then goes to Profile
        btnConfirm.setOnClickListener {
            val homeAddress = etHomeAddress.text.toString().trim()
            val location    = tvLocation.text.toString().trim()

            // Validation
            if (homeAddress.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Missing Information ⚠️")
                    .setMessage("Please enter your home or company address before confirming.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@setOnClickListener
            }
            if (homeAddress.length < 10) {
                etHomeAddress.error = "Please enter a complete address"
                etHomeAddress.requestFocus()
                return@setOnClickListener
            }

            // Disable button while saving
            btnConfirm.isEnabled = false
            btnConfirm.text      = "Saving..."

            // ── Save both address and location to Firebase ───────────────
            // Path: users → username → addressBook → { address, location }
            val addressData = mapOf(
                "address"  to homeAddress,
                "location" to if (location.isEmpty()) "Not selected" else location
            )

            database.getReference("users")
                .child(username)
                .child("addressBook")
                .setValue(addressData)
                .addOnSuccessListener {
                    btnConfirm.isEnabled = true
                    btnConfirm.text      = "Confirm"

                    AlertDialog.Builder(this)
                        .setTitle("Address Book Updated ✅")
                        .setMessage(
                            "Your address book has been saved!\n\n" +
                                    "🏠 Address:\n$homeAddress\n\n" +
                                    (if (location.isNotEmpty()) "$location\n\n" else "") +
                                    "Returning to your profile..."
                        )
                        .setPositiveButton("Go to Profile") { dialog, _ ->
                            dialog.dismiss()
                            val intent = Intent(this, Profile::class.java)
                            intent.putExtra("USERNAME", username)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("Stay Here") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                .addOnFailureListener { e ->
                    btnConfirm.isEnabled = true
                    btnConfirm.text      = "Confirm"

                    AlertDialog.Builder(this)
                        .setTitle("Save Failed ❌")
                        .setMessage(
                            "Could not save your address book.\n\n${e.message}\n\n" +
                                    "Please check your internet connection and try again."
                        )
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Load saved address when screen opens
    //
    // Reads users → username → addressBook from Firebase and fills in
    // the EditText and location TextView automatically so the user
    // can see their previously saved address.
    // ════════════════════════════════════════════════════════════════════
    private fun loadSavedAddress(
        username: String,
        etHomeAddress: EditText,
        tvLocation: TextView
    ) {
        database.getReference("users")
            .child(username)
            .child("addressBook")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val savedAddress  = snapshot.child("address").value?.toString() ?: ""
                    val savedLocation = snapshot.child("location").value?.toString() ?: ""

                    if (savedAddress.isNotEmpty()) {
                        etHomeAddress.setText(savedAddress)
                    }
                    if (savedLocation.isNotEmpty() && savedLocation != "Not selected") {
                        tvLocation.text = savedLocation
                    }
                }
            }
            .addOnFailureListener {
                // Silent fail on load — user can still enter address manually
                Toast.makeText(
                    this,
                    "Could not load saved address. You can enter it manually.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}