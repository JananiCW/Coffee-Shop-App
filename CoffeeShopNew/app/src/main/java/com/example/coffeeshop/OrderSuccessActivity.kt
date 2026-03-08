package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val DELIVERY_FEE = 2.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        val orderID = intent.getStringExtra("order_id") ?: generateOrderID()
        val subtotal = intent.getDoubleExtra("subtotal_amount", 0.0)
        val deliveryFee = intent.getDoubleExtra("delivery_fee", DELIVERY_FEE)
        val totalAmount = intent.getDoubleExtra("total_amount", subtotal + deliveryFee)
        val estimatedTime = intent.getStringExtra("estimated_time") ?: generateEstimatedTime()

        val orderNumberText = findViewById<TextView>(R.id.orderNumberText)
        val totalAmountText = findViewById<TextView>(R.id.totalAmountText)
        val estimatedTimeText = findViewById<TextView>(R.id.estimatedTimeText)
        val paymentMethodText = findViewById<TextView>(R.id.paymentMethodText)
        val trackOrderBtn = findViewById<Button>(R.id.trackOrderBtn)
        val continueShoppingBtn = findViewById<Button>(R.id.continueShoppingBtn)

        orderNumberText.text = orderID
        totalAmountText.text = "$%.2f".format(totalAmount)
        estimatedTimeText.text = estimatedTime
        paymentMethodText.text = "Cash on Delivery"

        // Clear cart
        sharedPreferences.edit().remove(CartActivity.CART_KEY).apply()

        trackOrderBtn.setOnClickListener {
            val intent = Intent(this, TrackOrderActivity::class.java)
            intent.putExtra("order_number", orderID)
            intent.putExtra("total_amount", totalAmount)
            intent.putExtra("estimated_time", estimatedTime)
            startActivity(intent)
        }

        continueShoppingBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun generateOrderID(): String {
        return "CF" + (100000..999999).random()
    }

    private fun generateEstimatedTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()

        val start = now.clone() as Calendar
        start.add(Calendar.MINUTE, 10)

        val end = now.clone() as Calendar
        end.add(Calendar.MINUTE, 20)

        return "${sdf.format(start.time)} - ${sdf.format(end.time)}"
    }
}
