package com.example.gemlink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Chamod : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chamod)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"

        val btnAddRatings = findViewById<Button>(R.id.button17)
        val btnMessage    = findViewById<Button>(R.id.button18)
        val btnCall       = findViewById<Button>(R.id.button19)

        val tvPhone1 = findViewById<TextView>(R.id.textView116)
        val tvPhone2 = findViewById<TextView>(R.id.textView117)
        val tvEmail  = findViewById<TextView>(R.id.textView118)

        // ── Phone 1 click → Dial ─────────────────────────────────────────
        tvPhone1.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0778047628"))
            startActivity(i)
        }

        // ── Phone 2 click → Dial ─────────────────────────────────────────
        tvPhone2.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0703462702"))
            startActivity(i)
        }

        // ── Email click → Open Email App ─────────────────────────────────
        tvEmail.setOnClickListener {
            val i = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:chamoddinusha176@gmail.com")

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "GemLink - Inquiry about Hessonite"
                )

                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hello Chamod,\n\n" +
                            "I am interested in your Hessonite gem listing on GemLink.\n\n" +
                            "Regards,\n$username"
                )
            }

            startActivity(i)
        }

        // ── Add Ratings → Goes to Ratings screen ✅ ──────────────────────
        btnAddRatings.setOnClickListener {
            val intent = Intent(this, Ratings::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("SELLER_NAME", "Chamod Dinusha")
            startActivity(intent)
        }

        // ── Message Button → Go to Chat Screen ✅ ────────────────────────
        btnMessage.setOnClickListener {
            val intent = Intent(this, Messagechamod::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Call Button ──────────────────────────────────────────────────
        btnCall.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("📞 Call Chamod Dinusha")
                .setMessage("Choose a number to call:")

                .setPositiveButton("077 8047628") { dialog, _ ->
                    dialog.dismiss()

                    val i = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:0778047628")
                    )

                    startActivity(i)
                }

                .setNegativeButton("070 3462702") { dialog, _ ->
                    dialog.dismiss()

                    val i = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:0703462702")
                    )

                    startActivity(i)
                }

                .setNeutralButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

                .show()
        }
    }
}