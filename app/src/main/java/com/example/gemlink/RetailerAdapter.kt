package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RetailerAdapter(
    private var retailers: MutableList<Retailer>,
    private val onItemClick: (Retailer) -> Unit
) : RecyclerView.Adapter<RetailerAdapter.RetailerViewHolder>() {

    inner class RetailerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName   : TextView  = view.findViewById(R.id.tv_miner_name)
        val tvRegion : TextView  = view.findViewById(R.id.tv_miner_region)
        val rbRating : RatingBar = view.findViewById(R.id.rb_miner_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetailerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_miner, parent, false)
        return RetailerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RetailerViewHolder, position: Int) {
        val retailer = retailers[position]
        holder.tvName.text     = retailer.name
        holder.tvRegion.text   = retailer.region
        holder.rbRating.rating = retailer.rating
        holder.itemView.setOnClickListener { onItemClick(retailer) }
    }

    override fun getItemCount() = retailers.size

    fun filter(query: String, originalList: List<Retailer>) {
        retailers = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.lowercase().contains(query.lowercase()) ||
                        it.region.lowercase().contains(query.lowercase())
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}