package com.example.coffeeshop

data class OrderStatus(
    val title: String,
    val description: String,
    val time: String,
    val isCompleted: Boolean,
    val isActive: Boolean
)