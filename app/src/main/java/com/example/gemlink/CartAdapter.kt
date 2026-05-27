package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private var cartItems: MutableList<CartItem>,
    private val onDeleteClick: (CartItem, Int) -> Unit,
    private val onMoreDetailsClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGemImage    : ImageView = view.findViewById(R.id.iv_cart_gem_image)
        val tvName        : TextView  = view.findViewById(R.id.tv_cart_gem_name)
        val tvPrice       : TextView  = view.findViewById(R.id.tv_cart_gem_price)
        val tvDate        : TextView  = view.findViewById(R.id.tv_cart_date)
        val ivDelete      : ImageView = view.findViewById(R.id.iv_cart_delete)
        val btnMoreDetails: TextView  = view.findViewById(R.id.btn_more_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.ivGemImage.setImageResource(item.imageRes)
        holder.tvName.text         = item.name
        holder.tvPrice.text        = item.price
        holder.tvDate.text         = "Added on ${item.dateAdded}"
        holder.ivDelete.setOnClickListener { onDeleteClick(item, position) }
        holder.btnMoreDetails.setOnClickListener { onMoreDetailsClick(item) }
    }

    override fun getItemCount() = cartItems.size

    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, cartItems.size)
    }

    fun filter(query: String, originalList: List<CartItem>) {
        cartItems = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.lowercase().contains(query.lowercase())
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}