package com.example.gemlink

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class YourListingAdapter(
    private val items: MutableList<ListingItem>,
    private val onMarkSold: (Int) -> Unit,
    private val onShare: (ListingItem) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<YourListingAdapter.YourListingViewHolder>() {

    inner class YourListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGemImage   : ImageView = view.findViewById(R.id.iv_gem_image)
        val tvGemName    : TextView  = view.findViewById(R.id.tv_gem_name)
        val tvGemPrice   : TextView  = view.findViewById(R.id.tv_gem_price)
        val tvListedDate : TextView  = view.findViewById(R.id.tv_listed_date)
        val btnMarkSold  : Button    = view.findViewById(R.id.btn_mark_sold)
        val btnShare     : Button    = view.findViewById(R.id.btn_share)
        val btnDelete    : Button    = view.findViewById(R.id.btn_delete_listing)
        val ivMore       : ImageView = view.findViewById(R.id.iv_more_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_your_listing, parent, false)
        return YourListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourListingViewHolder, position: Int) {
        val item = items[position]

        holder.ivGemImage.setImageResource(item.imageRes)
        holder.tvGemName.text    = item.gemName
        holder.tvGemPrice.text   = item.price
        holder.tvListedDate.text = "Listed on ${item.listedDate}"

        // ── Mark as Sold state ───────────────────────────────────────────
        if (item.isSold) {
            holder.btnMarkSold.text      = "✔ Sold"
            holder.btnMarkSold.isEnabled = false
            holder.tvGemName.alpha       = 0.5f
            holder.tvGemPrice.alpha      = 0.5f
        } else {
            holder.btnMarkSold.text      = "Mark as sold"
            holder.btnMarkSold.isEnabled = true
            holder.tvGemName.alpha       = 1.0f
            holder.tvGemPrice.alpha      = 1.0f
        }

        // ── Mark as Sold click ───────────────────────────────────────────
        holder.btnMarkSold.setOnClickListener {
            onMarkSold(holder.adapterPosition)
        }

        // ── Share click ──────────────────────────────────────────────────
        holder.btnShare.setOnClickListener {
            onShare(item)
        }

        // ── Delete click ─────────────────────────────────────────────────
        holder.btnDelete.setOnClickListener {
            onDelete(holder.adapterPosition)
        }

        // ── Three dot popup menu ─────────────────────────────────────────
        holder.ivMore.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(view.context, view)
            popup.menu.add("Mark as sold")
            popup.menu.add("Share")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Mark as sold" -> onMarkSold(holder.adapterPosition)
                    "Share"        -> onShare(item)
                    "Delete"       -> onDelete(holder.adapterPosition)
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount() = items.size

    // ── Mark item as sold ────────────────────────────────────────────────
    fun markAsSold(position: Int) {
        items[position].isSold = true
        notifyItemChanged(position)
    }

    // ── Delete item ──────────────────────────────────────────────────────
    fun deleteItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }
}