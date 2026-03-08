package com.example.coffeeshop

data class CartItem(
    val name: String,
    val subtitle: String,
    var price: Double,
    val size: String = "M",
    var quantity: Int,
    val imageResId: Int
)
