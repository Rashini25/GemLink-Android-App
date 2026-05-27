package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val items: MutableList<Any>,
    private val onItemClick: (NotificationItem) -> Unit,
    private val onDeleteClick: (Int) -> Unit          // ← new delete callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER  = 0
        private const val TYPE_MESSAGE = 1
    }

    override fun getItemViewType(position: Int) =
        if (items[position] is String) TYPE_HEADER else TYPE_MESSAGE

    // ── Header ViewHolder ────────────────────────────────────────────────
    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(R.id.tv_notif_header)
    }

    // ── Message ViewHolder ───────────────────────────────────────────────
    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage : TextView  = view.findViewById(R.id.tv_notif_message)
        val ivDelete  : ImageView = view.findViewById(R.id.iv_delete_notif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notif_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notif_message, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.tvHeader.text = items[position] as String

        } else if (holder is MessageViewHolder) {
            val item = items[position] as NotificationItem
            holder.tvMessage.text = item.message

            // ── Message click → open dialog ──────────────────────────────
            holder.itemView.setOnClickListener { onItemClick(item) }

            // ── Delete click → remove item ───────────────────────────────
            holder.ivDelete.setOnClickListener {
                val currentPos = holder.adapterPosition
                if (currentPos != RecyclerView.NO_ID.toInt()) {
                    onDeleteClick(currentPos)
                }
            }
        }
    }

    override fun getItemCount() = items.size

    // ── Remove item and clean up orphan headers ──────────────────────────
    fun deleteItem(position: Int) {
        items.removeAt(position)

        // Remove header if it has no messages left under it
        if (position < items.size && items[position] is String) {
            // Next item is already a header — previous header is now empty
            if (position > 0 && items[position - 1] is String) {
                items.removeAt(position - 1)
                notifyDataSetChanged()
                return
            }
        }
        // If deleted item was last and previous is a header, remove header too
        if (position == items.size && position > 0 && items[position - 1] is String) {
            items.removeAt(position - 1)
            notifyDataSetChanged()
            return
        }

        notifyDataSetChanged()
    }
}