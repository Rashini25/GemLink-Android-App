package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameInput = findViewById<EditText>(R.id.editTextText3)
        val passwordInput = findViewById<EditText>(R.id.editTextText4)
        val confirmPasswordInput = findViewById<EditText>(R.id.editTextText5)
        val signUpButton = findViewById<Button>(R.id.button4)

        val facebookIcon = findViewById<ImageView>(R.id.imageView12)
        val googleIcon = findViewById<ImageView>(R.id.imageView13)

        // ── SIGN UP BUTTON ─────────────────────────────────────────────
        signUpButton.setOnClickListener {

            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            // Validation
            if (username.isEmpty()) {
                usernameInput.error = "Please enter a username"
                return@setOnClickListener
            }

            if (username.length < 3) {
                usernameInput.error = "Username must be at least 3 characters"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Please enter a password"
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordInput.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordInput.error = "Please confirm your password"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                AlertDialog.Builder(this)
                    .setTitle("Password Mismatch ❌")
                    .setMessage("The passwords you entered do not match.")
                    .setPositiveButton("Try Again") { dialog, _ ->
                        dialog.dismiss()
                        confirmPasswordInput.text.clear()
                        confirmPasswordInput.requestFocus()
                    }
                    .setCancelable(false)
                    .show()
                return@setOnClickListener
            }

            // ── SAVE TO FIREBASE + SUCCESS ─────────────────────────────
            AlertDialog.Builder(this)
                .setTitle("Account Created ✅")
                .setMessage("Welcome to GemLink, $username!")

                .setPositiveButton("Go to Home") { dialog, _ ->
                    dialog.dismiss()

                    // 🔥 Firebase save
                    val database = FirebaseDatabase.getInstance()
                    val ref = database.getReference("users")

                    val userData = mapOf(
                        "username" to username,
                        "password" to password
                    )

                    ref.child(username).setValue(userData)

                    // Navigate next screen
                    val intent = Intent(this, Subscription::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    finish()
                }
                .setCancelable(false)
                .show()
        }

        // ── FACEBOOK BUTTON ───────────────────────────────────────────
        facebookIcon.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Facebook Login")
                .setMessage("Coming soon!")
                .setPositiveButton("OK") { d, _ -> d.dismiss() }
                .show()
        }

        // ── GOOGLE BUTTON ─────────────────────────────────────────────
        googleIcon.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Google Login")
                .setMessage("Coming soon!")
                .setPositiveButton("OK") { d, _ -> d.dismiss() }
                .show()
        }
    }
}