package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Sellers : AppCompatActivity() {

    private val originalList = listOf(
        Seller("Jayalath Gunasinghe",  "Balangoda",   5.0f),
        Seller("Bimal Jayakodi",       "Nivithigala", 5.0f),
        Seller("Nuwan Deshan",         "Balangoda",   5.0f),
        Seller("Duneesh Matheesha",    "Rathnapura",  4.5f),
        Seller("Dilshan Senanayaka",   "Pelmadulla",  5.0f),
        Seller("Madusanka Shamal",     "Opanayaka",   4.5f),
        Seller("Kasun Tharanga",       "Kuruwita",    4.5f),
        Seller("Sachith Perera",       "Rathnapura",  5.0f),
        Seller("Thilina Bandara",      "Balangoda",   4.5f),
        Seller("Rohan Fernando",       "Kalawana",    4.5f),
        Seller("Prabath Wickrama",     "Pelmadulla",  4.0f),
        Seller("Sanjeewa Silva",       "Eheliyagoda", 4.5f),
        Seller("Lahiru Rajapaksha",    "Kuruwita",    4.0f),
        Seller("Dilan Madushanka",     "Nivithigala", 4.5f),
        Seller("Chathura Herath",      "Rathnapura",  4.0f),
        Seller("Nuwan Gunawardena",    "Opanayaka",   4.5f),
        Seller("Ruwan Dissanayake",    "Balangoda",   4.0f),
        Seller("Asanka Jayaweera",     "Kalawana",    4.5f),
        Seller("Buddhika Sanjeewa",    "Pelmadulla",  4.5f),
        Seller("Thilina Maduranga",    "Eheliyagoda", 4.0f),
        Seller("Manjula Rathnayake",   "Rathnapura",  4.5f),
        Seller("Vimukthi Prasad",      "Kuruwita",    4.0f),
        Seller("Isuru Lakshan",        "Nivithigala", 4.5f),
        Seller("Chathura Seneviratne", "Balangoda",   4.0f),
        Seller("Gayan Pathirana",      "Kalawana",    4.5f),
        Seller("Harsha Gunathilake",   "Opanayaka",   4.0f),
        Seller("Priyantha Kumara",     "Eheliyagoda", 5.0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sellers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username   = intent.getStringExtra("USERNAME") ?: "User"
        val ivBack     = findViewById<ImageView>(R.id.iv_back_sellers)
        val searchView = findViewById<SearchView>(R.id.searchView_sellers)
        val recycler   = findViewById<RecyclerView>(R.id.recyclerView_sellers)

        // ── Setup RecyclerView ───────────────────────────────────────────
        val adapter = SellerAdapter(originalList.toMutableList()) { seller ->
            AlertDialog.Builder(this)
                .setTitle("👤 ${seller.name}")
                .setMessage(
                    "📍 Region  : ${seller.region}\n" +
                            "⭐ Rating  : ${seller.rating}/5\n\n" +
                            "Would you like to contact this seller?"
                )
                .setPositiveButton("💬 Message") { dialog, _ ->
                    dialog.dismiss()
                    // ✅ Passes seller name → chat shows correct name
                    val intent = Intent(this, Messagechamod::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("MINER_NAME", seller.name)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // ── Back ─────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Search ───────────────────────────────────────────────────────
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter(query ?: "", originalList)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "", originalList)
                return true
            }
        })
    }
}