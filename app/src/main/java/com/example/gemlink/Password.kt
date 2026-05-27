package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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

class Password : AppCompatActivity() {

    // ── Firebase ──────────────────────────────────────────────────────────
    private val database = FirebaseDatabase.getInstance()
    private var generatedCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Username passed from previous screen — this is how your app tracks
        // the logged-in user since you don't use FirebaseAuth sessions
        val username          = intent.getStringExtra("USERNAME") ?: ""

        // Safety check — if username is missing, go back to Login
        if (username.isEmpty()) {
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        val etCurrentPassword = findViewById<EditText>(R.id.editTextText16)
        val etNewPassword     = findViewById<EditText>(R.id.editTextText17)
        val etConfirmPassword = findViewById<EditText>(R.id.editTextText18)
        val tvForgotPassword  = findViewById<TextView>(R.id.textView95)
        val btnUpdateSave     = findViewById<Button>(R.id.button14)

        // ── Toggle Password Visibility (long press) ──────────────────────
        var isCurrentVisible = false
        var isNewVisible     = false
        var isConfirmVisible = false

        etCurrentPassword.setOnLongClickListener {
            isCurrentVisible = !isCurrentVisible
            etCurrentPassword.transformationMethod =
                if (isCurrentVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            etCurrentPassword.setSelection(etCurrentPassword.text.length)
            Toast.makeText(
                this,
                if (isCurrentVisible) "Password visible" else "Password hidden",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        etNewPassword.setOnLongClickListener {
            isNewVisible = !isNewVisible
            etNewPassword.transformationMethod =
                if (isNewVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            etNewPassword.setSelection(etNewPassword.text.length)
            true
        }

        etConfirmPassword.setOnLongClickListener {
            isConfirmVisible = !isConfirmVisible
            etConfirmPassword.transformationMethod =
                if (isConfirmVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
            true
        }

        // ── Forgot Password ──────────────────────────────────────────────
        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog(username)
        }

        // ── Update Password Button ───────────────────────────────────────
        btnUpdateSave.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString().trim()
            val newPassword     = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // ── Validation ───────────────────────────────────────────────
            when {
                currentPassword.isEmpty() -> {
                    etCurrentPassword.error = "Please enter your current password"
                    etCurrentPassword.requestFocus()
                    return@setOnClickListener
                }
                currentPassword.length < 6 -> {
                    etCurrentPassword.error = "Password must be at least 6 characters"
                    etCurrentPassword.requestFocus()
                    return@setOnClickListener
                }
                newPassword.isEmpty() -> {
                    etNewPassword.error = "Please enter a new password"
                    etNewPassword.requestFocus()
                    return@setOnClickListener
                }
                newPassword.length < 6 -> {
                    etNewPassword.error = "New password must be at least 6 characters"
                    etNewPassword.requestFocus()
                    return@setOnClickListener
                }
                newPassword == currentPassword -> {
                    etNewPassword.error = "New password must be different from current"
                    etNewPassword.requestFocus()
                    return@setOnClickListener
                }
                confirmPassword.isEmpty() -> {
                    etConfirmPassword.error = "Please confirm your new password"
                    etConfirmPassword.requestFocus()
                    return@setOnClickListener
                }
                newPassword != confirmPassword -> {
                    AlertDialog.Builder(this)
                        .setTitle("Passwords Don't Match ❌")
                        .setMessage("The new password and confirm password do not match.\n\nPlease try again.")
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            etConfirmPassword.text.clear()
                            etConfirmPassword.requestFocus()
                        }
                        .setCancelable(false)
                        .show()
                    return@setOnClickListener
                }
                !isPasswordStrong(newPassword) -> {
                    AlertDialog.Builder(this)
                        .setTitle("Weak Password ⚠️")
                        .setMessage(
                            "Your password should contain:\n\n" +
                                    "• At least 6 characters\n" +
                                    "• At least one uppercase letter\n" +
                                    "• At least one number\n\n" +
                                    "Example: GemLink1"
                        )
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                    return@setOnClickListener
                }
                else -> {
                    // All validation passed — verify current password
                    // against Firebase, then update if correct
                    verifyAndUpdatePassword(
                        username        = username,
                        currentPassword = currentPassword,
                        newPassword     = newPassword,
                        btnUpdateSave   = btnUpdateSave
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Verify current password then update
    //
    // Step 1: Read the saved password from Firebase Realtime Database
    //         under users → username → password
    // Step 2: Compare it with what the user typed
    // Step 3: If it matches, overwrite it with the new password
    // ════════════════════════════════════════════════════════════════════
    private fun verifyAndUpdatePassword(
        username: String,
        currentPassword: String,
        newPassword: String,
        btnUpdateSave: Button
    ) {
        // Disable button to prevent double-clicks while Firebase is working
        btnUpdateSave.isEnabled = false
        btnUpdateSave.text      = "Checking..."

        val userRef = database.getReference("users").child(username)

        // Step 1 — Read user record from Firebase
        userRef.get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {
                    // User record not found in database
                    btnUpdateSave.isEnabled = true
                    btnUpdateSave.text      = "Update & Save"
                    Toast.makeText(
                        this,
                        "User not found. Please log in again.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                // Step 2 — Compare current password
                val savedPassword = snapshot.child("password").value.toString()

                if (savedPassword != currentPassword) {
                    // Wrong current password
                    btnUpdateSave.isEnabled = true
                    btnUpdateSave.text      = "Update & Save"
                    AlertDialog.Builder(this)
                        .setTitle("Wrong Current Password ❌")
                        .setMessage(
                            "The current password you entered is incorrect.\n\n" +
                                    "Please try again or use Forgot Password."
                        )
                        .setPositiveButton("Try Again") { dialog, _ -> dialog.dismiss() }
                        .setNegativeButton("Forgot Password") { dialog, _ ->
                            dialog.dismiss()
                            showForgotPasswordDialog(username)
                        }
                        .show()
                    return@addOnSuccessListener
                }

                // Step 3 — Current password is correct, now update in Firebase
                btnUpdateSave.text = "Saving..."
                userRef.child("password").setValue(newPassword)
                    .addOnSuccessListener {
                        btnUpdateSave.isEnabled = true
                        btnUpdateSave.text      = "Update & Save"

                        AlertDialog.Builder(this)
                            .setTitle("Password Updated ✅")
                            .setMessage(
                                "Your password has been changed successfully!\n\n" +
                                        "Please use your new password for future logins."
                            )
                            .setPositiveButton("Go to Profile") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this, Profile::class.java)
                                intent.putExtra("USERNAME", username)
                                startActivity(intent)
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        btnUpdateSave.isEnabled = true
                        btnUpdateSave.text      = "Update & Save"
                        AlertDialog.Builder(this)
                            .setTitle("Save Failed ❌")
                            .setMessage(
                                "Password could not be saved.\n\n${e.message}\n\n" +
                                        "Please check your internet connection and try again."
                            )
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
            }
            .addOnFailureListener { e ->
                btnUpdateSave.isEnabled = true
                btnUpdateSave.text      = "Update & Save"
                AlertDialog.Builder(this)
                    .setTitle("Connection Error ❌")
                    .setMessage(
                        "Could not connect to Firebase.\n\n${e.message}\n\n" +
                                "Please check your internet and try again."
                    )
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
    }

    // ════════════════════════════════════════════════════════════════════
    // FORGOT PASSWORD
    //
    // Since your app uses username + password in Realtime Database
    // (not Firebase Auth email login), we keep your original code flow:
    // Step 1 — User enters their username
    // Step 2 — We check it exists in Firebase
    // Step 3 — Generate a 6-digit code and open email app
    // Step 4 — User enters code to verify
    // Step 5 — User sets a new password → saved to Firebase
    // ════════════════════════════════════════════════════════════════════
    private fun showForgotPasswordDialog(username: String) {
        val emailInput = EditText(this).apply {
            hint      = "Enter your email address"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Forgot Password 🔑")
            .setMessage("Enter your email to receive a reset code.")
            .setView(emailInput)
            .setPositiveButton("Send Code") { _, _ ->
                val email = emailInput.text.toString().trim()

                if (email.isEmpty() ||
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    showForgotPasswordDialog(username)
                    return@setPositiveButton
                }

                // Generate 6-digit code
                generatedCode = Random.nextInt(100000, 999999).toString()

                // Open email app
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, "GemLink - Password Reset Code")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Hello GemLink User,\n\n" +
                                "Your password reset code is:\n\n" +
                                "🔐  $generatedCode\n\n" +
                                "This code is valid for 10 minutes.\n\n" +
                                "— GemLink Team"
                    )
                }
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send reset code via..."))
                } catch (e: Exception) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                }

                showVerifyCodeDialog(username)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ── Verify Code Dialog ───────────────────────────────────────────────
    private fun showVerifyCodeDialog(username: String) {
        val codeInput = EditText(this).apply {
            hint      = "Enter 6-digit code"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters   = arrayOf(android.text.InputFilter.LengthFilter(6))
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Enter Verification Code 📧")
            .setMessage("Enter the 6-digit code sent to your email.")
            .setView(codeInput)
            .setPositiveButton("Verify") { _, _ ->
                val entered = codeInput.text.toString().trim()
                if (entered == generatedCode) {
                    showNewPasswordDialog(username)
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Invalid Code ❌")
                        .setMessage("The code you entered is incorrect.")
                        .setPositiveButton("Retry") { _, _ -> showVerifyCodeDialog(username) }
                        .setNegativeButton("Resend") { _, _ -> showForgotPasswordDialog(username) }
                        .setCancelable(false)
                        .show()
                }
            }
            .setNegativeButton("Resend Code") { _, _ -> showForgotPasswordDialog(username) }
            .setCancelable(false)
            .show()
    }

    // ── New Password Dialog — saves to Firebase ──────────────────────────
    private fun showNewPasswordDialog(username: String) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val etNew = EditText(this).apply {
            hint                 = "New Password"
            inputType            = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        val etConfirm = EditText(this).apply {
            hint                 = "Confirm New Password"
            inputType            = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        layout.addView(etNew)
        layout.addView(etConfirm)

        AlertDialog.Builder(this)
            .setTitle("Set New Password 🔒")
            .setView(layout)
            .setPositiveButton("Save Password") { _, _ ->
                val newPass     = etNew.text.toString().trim()
                val confirmPass = etConfirm.text.toString().trim()

                when {
                    newPass.isEmpty() -> {
                        Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
                        showNewPasswordDialog(username)
                    }
                    newPass.length < 6 -> {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        showNewPasswordDialog(username)
                    }
                    newPass != confirmPass -> {
                        AlertDialog.Builder(this)
                            .setTitle("Passwords Don't Match ❌")
                            .setMessage("The passwords you entered do not match.")
                            .setPositiveButton("Try Again") { _, _ -> showNewPasswordDialog(username) }
                            .setCancelable(false)
                            .show()
                    }
                    else -> {
                        // ── Save new password to Firebase ────────────────
                        database.getReference("users")
                            .child(username)
                            .child("password")
                            .setValue(newPass)
                            .addOnSuccessListener {
                                generatedCode = ""
                                AlertDialog.Builder(this)
                                    .setTitle("Password Reset Successful ✅")
                                    .setMessage(
                                        "Your password has been reset successfully!\n\n" +
                                                "Please use your new password to login."
                                    )
                                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                    .setCancelable(false)
                                    .show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to save: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                showNewPasswordDialog(username)
                            }
                    }
                }
            }
            .setCancelable(false)
            .show()
    }

    // ── Password Strength Check ──────────────────────────────────────────
    private fun isPasswordStrong(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit     = password.any { it.isDigit() }
        val hasMinLength = password.length >= 6
        return hasUppercase && hasDigit && hasMinLength
    }
}