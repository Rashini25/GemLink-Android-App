package com.example.gemlink

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Feedback : AppCompatActivity() {

    private val feedbackList = listOf(
        FeedbackItem(
            "Buyer 1",
            "Excellent quality gemstones! I purchased a Blue Sapphire and it was exactly as shown in the photos. Great shine and secure packaging. Very happy with my purchase!",
            5.0f
        ),
        FeedbackItem(
            "Buyer 2",
            "Fast delivery and good service. The Yellow Sapphire arrived earlier than expected and the seller answered all my questions quickly. I just wish there were more size options.",
            5.0f
        ),
        FeedbackItem(
            "Buyer 3",
            "Authentic gems with certificates. I ordered an Emerald and received the authenticity certificate as promised. The stone is beautiful and worth the price. Highly recommend this seller.",
            5.0f
        ),
        FeedbackItem(
            "Buyer 4",
            "Nice app and easy to use. The app is simple to navigate and has clear photos and descriptions. Checkout process was smooth. Looking forward to buying again.",
            5.0f
        ),
        FeedbackItem(
            "Buyer 5",
            "Good quality but shipping took a bit longer than expected. Overall satisfied with the product.",
            4.0f
        ),
        FeedbackItem(
            "Buyer 6",
            "The gem color was slightly different from the photo but still beautiful. Customer support was helpful.",
            4.0f
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ── Back ─────────────────────────────────────────────────────────
        findViewById<ImageView>(R.id.iv_back_feedback).setOnClickListener { finish() }

        // ── RecyclerView ─────────────────────────────────────────────────
        val recycler = findViewById<RecyclerView>(R.id.recyclerView_feedback)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = FeedbackAdapter(feedbackList)
    }
}