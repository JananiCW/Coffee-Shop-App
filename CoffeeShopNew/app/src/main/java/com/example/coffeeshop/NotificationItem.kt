package com.example.coffeeshop

data class NotificationItem(
    val id: String = "",        // Firestore doc ID
    val title: String = "",
    val message: String = "",
    val time: String = "",
    val isRead: Boolean = false
)
