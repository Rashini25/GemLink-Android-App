package com.example.gemlink

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Messagechamod : AppCompatActivity() {

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messagechamod)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username      = intent.getStringExtra("USERNAME") ?: "User"

        // ✅ Receives miner name — defaults to Chamod if coming from Chamod.kt
        val minerName     = intent.getStringExtra("MINER_NAME") ?: "Chamod Dinusha"

        val ivBack        = findViewById<ImageView>(R.id.imageView108)
        val ivInfo        = findViewById<ImageView>(R.id.imageView110)
        val tvContactName = findViewById<TextView>(R.id.textView109)
        val recycler      = findViewById<RecyclerView>(R.id.recyclerView)
        val etMessage     = findViewById<EditText>(R.id.editTextText25)
        val ivPlus        = findViewById<ImageView>(R.id.imageView112)
        val ivSend        = findViewById<ImageView>(R.id.imageView113)

        // ✅ Set miner name in top bar dynamically
        tvContactName.text = minerName

        // ── Setup RecyclerView ───────────────────────────────────────────
        chatAdapter = ChatAdapter(messageList)
        recycler.apply {
            layoutManager = LinearLayoutManager(this@Messagechamod).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        // ✅ Opening message shows miner's name
        addMessage("👋 Hello! I am $minerName. How can I help you?", isSentByMe = false)

        // ── Back ─────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Info ─────────────────────────────────────────────────────────
        ivInfo.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("ℹ️ $minerName")
                .setMessage(
                    "💎 Gem Miner / Seller\n" +
                            "📍 Rathnapura Region\n\n" +
                            "Verified GemLink Seller ✅"
                )
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Plus → Attachments ───────────────────────────────────────────
        ivPlus.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("📎 Attach")
                .setItems(
                    arrayOf(
                        "📷 Send Photo",
                        "📍 Send Location",
                        "💎 Share Gem Listing"
                    )
                ) { dialog, which ->
                    dialog.dismiss()
                    when (which) {
                        0 -> addMessage("📷 [Photo]", isSentByMe = true)
                        1 -> addMessage("📍 [Location shared]", isSentByMe = true)
                        2 -> addMessage("💎 [Gem listing shared]", isSentByMe = true)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Send ─────────────────────────────────────────────────────────
        ivSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            addMessage(text, isSentByMe = true)
            etMessage.text.clear()

            // ✅ Auto reply uses miner name
            recycler.postDelayed({
                val replies = listOf(
                    "Hello $username! Thank you for contacting me 😊",
                    "Yes, my gems are still available!",
                    "You can visit my shop for more details.",
                    "I can arrange delivery for you.",
                    "Please feel free to ask any questions!"
                )
                addMessage(replies.random(), isSentByMe = false)
            }, 1000)
        }
    }

    // ── Add message helper ───────────────────────────────────────────────
    private fun addMessage(text: String, isSentByMe: Boolean) {
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        messageList.add(ChatMessage(text, isSentByMe, time))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        findViewById<RecyclerView>(R.id.recyclerView)
            .scrollToPosition(messageList.size - 1)
    }
}