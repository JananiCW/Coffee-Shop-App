package com.example.coffeeshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationsAdapter(
    private val notifications: List<NotificationItem>,
    private val onClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.notificationTitle)
        val messageText: TextView = itemView.findViewById(R.id.notificationMessage)
        val timeText: TextView = itemView.findViewById(R.id.notificationTime)
        val unreadIndicator: View = itemView.findViewById(R.id.unreadIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.titleText.text = notification.title
        holder.messageText.text = notification.message
        holder.timeText.text = notification.time
        holder.unreadIndicator.visibility = if (notification.isRead) View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener { onClick(notification) }
    }

    override fun getItemCount(): Int = notifications.size
}
