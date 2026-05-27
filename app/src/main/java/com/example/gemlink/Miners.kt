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

class Miners : AppCompatActivity() {

    private val originalList = listOf(
        Miner("Chamod Jayashan",      "Balangoda",    5.0f),
        Miner("Dulanka Dilshan",      "Nivithigala",  5.0f),
        Miner("Kumara Jayarathna",    "Balangoda",    5.0f),
        Miner("Sunil Deepal",         "Rathnapura",   5.0f),
        Miner("Ushan Galkanda",       "Pelmadulla",   5.0f),
        Miner("Ajith Kumara",         "Opanayaka",    5.0f),
        Miner("Sameera Namal",        "Kuruwita",     5.0f),
        Miner("Nimal Perera",         "Rathnapura",   4.5f),
        Miner("Kamal Bandara",        "Balangoda",    4.5f),
        Miner("Roshan Fernando",      "Kalawana",     4.5f),
        Miner("Tharanga Wickrama",    "Pelmadulla",   4.5f),
        Miner("Pradeep Silva",        "Eheliyagoda",  4.5f),
        Miner("Sampath Rajapaksha",   "Kuruwita",     4.0f),
        Miner("Lasith Madushanka",    "Nivithigala",  4.0f),
        Miner("Chaminda Herath",      "Rathnapura",   4.0f),
        Miner("Dinesh Gunawardena",   "Opanayaka",    4.0f),
        Miner("Ruwan Dissanayake",    "Balangoda",    4.0f),
        Miner("Asanka Jayaweera",     "Kalawana",     4.5f),
        Miner("Buddhika Sanjeewa",    "Pelmadulla",   4.5f),
        Miner("Thilina Maduranga",    "Eheliyagoda",  4.0f),
        Miner("Manjula Rathnayake",   "Rathnapura",   4.5f),
        Miner("Vimukthi Prasad",      "Kuruwita",     4.0f),
        Miner("Isuru Lakshan",        "Nivithigala",  4.5f),
        Miner("Chathura Seneviratne", "Balangoda",    4.0f),
        Miner("Gayan Pathirana",      "Kalawana",     4.5f),
        Miner("Harsha Gunathilake",   "Opanayaka",    4.0f),
        Miner("Priyantha Kumara",     "Eheliyagoda",  5.0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_miners)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username   = intent.getStringExtra("USERNAME") ?: "User"
        val ivBack     = findViewById<ImageView>(R.id.iv_back_miners)
        val searchView = findViewById<SearchView>(R.id.searchView_miners)
        val recycler   = findViewById<RecyclerView>(R.id.recyclerView_miners)

        // ── Setup RecyclerView ───────────────────────────────────────────
        val adapter = MinerAdapter(originalList.toMutableList()) { miner ->
            // ── Click on miner → show details dialog ─────────────────────
            AlertDialog.Builder(this)
                .setTitle("👤 ${miner.name}")
                .setMessage(
                    "📍 Region  : ${miner.region}\n" +
                            "⭐ Rating  : ${miner.rating}/5\n\n" +
                            "Would you like to contact this miner?"
                )
                .setPositiveButton("💬 Message") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Messagechamod::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("MINER_NAME", miner.name)
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