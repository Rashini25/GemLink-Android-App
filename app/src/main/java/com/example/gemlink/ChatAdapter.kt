package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByMe) 0 else 1
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage : TextView = view.findViewById(R.id.tv_message_text)
        val tvTime    : TextView = view.findViewById(R.id.tv_message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0)
            R.layout.item_message_sent
        else
            R.layout.item_message_received

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.tvMessage.text = message.text
        holder.tvTime.text    = message.time
    }

    override fun getItemCount() = messages.size
}