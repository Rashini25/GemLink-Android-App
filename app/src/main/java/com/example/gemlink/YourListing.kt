package com.example.gemlink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class YourListing : AppCompatActivity() {

    // ── Firebase ──────────────────────────────────────────────────────────
    private val database = FirebaseDatabase.getInstance()

    // ── Your original hardcoded list — NEVER removed ─────────────────────
    private val hardcodedList = mutableListOf(
        ListingItem("Amber",         "LKR 10000000.00", "01/01/2026", R.drawable.amber),
        ListingItem("Diamond Ruby",  "LKR 18000000.00", "12/02/2026", R.drawable.ruby),
        ListingItem("Tanzanite",     "LKR 16000000.00", "14/01/2026", R.drawable.tanzanite),
        ListingItem("Blue Sapphire", "LKR 22000000.00", "05/03/2026", R.drawable.sapphire),
        ListingItem("Emerald",       "LKR 14000000.00", "20/02/2026", R.drawable.emerald),
        ListingItem("Hessonite",     "LKR 8000000.00",  "10/01/2026", R.drawable.hessonite)
    )

    // ── Firebase listings loaded dynamically ──────────────────────────────
    private val firebaseList = mutableListOf<ListingItem>()

    // ── Combined list shown in RecyclerView ───────────────────────────────
    // Firebase new listings appear at TOP, hardcoded ones below
    private val displayList  = mutableListOf<ListingItem>()

    // Map of "gemName-listedDate" → Firebase key for delete/sold operations
    private val firebaseKeyMap = mutableMapOf<String, String>()

    private lateinit var adapter : YourListingAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_your_listing)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = intent.getStringExtra("USERNAME") ?: ""

        // Safety check
        if (username.isEmpty()) {
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        val ivBack           = findViewById<ImageView>(R.id.iv_back_your_listing)
        val btnCreateListing = findViewById<Button>(R.id.btn_create_listing)
        val searchView       = findViewById<SearchView>(R.id.searchView_your_listing)
        recycler             = findViewById(R.id.recyclerView_your_listing)

        // ── Build initial display list with hardcoded items ──────────────
        // Firebase items will be added on top once they load
        rebuildDisplayList()

        // ── Setup adapter ────────────────────────────────────────────────
        setupAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter       = adapter

        // ── Load Firebase listings on top ────────────────────────────────
        loadListingsFromFirebase()

        // ── Back ─────────────────────────────────────────────────────────
        ivBack.setOnClickListener { finish() }

        // ── Create Listing ───────────────────────────────────────────────
        btnCreateListing.setOnClickListener {
            val intent = Intent(this, Listing::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // ── Search — filters combined list ───────────────────────────────
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.lowercase() ?: ""

                // Filter both firebase + hardcoded lists together
                val allItems = firebaseList + hardcodedList
                val filtered = allItems.filter {
                    it.gemName.lowercase().contains(query)
                }.toMutableList()

                displayList.clear()
                displayList.addAll(filtered)
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }

    // ════════════════════════════════════════════════════════════════════
    // Rebuilds the displayList:
    // Firebase listings (newest first) on TOP
    // then hardcoded listings below
    // ════════════════════════════════════════════════════════════════════
    private fun rebuildDisplayList() {
        displayList.clear()
        displayList.addAll(firebaseList)   // Firebase items first (newest)
        displayList.addAll(hardcodedList)  // Your original 6 always at bottom
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Load listings for this user in real time
    // Path: listings → username → each child is one listing
    // ════════════════════════════════════════════════════════════════════
    private fun loadListingsFromFirebase() {
        database.getReference("listings")
            .child(username)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseList.clear()
                    firebaseKeyMap.clear()

                    for (child in snapshot.children) {
                        val firebaseKey = child.key ?: continue

                        val gemName    = child.child("gemName").value?.toString()    ?: "Unknown"
                        val price      = child.child("price").value?.toString()      ?: "LKR 0"
                        val listedDate = child.child("listedDate").value?.toString() ?: "-"
                        val status     = child.child("status").value?.toString()     ?: "active"

                        val item = ListingItem(
                            gemName    = gemName,
                            price      = price,
                            listedDate = listedDate,
                            imageRes   = R.drawable.hessonite, // default placeholder
                            isSold     = status == "sold"
                        )

                        firebaseList.add(item)
                        firebaseKeyMap["$gemName-$listedDate"] = firebaseKey
                    }

                    // Newest Firebase listing at top
                    firebaseList.reverse()

                    // Rebuild combined list and refresh RecyclerView
                    rebuildDisplayList()
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@YourListing,
                        "Failed to load listings: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Mark as sold
    // Only works for Firebase listings, not hardcoded ones
    // ════════════════════════════════════════════════════════════════════
    private fun markSoldInFirebase(position: Int) {
        val item        = displayList[position]
        val firebaseKey = firebaseKeyMap["${item.gemName}-${item.listedDate}"]

        // If no Firebase key found, this is a hardcoded item — handle locally
        if (firebaseKey == null) {
            adapter.markAsSold(position)
            Toast.makeText(this, "✔ Marked as sold", Toast.LENGTH_SHORT).show()
            return
        }

        database.getReference("listings")
            .child(username)
            .child(firebaseKey)
            .child("status")
            .setValue("sold")
            .addOnSuccessListener {
                Toast.makeText(this, "✔ Marked as sold", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ════════════════════════════════════════════════════════════════════
    // FIREBASE — Delete listing
    // Only deletes from Firebase listings, not hardcoded ones
    // ════════════════════════════════════════════════════════════════════
    private fun deleteFromFirebase(position: Int) {
        val item        = displayList[position]
        val firebaseKey = firebaseKeyMap["${item.gemName}-${item.listedDate}"]

        // If no Firebase key found, this is a hardcoded item — handle locally
        if (firebaseKey == null) {
            adapter.deleteItem(position)
            Toast.makeText(this, "🗑️ Listing deleted", Toast.LENGTH_SHORT).show()
            return
        }

        database.getReference("listings")
            .child(username)
            .child(firebaseKey)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "🗑️ Listing deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ── Setup adapter ────────────────────────────────────────────────────
    private fun setupAdapter() {
        adapter = YourListingAdapter(
            displayList,
            onMarkSold = { position ->
                AlertDialog.Builder(this)
                    .setTitle("✔ Mark as Sold")
                    .setMessage("Mark \"${displayList[position].gemName}\" as sold?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        markSoldInFirebase(position)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            },
            onShare = { item ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this gem on GemLink!\n" +
                                "💎 ${item.gemName}\n" +
                                "💰 ${item.price}\n" +
                                "📅 Listed on ${item.listedDate}"
                    )
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            },
            onDelete = { position ->
                AlertDialog.Builder(this)
                    .setTitle("🗑️ Delete Listing")
                    .setMessage(
                        "Are you sure you want to delete " +
                                "\"${displayList[position].gemName}\"?"
                    )
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        deleteFromFirebase(position)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        )
    }
}