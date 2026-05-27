package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Notifications : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username     = intent.getStringExtra("USERNAME") ?: "User"
        val ivBack       = findViewById<ImageView>(R.id.iv_back_notifications)
        val ivSettings   = findViewById<ImageView>(R.id.iv_notif_settings)
        val tvNotifCount = findViewById<TextView>(R.id.tv_notif_count)
        val recycler     = findViewById<RecyclerView>(R.id.recyclerView_notifications)

        // ── New Notifications ─────────────────────────────────────────────
        val newNotifications = mutableListOf(
            NotificationItem(
                "Hi, can you tell me the exact carat and dimensions of this Blue Sapphire?",
                "New"
            ),
            NotificationItem(
                "Hello, is the price negotiable for the Hessonite listed?",
                "New"
            ),
            NotificationItem(
                "Is the Ruby still available? I would like to purchase it immediately.",
                "New"
            ),
            NotificationItem(
                "Can you share more photos of the Alexandrite under different lighting?",
                "New"
            ),
            NotificationItem(
                "I'm interested in the Spinel. What is the treatment status of this gem?",
                "New"
            ),
            NotificationItem(
                "Your listing for Cat's Eye has received 12 new views today! 🔥",
                "New"
            )
        )

        // ── Today Notifications ───────────────────────────────────────────
        val todayNotifications = mutableListOf(
            NotificationItem(
                "Can you provide a GIA or AGL certificate for the Emerald gem?",
                "Today"
            ),
            NotificationItem(
                "Hi, do you ship internationally? How long will delivery take to Germany?",
                "Today"
            ),
            NotificationItem(
                "I'd like to buy 3 pieces of Tourmaline in bulk. Can you give a discount?",
                "Today"
            ),
            NotificationItem(
                "What is the origin of the Yellow Sapphire? Is it Ceylon certified?",
                "Today"
            ),
            NotificationItem(
                "Your Amethyst listing has been featured on the GemLink homepage! 🎉",
                "Today"
            ),
            NotificationItem(
                "A buyer has added your Hessonite to their wishlist. Follow up now!",
                "Today"
            ),
            NotificationItem(
                "Payment of LKR 1,500,000 received for your Hessonite gem. ✅",
                "Today"
            )
        )

        // ── Yesterday Notifications ───────────────────────────────────────
        val yesterdayNotifications = mutableListOf(
            NotificationItem(
                "Can you cut or set this Amethyst into a ring? If yes, what's the additional cost?",
                "Yesterday"
            ),
            NotificationItem(
                "Hello, do you have more pieces of Yellow Sapphire in similar quality?",
                "Yesterday"
            ),
            NotificationItem(
                "Is the Moonstone natural or synthetic? Please clarify before I proceed.",
                "Yesterday"
            ),
            NotificationItem(
                "Hi, I represent a jewellery company. Can we discuss a long-term supply deal?",
                "Yesterday"
            ),
            NotificationItem(
                "Your profile has been verified by GemLink! Your badge is now active. ✅",
                "Yesterday"
            ),
            NotificationItem(
                "New review posted on your profile: ⭐⭐⭐⭐⭐ — Excellent quality gem!",
                "Yesterday"
            )
        )

        // ── This Week Notifications ───────────────────────────────────────
        val thisWeekNotifications = mutableListOf(
            NotificationItem(
                "Your listing for Alexandrite has expired. Renew it to keep it visible.",
                "This Week"
            ),
            NotificationItem(
                "GemLink Price Alert: Blue Sapphire market prices have risen by 8% this week.",
                "This Week"
            ),
            NotificationItem(
                "A buyer from Japan is interested in your Garnet. Reply within 24 hours.",
                "This Week"
            ),
            NotificationItem(
                "Reminder: You have 2 pending inquiries that have not been responded to yet.",
                "This Week"
            ),
            NotificationItem(
                "Your subscription plan is expiring in 3 days. Renew to avoid disruption.",
                "This Week"
            ),
            NotificationItem(
                "Flash Sale Alert 🔔 — List gems this weekend and get 50% off listing fees!",
                "This Week"
            )
        )

        // ── Build Full List with Headers ──────────────────────────────────
        val allItems = mutableListOf<Any>()

        allItems.add("New")
        allItems.addAll(newNotifications)

        allItems.add("Today")
        allItems.addAll(todayNotifications)

        allItems.add("Yesterday")
        allItems.addAll(yesterdayNotifications)

        allItems.add("This Week")
        allItems.addAll(thisWeekNotifications)

        // ── Notification Count ────────────────────────────────────────────
        fun updateCount() {
            val remaining = allItems.count { it is NotificationItem }
            tvNotifCount.text = "You have $remaining notifications"
        }
        updateCount()

        // ── Adapter ───────────────────────────────────────────────────────
        lateinit var adapter: NotificationAdapter

        adapter = NotificationAdapter(
            allItems,
            onItemClick = { item ->
                AlertDialog.Builder(this)
                    .setTitle("🔔 Notification")
                    .setMessage(item.message)
                    .setPositiveButton("Reply") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(this, Messagechamod::class.java)
                        intent.putExtra("USERNAME", username)
                        intent.putExtra("MINER_NAME", "Buyer")
                        startActivity(intent)
                    }
                    .setNegativeButton("Dismiss") { dialog, _ -> dialog.dismiss() }
                    .show()
            },
            onDeleteClick = { position ->
                AlertDialog.Builder(this)
                    .setTitle("🗑️ Delete Notification")
                    .setMessage("Are you sure you want to delete this notification?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        adapter.deleteItem(position)
                        updateCount()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // ── Back ──────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Settings ──────────────────────────────────────────────────────
        ivSettings.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("🔔 Notification Settings")
                .setMultiChoiceItems(
                    arrayOf(
                        "New messages",
                        "Price alerts",
                        "Order updates",
                        "Promotions",
                        "Listing expiry reminders",
                        "Payment confirmations",
                        "Profile activity"
                    ),
                    booleanArrayOf(true, true, true, false, true, true, false)
                ) { _, _, _ -> }
                .setPositiveButton("Save") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(
                        this, "✅ Notification settings saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}