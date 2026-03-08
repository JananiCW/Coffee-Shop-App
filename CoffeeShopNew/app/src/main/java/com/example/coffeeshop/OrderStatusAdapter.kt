package com.example.coffeeshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class OrderStatusAdapter(private val list: List<OrderStatus>) :
    RecyclerView.Adapter<OrderStatusAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.statusIcon)
        val title: TextView = view.findViewById(R.id.statusTitle)
        val description: TextView = view.findViewById(R.id.statusDescription) // fixed id
        val time: TextView = view.findViewById(R.id.statusTime)
        val line: View = view.findViewById(R.id.statusLine) // fixed id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_status, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.title
        holder.description.text = item.description
        holder.time.text = item.time

        val ctx = holder.itemView.context

        when {
            item.isCompleted -> {
                holder.icon.setImageResource(R.drawable.check_icon)     // use your drawable
                holder.icon.setColorFilter(ContextCompat.getColor(ctx, R.color.orange))
                holder.title.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            }
            item.isActive -> {
                holder.icon.setImageResource(R.drawable.coffee_icon)    // use your drawable for "active"
                holder.icon.setColorFilter(ContextCompat.getColor(ctx, R.color.orange))
                holder.title.setTextColor(ContextCompat.getColor(ctx, R.color.orange))
            }
            else -> {
                holder.icon.setImageResource(R.drawable.clock_icon)     // fallback drawable
                holder.icon.setColorFilter(ContextCompat.getColor(ctx, R.color.lightBrown))
                holder.title.setTextColor(ContextCompat.getColor(ctx, R.color.lightBrown))
            }
        }

        holder.line.visibility = if (position == list.size - 1) View.GONE else View.VISIBLE
    }
}
