package com.example.gemlink

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class Setting : AppCompatActivity() {

    // ── Firebase ──────────────────────────────────────────────────────────
    private val database = FirebaseDatabase.getInstance()

    private var selectedImageUri: Uri?        = null
    private var certificationFileName: String = ""
    private lateinit var username: String

    // ── Profile Image Picker ─────────────────────────────────────────────
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                val ivProfilePic = findViewById<ImageView>(R.id.imageView81)
                Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(ivProfilePic)
                Toast.makeText(
                    this,
                    "✅ Photo selected! Press Save to upload.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ── Certification File Picker ────────────────────────────────────────
    private val certPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                try {
                    val cursor = contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        val nameIndex = it.getColumnIndex(
                            android.provider.OpenableColumns.DISPLAY_NAME
                        )
                        if (nameIndex >= 0 && it.moveToFirst()) {
                            certificationFileName = it.getString(nameIndex) ?: ""
                        }
                    }
                    if (certificationFileName.isEmpty()) {
                        certificationFileName = "certificate_${System.currentTimeMillis()}"
                    }
                    val etCertification = findViewById<EditText>(R.id.editTextText15)
                    etCertification.setText(certificationFileName)
                    Toast.makeText(
                        this,
                        "✅ Certificate selected: $certificationFileName",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this, "❌ Error reading file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = intent.getStringExtra("USERNAME") ?: ""

        if (username.isEmpty()) {
            Toast.makeText(this, "⚠️ Session error. Please login again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val usersRef = database.getReference("users").child(username)

        val ivProfilePic    = findViewById<ImageView>(R.id.imageView81)
        val ivEditPhoto     = findViewById<ImageView>(R.id.imageView92)
        val ivDropdown      = findViewById<ImageView>(R.id.imageView89)
        val ivCertificate   = findViewById<ImageView>(R.id.imageView90)
        val tvSave          = findViewById<TextView>(R.id.textView125)

        val etFirstName     = findViewById<EditText>(R.id.editTextText7)
        val etLastName      = findViewById<EditText>(R.id.editTextText8)
        val etAboutYou      = findViewById<EditText>(R.id.editTextText9)
        val etWhoAreYou     = findViewById<EditText>(R.id.editTextText10)
        val etEmail         = findViewById<EditText>(R.id.editTextText11)
        val etGender        = findViewById<EditText>(R.id.editTextText12)
        val etPhone1        = findViewById<EditText>(R.id.editTextText13)
        val etPhone2        = findViewById<EditText>(R.id.editTextText14)
        val etCertification = findViewById<EditText>(R.id.editTextText15)

        etFirstName.hint            = "First Name"
        etLastName.hint             = "Last Name"
        etAboutYou.hint             = "About You"
        etWhoAreYou.hint            = "Who are You"
        etEmail.hint                = "E-mail"
        etGender.hint               = "Gender"
        etPhone1.hint               = "Phone No.01"
        etPhone2.hint               = "Phone No.02"
        etCertification.hint        = "Add Certifications"
        etCertification.isFocusable = false

        // ════════════════════════════════════════════════════════════════
        // FIREBASE — Load existing profile + photo on screen open
        // profilePhotoBase64 is a Base64 string saved in Realtime Database
        // We decode it back to a Bitmap and show it with Glide
        // ════════════════════════════════════════════════════════════════
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return

                etFirstName.setText(snapshot.child("firstName").getValue(String::class.java) ?: "")
                etLastName.setText(snapshot.child("lastName").getValue(String::class.java) ?: "")
                etAboutYou.setText(snapshot.child("aboutYou").getValue(String::class.java) ?: "")
                etWhoAreYou.setText(snapshot.child("whoAreYou").getValue(String::class.java) ?: "")
                etEmail.setText(snapshot.child("email").getValue(String::class.java) ?: "")
                etGender.setText(snapshot.child("gender").getValue(String::class.java) ?: "")
                etPhone1.setText(snapshot.child("phone1").getValue(String::class.java) ?: "")
                etPhone2.setText(snapshot.child("phone2").getValue(String::class.java) ?: "")

                val savedCert = snapshot.child("certificationFileName")
                    .getValue(String::class.java) ?: ""
                if (savedCert.isNotEmpty()) {
                    etCertification.setText(savedCert)
                    certificationFileName = savedCert
                }

                // Load saved profile photo from Base64 string
                val base64Photo = snapshot.child("profilePhotoBase64")
                    .getValue(String::class.java) ?: ""
                if (base64Photo.isNotEmpty()) {
                    loadBase64IntoImageView(base64Photo, ivProfilePic)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@Setting,
                    "❌ Load failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        // ── Edit Profile Photo ───────────────────────────────────────────
        val photoClickListener = android.view.View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle("📷 Update Profile Photo")
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

        ivProfilePic.setOnClickListener(photoClickListener)
        ivEditPhoto.setOnClickListener(photoClickListener)

        // ── Who are You Dropdown ─────────────────────────────────────────
        val roles = arrayOf(
            "Gem Miner", "Gem Seller", "Jewellery Manufacturer",
            "Retailer", "Wholesaler", "Buyer"
        )
        val dropdownClickListener = android.view.View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Who are You?")
                .setItems(roles) { dialog, which ->
                    etWhoAreYou.setText(roles[which])
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        etWhoAreYou.setOnClickListener(dropdownClickListener)
        ivDropdown.setOnClickListener(dropdownClickListener)

        // ── Gender Dropdown ──────────────────────────────────────────────
        val genders = arrayOf("Male", "Female", "Prefer not to say")
        etGender.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Gender")
                .setItems(genders) { dialog, which ->
                    etGender.setText(genders[which])
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Certification ────────────────────────────────────────────────
        val certClickListener = android.view.View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle("📄 Add Certification")
                .setMessage("Supported: PDF, JPG, PNG, DOC")
                .setPositiveButton("📁 Choose File") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"
                        putExtra(
                            Intent.EXTRA_MIME_TYPES,
                            arrayOf(
                                "application/pdf", "image/jpeg", "image/png",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            )
                        )
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    certPickerLauncher.launch(intent)
                }
                .setNeutralButton("Type manually") { dialog, _ ->
                    dialog.dismiss()
                    etCertification.isFocusable = true
                    etCertification.isFocusableInTouchMode = true
                    etCertification.requestFocus()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        ivCertificate.setOnClickListener(certClickListener)
        etCertification.setOnClickListener(certClickListener)

        // ── Save Button ──────────────────────────────────────────────────
        tvSave.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName  = etLastName.text.toString().trim()
            val aboutYou  = etAboutYou.text.toString().trim()
            val whoAreYou = etWhoAreYou.text.toString().trim()
            val email     = etEmail.text.toString().trim()
            val gender    = etGender.text.toString().trim()
            val phone1    = etPhone1.text.toString().trim()
            val phone2    = etPhone2.text.toString().trim()
            val certText  = etCertification.text.toString().trim()

            if (certText.isNotEmpty() && certificationFileName.isEmpty()) {
                certificationFileName = certText
            }

            // ── Validation ───────────────────────────────────────────────
            when {
                firstName.isEmpty() -> {
                    etFirstName.error = "Please enter your first name"
                    etFirstName.requestFocus(); return@setOnClickListener
                }
                lastName.isEmpty() -> {
                    etLastName.error = "Please enter your last name"
                    etLastName.requestFocus(); return@setOnClickListener
                }
                email.isEmpty() -> {
                    etEmail.error = "Please enter your email"
                    etEmail.requestFocus(); return@setOnClickListener
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = "Please enter a valid email"
                    etEmail.requestFocus(); return@setOnClickListener
                }
                phone1.isEmpty() -> {
                    etPhone1.error = "Please enter your phone number"
                    etPhone1.requestFocus(); return@setOnClickListener
                }
                phone1.length < 10 -> {
                    etPhone1.error = "Please enter a valid 10-digit number"
                    etPhone1.requestFocus(); return@setOnClickListener
                }
            }

            val loadingDialog = AlertDialog.Builder(this)
                .setTitle("💾 Saving...")
                .setMessage("Please wait...")
                .setCancelable(false)
                .create()
            loadingDialog.show()

            // ── If new photo selected, convert to Base64 first ───────────
            if (selectedImageUri != null) {
                val base64String = convertUriToBase64(selectedImageUri!!)
                if (base64String != null) {
                    saveProfileToDatabase(
                        usersRef        = usersRef,
                        firstName       = firstName,
                        lastName        = lastName,
                        aboutYou        = aboutYou,
                        whoAreYou       = whoAreYou,
                        email           = email,
                        gender          = gender,
                        phone1          = phone1,
                        phone2          = phone2,
                        base64Photo     = base64String,
                        loadingDialog   = loadingDialog
                    )
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        this, "❌ Could not process photo. Try again.", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // No new photo — save text fields only
                saveProfileToDatabase(
                    usersRef        = usersRef,
                    firstName       = firstName,
                    lastName        = lastName,
                    aboutYou        = aboutYou,
                    whoAreYou       = whoAreYou,
                    email           = email,
                    gender          = gender,
                    phone1          = phone1,
                    phone2          = phone2,
                    base64Photo     = null,
                    loadingDialog   = loadingDialog
                )
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // Convert selected image Uri → compressed Base64 string
    //
    // We compress the image to max 300x300 pixels and 60% JPEG quality
    // so the Base64 string stays small enough for Realtime Database.
    // Realtime Database has a 10MB node limit — a compressed photo
    // will typically be 20–80KB as Base64, well within limits.
    // ════════════════════════════════════════════════════════════════════
    private fun convertUriToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Resize to max 300x300 to keep Base64 string small
            val resized = Bitmap.createScaledBitmap(originalBitmap, 300, 300, true)

            val outputStream = ByteArrayOutputStream()
            // Compress as JPEG at 60% quality
            resized.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()

            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // Decode Base64 string → Bitmap and show in ImageView using Glide
    // ════════════════════════════════════════════════════════════════════
    private fun loadBase64IntoImageView(base64String: String, imageView: ImageView) {
        try {
            val bytes  = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Glide.with(this)
                .load(bitmap)
                .circleCrop()
                .placeholder(R.drawable.a)
                .error(R.drawable.a)
                .into(imageView)
        } catch (e: Exception) {
            // Silent fail — just keep default photo
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Save profile to Realtime Database
    // Uses updateChildren so password/username fields are never deleted
    // base64Photo is only added to the map if a new photo was selected
    // ════════════════════════════════════════════════════════════════════
    private fun saveProfileToDatabase(
        usersRef     : com.google.firebase.database.DatabaseReference,
        firstName    : String,
        lastName     : String,
        aboutYou     : String,
        whoAreYou    : String,
        email        : String,
        gender       : String,
        phone1       : String,
        phone2       : String,
        base64Photo  : String?,
        loadingDialog: AlertDialog
    ) {
        val profileMap = mutableMapOf<String, Any>(
            "username"              to username,
            "firstName"             to firstName,
            "lastName"              to lastName,
            "aboutYou"              to aboutYou,
            "whoAreYou"             to whoAreYou,
            "email"                 to email,
            "gender"                to gender,
            "phone1"                to phone1,
            "phone2"                to phone2,
            "certificationFileName" to certificationFileName,
            "updatedAt"             to System.currentTimeMillis()
        )

        // Only save photo if a new one was selected
        // Prevents overwriting existing photo when saving other fields
        if (base64Photo != null) {
            profileMap["profilePhotoBase64"] = base64Photo
        }

        usersRef.updateChildren(profileMap)
            .addOnSuccessListener {
                loadingDialog.dismiss()
                Toast.makeText(this, "✅ Profile saved!", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this)
                    .setTitle("Profile Updated ✅")
                    .setMessage(
                        "Your profile has been saved!\n\n" +
                                "👤 Name  : $firstName $lastName\n" +
                                "📧 Email : $email\n" +
                                "📞 Phone : $phone1\n" +
                                "💼 Role  : $whoAreYou\n" +
                                if (certificationFileName.isNotEmpty())
                                    "📄 Cert  : $certificationFileName" else ""
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
                loadingDialog.dismiss()
                Toast.makeText(this, "❌ Save failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}