package com.example.coffeeshop

data class OrderData(
    val orderID: String = "",
    val totalAmount: Double = 0.0,
    val estimatedTime: String = "",
    val status: String = "",
    val timestamp: Long = 0
)
