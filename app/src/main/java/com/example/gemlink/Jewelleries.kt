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

class Jewelleries : AppCompatActivity() {

    private val originalList = listOf(
        Jewellery("Rathnapura Gold House",  "Balangoda",   5.0f),
        Jewellery("Nivithigala Jewellers",  "Nivithigala", 5.0f),
        Jewellery("Rishana Gol House",      "Balangoda",   5.0f),
        Jewellery("Dedigama Jewellers",     "Rathnapura",  4.5f),
        Jewellery("Vogue Jewellers",        "Pelmadulla",  5.0f),
        Jewellery("Sarita Jewellers",       "Opanayaka",   4.5f),
        Jewellery("Shama Jewellers",        "Kuruwita",    4.5f),
        Jewellery("Ceylon Gem House",       "Rathnapura",  5.0f),
        Jewellery("Sapphire Jewellers",     "Balangoda",   4.5f),
        Jewellery("Golden Crown Jewellers", "Kalawana",    4.5f),
        Jewellery("Royal Gem Studio",       "Pelmadulla",  4.0f),
        Jewellery("Star Jewellers",         "Eheliyagoda", 4.5f),
        Jewellery("Diamond Palace",         "Kuruwita",    4.0f),
        Jewellery("Gem Art Jewellers",      "Nivithigala", 4.5f),
        Jewellery("Lanka Jewel House",      "Rathnapura",  4.0f),
        Jewellery("Precious Stone Studio",  "Opanayaka",   4.5f),
        Jewellery("Heritage Jewellers",     "Balangoda",   4.0f),
        Jewellery("Crystal Jewellers",      "Kalawana",    4.5f),
        Jewellery("Golden Touch Jewellers", "Pelmadulla",  4.5f),
        Jewellery("Gem Valley Studio",      "Eheliyagoda", 4.0f),
        Jewellery("Silver Crest Jewellers", "Rathnapura",  4.5f),
        Jewellery("Pearl Jewel House",      "Kuruwita",    4.0f),
        Jewellery("Island Gem Studio",      "Nivithigala", 4.5f),
        Jewellery("Sunrise Jewellers",      "Balangoda",   4.0f),
        Jewellery("Moonstone Jewellers",    "Kalawana",    4.5f),
        Jewellery("Blue Lotus Jewellers",   "Opanayaka",   4.0f),
        Jewellery("Radiant Gem House",      "Eheliyagoda", 5.0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jewelleries)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username   = intent.getStringExtra("USERNAME") ?: "User"
        val ivBack     = findViewById<ImageView>(R.id.imageView115)
        val searchView = findViewById<SearchView>(R.id.searchview1)
        val recycler   = findViewById<RecyclerView>(R.id.recycler1)

        // ── Setup RecyclerView ───────────────────────────────────────────
        val adapter = JewelleryAdapter(originalList.toMutableList()) { jewellery ->
            AlertDialog.Builder(this)
                .setTitle("💍 ${jewellery.name}")
                .setMessage(
                    "📍 Region  : ${jewellery.region}\n" +
                            "⭐ Rating  : ${jewellery.rating}/5\n\n" +
                            "Would you like to contact this jeweller?"
                )
                .setPositiveButton("💬 Message") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, Messagechamod::class.java)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("MINER_NAME", jewellery.name)
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