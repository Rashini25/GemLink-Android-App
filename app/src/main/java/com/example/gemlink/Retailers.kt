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

class Retailers : AppCompatActivity() {

    private val originalList = listOf(
        Retailer("Kavindra Rashen",      "Balangoda",   5.0f),
        Retailer("Ravishan Perera",      "Nivithigala", 5.0f),
        Retailer("Avishka Deshan",       "Balangoda",   5.0f),
        Retailer("Pasindu Priyalanka",   "Rathnapura",  5.0f),
        Retailer("Ashan Shakeera",       "Pelmadulla",  5.0f),
        Retailer("Yashohara Ganewatta",  "Opanayaka",   5.0f),
        Retailer("Seshan Sandeepa",      "Kuruwita",    5.0f),
        Retailer("Nimal Rajapaksha",     "Rathnapura",  4.5f),
        Retailer("Kamal Seneviratne",    "Balangoda",   4.5f),
        Retailer("Roshan Bandara",       "Kalawana",    4.5f),
        Retailer("Tharanga Kumara",      "Pelmadulla",  4.5f),
        Retailer("Pradeep Jayaweera",    "Eheliyagoda", 4.5f),
        Retailer("Sampath Fernando",     "Kuruwita",    4.0f),
        Retailer("Lasith Wickrama",      "Nivithigala", 4.0f),
        Retailer("Chaminda Dissanayake", "Rathnapura",  4.0f),
        Retailer("Dinesh Rathnayake",    "Opanayaka",   4.0f),
        Retailer("Ruwan Maduranga",      "Balangoda",   4.0f),
        Retailer("Asanka Herath",        "Kalawana",    4.5f),
        Retailer("Buddhika Lakshan",     "Pelmadulla",  4.5f),
        Retailer("Thilina Prasad",       "Eheliyagoda", 4.0f),
        Retailer("Manjula Gunawardena",  "Rathnapura",  4.5f),
        Retailer("Vimukthi Sanjeewa",    "Kuruwita",    4.0f),
        Retailer("Isuru Pathirana",      "Nivithigala", 4.5f),
        Retailer("Chathura Madushanka",  "Balangoda",   4.0f),
        Retailer("Gayan Gunathilake",    "Kalawana",    4.5f),
        Retailer("Harsha Jayarathna",    "Opanayaka",   4.0f),
        Retailer("Priyantha Silva",      "Eheliyagoda", 5.0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_retailers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username   = intent.getStringExtra("USERNAME") ?: "User"
        val ivBack     = findViewById<ImageView>(R.id.imageView117)
        val searchView = findViewById<SearchView>(R.id.searchView_retailers)
        val recycler   = findViewById<RecyclerView>(R.id.recyclerView_retailers)

        // ── Setup RecyclerView ───────────────────────────────────────────
        val adapter = RetailerAdapter(originalList.toMutableList()) { retailer ->
            AlertDialog.Builder(this)
                .setTitle("👤 ${retailer.name}")
                .setMessage(
                    "📍 Region  : ${retailer.region}\n" +
                            "⭐ Rating  : ${retailer.rating}/5\n\n" +
                            "Would you like to contact this retailer?"
                )
                .setPositiveButton("💬 Message") { dialog, _ ->
                    dialog.dismiss()
                    // ✅ Fixed — using MINER_NAME key
                    val intent = Intent(this, Messagechamod::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("MINER_NAME", retailer.name)
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