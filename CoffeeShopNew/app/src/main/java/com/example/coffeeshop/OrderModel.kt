package com.example.coffeeshop

data class OrderModel(
    val orderId: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val name: String,
    val phone: String,
    val address: String,
    val paymentMethod: String,
    val orderTime: Long,
    val estimatedDelivery: Long,
    val status: Int = 0 // 0=Order Confirmed
)
