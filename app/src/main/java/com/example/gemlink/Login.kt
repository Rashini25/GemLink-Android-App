package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class Login : AppCompatActivity() {

    private var generatedCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameInput = findViewById<EditText>(R.id.editTextText)
        val passwordInput = findViewById<EditText>(R.id.editTextText2)
        val signInButton = findViewById<Button>(R.id.button5)
        val signUpText = findViewById<TextView>(R.id.textView12)
        val forgotText = findViewById<TextView>(R.id.textView5)

        // ── LOGIN (FIREBASE CHECK) ─────────────────────────────
        signInButton.setOnClickListener {

            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty()) {
                usernameInput.error = "Please enter your username"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Please enter your password"
                return@setOnClickListener
            }

            val ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)

            ref.get().addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    val savedPassword =
                        snapshot.child("password").value.toString()

                    if (savedPassword == password) {

                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, Subscription::class.java)
                        intent.putExtra("USERNAME", username)
                        startActivity(intent)
                        finish()

                    } else {
                        passwordInput.error = "Wrong password"
                    }

                } else {
                    usernameInput.error = "User not found"
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Firebase error", Toast.LENGTH_SHORT).show()
            }
        }

        // ── SIGN UP ─────────────────────────────────────────────
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }

        // ── FORGOT PASSWORD ─────────────────────────────────────
        forgotText.setOnClickListener {
            showEnterEmailDialog()
        }
    }

    // ─────────────────────────────────────────────
    // FORGOT PASSWORD (UNCHANGED YOUR LOGIC)
    // ─────────────────────────────────────────────

    private fun showEnterEmailDialog() {
        val emailInput = EditText(this).apply {
            hint = "Enter your email address"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        AlertDialog.Builder(this)
            .setTitle("Forgot Password 🔑")
            .setMessage("Enter your registered email address.")
            .setView(emailInput)
            .setPositiveButton("Send Code") { _, _ ->

                val email = emailInput.text.toString().trim()

                if (email.isEmpty()) {
                    Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                generatedCode = Random.nextInt(100000, 999999).toString()

                sendEmailWithCode(email, generatedCode)
                showVerifyCodeDialog(email)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendEmailWithCode(email: String, code: String) {

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "GemLink Password Reset Code")
            putExtra(Intent.EXTRA_TEXT, "Your code is: $code")
        }

        startActivity(Intent.createChooser(emailIntent, "Send email"))
    }

    private fun showVerifyCodeDialog(email: String) {

        val codeInput = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("Enter Code")
            .setView(codeInput)
            .setPositiveButton("Verify") { _, _ ->

                if (codeInput.text.toString() == generatedCode) {
                    showResetPasswordDialog()
                } else {
                    Toast.makeText(this, "Wrong code", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Resend", { _, _ -> showEnterEmailDialog() })
            .show()
    }

    private fun showResetPasswordDialog() {

        val input = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                Toast.makeText(this, "Password reset (UI only)", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}