package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GemAdapter(
    private var gems: MutableList<GemItem>,
    private val onItemClick: (GemItem) -> Unit
) : RecyclerView.Adapter<GemAdapter.GemViewHolder>() {

    inner class GemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage  : ImageView = view.findViewById(R.id.iv_gem_card_image)
        val tvName   : TextView  = view.findViewById(R.id.tv_gem_card_name)
        val tvPrice  : TextView  = view.findViewById(R.id.tv_gem_card_price)
        val tvRegion : TextView  = view.findViewById(R.id.tv_gem_card_region)
        val tvSeller : TextView  = view.findViewById(R.id.tv_gem_card_seller)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gem_card, parent, false)
        return GemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GemViewHolder, position: Int) {
        val gem = gems[position]
        holder.ivImage.setImageResource(gem.imageRes)
        holder.tvName.text   = gem.name
        holder.tvPrice.text  = gem.price
        holder.tvRegion.text = gem.region
        holder.tvSeller.text = gem.seller
        holder.itemView.setOnClickListener { onItemClick(gem) }
    }

    override fun getItemCount() = gems.size

    fun filter(query: String, originalList: List<GemItem>) {
        gems = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.lowercase().contains(query.lowercase()) ||
                        it.price.contains(query) ||
                        it.region.lowercase().contains(query.lowercase()) ||
                        it.seller.lowercase().contains(query.lowercase())
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun filterByType(type: String, originalList: List<GemItem>) {
        gems = if (type == "All") {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.lowercase().contains(type.lowercase())
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}