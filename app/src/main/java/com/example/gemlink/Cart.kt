package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Cart : AppCompatActivity() {

    private val originalList = mutableListOf(
        CartItem("Hessonite",       "LKR 1500000.00", "01/01/2026", R.drawable.hessonite),
        CartItem("Blue Sapphire",   "LKR 2500000.00", "01/02/2026", R.drawable.bluesapphire),
        CartItem("Amethyst",        "LKR 1000000.00", "12/02/2026", R.drawable.amethyst),
        CartItem("Yellow Sapphire", "LKR 2000000.00", "14/01/2026", R.drawable.yellowsapphire),
        CartItem("Ruby",            "LKR 3500000.00", "15/01/2026", R.drawable.ruby),
        CartItem("Emerald",         "LKR 8000000.00", "20/01/2026", R.drawable.emerald),
        CartItem("Cat's Eye",       "LKR 4500000.00", "22/01/2026", R.drawable.catseye),
        CartItem("Alexandrite",     "LKR 8000000.00", "25/01/2026", R.drawable.alexandrite),
        CartItem("Spinel",          "LKR 1200000.00", "28/01/2026", R.drawable.spinel),
        CartItem("Tourmaline",      "LKR 900000.00",  "30/01/2026", R.drawable.tourmaline),
        CartItem("Moonstone",       "LKR 750000.00",  "02/02/2026", R.drawable.moonstone),
        CartItem("Garnet",          "LKR 650000.00",  "05/02/2026", R.drawable.garnet),
        CartItem("Topaz",           "LKR 550000.00",  "08/02/2026", R.drawable.topaz),
        CartItem("Aquamarine",      "LKR 1800000.00", "10/02/2026", R.drawable.aquamarine)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val plan     = intent.getStringExtra("PLAN") ?: "FREE"

        // ── Bind Views ────────────────────────────────────────────────────
        val ivBack   = findViewById<ImageView>(R.id.imageView122)
        val searchView = findViewById<SearchView>(R.id.searchView3)
        val recycler   = findViewById<RecyclerView>(R.id.recyclerView2)

        // ── Bottom Nav (LinearLayout) ─────────────────────────────────────
        val ivNavHome    = findViewById<LinearLayout>(R.id.imageView118)
        val ivNavShop    = findViewById<LinearLayout>(R.id.imageView123)
        val ivNavCart    = findViewById<LinearLayout>(R.id.imageView119)
        val ivNavProfile = findViewById<LinearLayout>(R.id.imageView120)

        // ── Setup RecyclerView ────────────────────────────────────────────
        lateinit var adapter: CartAdapter

        adapter = CartAdapter(
            originalList.toMutableList(),
            onDeleteClick = { item, position ->
                AlertDialog.Builder(this)
                    .setTitle("🗑️ Remove Item")
                    .setMessage("Are you sure you want to remove\n${item.name} from your cart?")
                    .setPositiveButton("Remove") { dialog, _ ->
                        dialog.dismiss()
                        adapter.removeItem(position)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            },
            onMoreDetailsClick = { item ->
                AlertDialog.Builder(this)
                    .setTitle("💎 ${item.name}")
                    .setMessage(
                        "💰 Price   : ${item.price}\n" +
                                "📅 Added   : ${item.dateAdded}\n\n" +
                                "Would you like to proceed to purchase?"
                    )
                    .setPositiveButton("Buy Now") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(this, Card::class.java)
                        intent.putExtra("USERNAME", username)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)

        // ── Back ──────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

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

        // ── Bottom Navigation ─────────────────────────────────────────────
        ivNavHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }

        ivNavShop.setOnClickListener {
            val intent = Intent(this, Marketplace::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        ivNavCart.setOnClickListener {
            // Already on Cart
        }

        ivNavProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("PLAN", plan)
            startActivity(intent)
        }
    }
}