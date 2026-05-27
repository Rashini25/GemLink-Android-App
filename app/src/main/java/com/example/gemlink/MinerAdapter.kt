package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MinerAdapter(
    private var miners: MutableList<Miner>,
    private val onItemClick: (Miner) -> Unit
) : RecyclerView.Adapter<MinerAdapter.MinerViewHolder>() {

    inner class MinerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName   : TextView  = view.findViewById(R.id.tv_miner_name)
        val tvRegion : TextView  = view.findViewById(R.id.tv_miner_region)
        val rbRating : RatingBar = view.findViewById(R.id.rb_miner_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_miner, parent, false)
        return MinerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MinerViewHolder, position: Int) {
        val miner = miners[position]
        holder.tvName.text    = miner.name
        holder.tvRegion.text  = miner.region
        holder.rbRating.rating = miner.rating
        holder.itemView.setOnClickListener { onItemClick(miner) }
    }

    override fun getItemCount() = miners.size

    // ── Filter for search ────────────────────────────────────────────────
    fun filter(query: String, originalList: List<Miner>) {
        miners = if (query.isEmpty()) {
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