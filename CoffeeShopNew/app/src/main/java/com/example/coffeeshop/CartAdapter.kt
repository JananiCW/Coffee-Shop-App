package com.example.coffeeshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onRemoveClick: (Int) -> Unit,
    private val onQuantityChange: (Int, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coffeeImage: ImageView = itemView.findViewById(R.id.coffeeImage)
        val coffeeNameText: TextView = itemView.findViewById(R.id.coffeeNameText)
        val coffeeSizeText: TextView = itemView.findViewById(R.id.coffeeSizeText)
        val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        val priceText: TextView = itemView.findViewById(R.id.priceText)
        val minusBtn: ImageView = itemView.findViewById(R.id.minusBtn)
        val plusBtn: ImageView = itemView.findViewById(R.id.plusBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.coffeeImage.setImageResource(item.imageResId)
        holder.coffeeNameText.text = item.name
        holder.coffeeSizeText.text = "Size: ${item.size}"
        holder.quantityText.text = item.quantity.toString()
        holder.priceText.text = "$%.2f".format(item.price * item.quantity)

        holder.minusBtn.setOnClickListener {
            if (item.quantity > 1) onQuantityChange(position, item.quantity - 1)
            else onRemoveClick(position)
        }

        holder.plusBtn.setOnClickListener {
            if (item.quantity < 10) onQuantityChange(position, item.quantity + 1)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateList(newList: MutableList<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newList)
        notifyDataSetChanged()
    }



}
