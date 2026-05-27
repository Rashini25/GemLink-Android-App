package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SellerAdapter(
    private var sellers: MutableList<Seller>,
    private val onItemClick: (Seller) -> Unit
) : RecyclerView.Adapter<SellerAdapter.SellerViewHolder>() {

    inner class SellerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName   : TextView  = view.findViewById(R.id.tv_miner_name)
        val tvRegion : TextView  = view.findViewById(R.id.tv_miner_region)
        val rbRating : RatingBar = view.findViewById(R.id.rb_miner_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_miner, parent, false)
        return SellerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        val seller = sellers[position]
        holder.tvName.text     = seller.name
        holder.tvRegion.text   = seller.region
        holder.rbRating.rating = seller.rating
        holder.itemView.setOnClickListener { onItemClick(seller) }
    }

    override fun getItemCount() = sellers.size

    fun filter(query: String, originalList: List<Seller>) {
        sellers = if (query.isEmpty()) {
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