package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JewelleryAdapter(
    private var jewelleries: MutableList<Jewellery>,
    private val onItemClick: (Jewellery) -> Unit
) : RecyclerView.Adapter<JewelleryAdapter.JewelleryViewHolder>() {

    inner class JewelleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName   : TextView  = view.findViewById(R.id.tv_miner_name)
        val tvRegion : TextView  = view.findViewById(R.id.tv_miner_region)
        val rbRating : RatingBar = view.findViewById(R.id.rb_miner_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JewelleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_miner, parent, false)
        return JewelleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: JewelleryViewHolder, position: Int) {
        val jewellery = jewelleries[position]
        holder.tvName.text     = jewellery.name
        holder.tvRegion.text   = jewellery.region
        holder.rbRating.rating = jewellery.rating
        holder.itemView.setOnClickListener { onItemClick(jewellery) }
    }

    override fun getItemCount() = jewelleries.size

    fun filter(query: String, originalList: List<Jewellery>) {
        jewelleries = if (query.isEmpty()) {
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