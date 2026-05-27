package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Marketplace : AppCompatActivity() {

    private val originalList = listOf(
        GemItem("Hessonite",       "LKR 1500000.00", "Rathnapura",  "Chamod",  R.drawable.hessonite),
        GemItem("Blue Sapphire",   "LKR 2500000.00", "Balangoda",   "Rashini", R.drawable.bluesapphire),
        GemItem("Amethyst",        "LKR 1000000.00", "Pelmadulla",  "Dulanka", R.drawable.amethyst),
        GemItem("Yellow Sapphire", "LKR 2000000.00", "Kalawana",    "Renuka",  R.drawable.yellowsapphire),
        GemItem("Emerald",         "LKR 8000000.00", "Nivithigala", "Kumara",  R.drawable.emerald),
        GemItem("Alexandrite",     "LKR 9000000.00", "Rathnapura",  "Dammika", R.drawable.alexandrite),
        GemItem("Ruby",            "LKR 3500000.00", "Rathnapura",  "Saman",   R.drawable.ruby),
        GemItem("Cat's Eye",       "LKR 4500000.00", "Balangoda",   "Nimal",   R.drawable.catseye),
        GemItem("Spinel",          "LKR 1200000.00", "Kalawana",    "Kamal",   R.drawable.spinel),
        GemItem("Tourmaline",      "LKR 900000.00",  "Pelmadulla",  "Sunil",   R.drawable.tourmaline)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_marketplace)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username       = intent.getStringExtra("USERNAME") ?: "User"
        val plan           = intent.getStringExtra("PLAN") ?: "FREE"

        // ── Bind Views ────────────────────────────────────────────────────
        val ivProfile      = findViewById<ImageView>(R.id.iv_marketplace_profile)
        val ivPlus         = findViewById<ImageView>(R.id.iv_marketplace_plus)
        val tvTypeDropdown = findViewById<TextView>(R.id.tv_type_dropdown)
        val searchView     = findViewById<SearchView>(R.id.searchView_marketplace)
        val recycler       = findViewById<RecyclerView>(R.id.recyclerView_marketplace)

        // ── Bottom Nav (LinearLayout) ─────────────────────────────────────
        val ivNavHome    = findViewById<LinearLayout>(R.id.iv_nav_home_market)
        val ivNavShop    = findViewById<LinearLayout>(R.id.iv_nav_shop_market)
        val ivNavCart    = findViewById<LinearLayout>(R.id.iv_nav_cart_market)
        val ivNavProfile = findViewById<LinearLayout>(R.id.iv_nav_profile_market)

        // ── Setup RecyclerView ────────────────────────────────────────────
        lateinit var adapter: GemAdapter

        adapter = GemAdapter(originalList.toMutableList()) { gem ->
            AlertDialog.Builder(this)
                .setTitle("💎 ${gem.name}")
                .setMessage(
                    "💰 Price   : ${gem.price}\n" +
                            "📍 Region  : ${gem.region}\n" +
                            "👤 Seller  : ${gem.seller}\n\n" +
                            "Would you like to view full details?"
                )
                .setPositiveButton("View Details") { dialog, _ ->
                    dialog.dismiss()
                    if (gem.name == "Hessonite") {
                        val intent = Intent(this, Hessonite::class.java)
                        intent.putExtra("USERNAME", username)
                        startActivity(intent)
                    } else {
                        android.widget.Toast.makeText(
                            this, "${gem.name} details coming soon!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("🛒 Add to Cart") { dialog, _ ->
                    dialog.dismiss()
                    android.widget.Toast.makeText(
                        this, "✅ ${gem.name} added to cart!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
        }

        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter

        // ── Type Dropdown ─────────────────────────────────────────────────
        val gemTypes = arrayOf(
            "All", "Hessonite", "Blue Sapphire", "Amethyst",
            "Yellow Sapphire", "Emerald", "Alexandrite",
            "Ruby", "Cat's Eye", "Spinel", "Tourmaline"
        )

        tvTypeDropdown.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Filter by Gem Type")
                .setItems(gemTypes) { dialog, which ->
                    val selected = gemTypes[which]
                    tvTypeDropdown.text = selected
                    adapter.filterByType(selected, originalList)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // ── Search ────────────────────────────────────────────────────────
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

        // ── Profile Icon ──────────────────────────────────────────────────
        ivProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }

        // ── Plus Icon ─────────────────────────────────────────────────────
        ivPlus.setOnClickListener {
            val intent = Intent(this, Listing::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Bottom Navigation ─────────────────────────────────────────────
        ivNavHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }

        ivNavShop.setOnClickListener {
            // Already on Marketplace
        }

        ivNavCart.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }

        ivNavProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }
    }
}