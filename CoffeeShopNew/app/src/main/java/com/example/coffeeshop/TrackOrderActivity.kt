package com.example.coffeeshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TrackOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)

        val orderNumber = intent.getStringExtra("order_number") ?: "CF123456"
        val totalAmount = intent.getDoubleExtra("total_amount", 0.0)
        val estimatedTime = intent.getStringExtra("estimated_time") ?: "15:30 - 16:00"

        setupViews(orderNumber, totalAmount, estimatedTime)
        setupBackButton()
        setupOrderStatus()
    }

    private fun setupViews(orderNumber: String, totalAmount: Double, estimatedTime: String) {
        findViewById<TextView>(R.id.orderNumberText).text = orderNumber
        findViewById<TextView>(R.id.totalAmountText).text = "$%.2f".format(totalAmount)
        findViewById<TextView>(R.id.estimatedTimeText).text = estimatedTime
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageView>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupOrderStatus() {
        val recyclerView = findViewById<RecyclerView>(R.id.orderStatusRecycler)
        val statusList = getOrderStatusList()
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = OrderStatusAdapter(statusList)
    }

    private fun getOrderStatusList(): List<OrderStatus> {
        val currentTime = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        // Create realistic timeline
        val orderTime = Calendar.getInstance()
        orderTime.add(Calendar.MINUTE, -5) // Order placed 5 minutes ago
        
        val confirmTime = Calendar.getInstance()
        confirmTime.add(Calendar.MINUTE, -3) // Confirmed 3 minutes ago
        
        val preparingTime = Calendar.getInstance()
        preparingTime.add(Calendar.MINUTE, -1) // Started preparing 1 minute ago
        
        val readyTime = Calendar.getInstance()
        readyTime.add(Calendar.MINUTE, 10) // Will be ready in 10 minutes
        
        return listOf(
            OrderStatus(
                title = "Order Placed",
                description = "Your order has been received and confirmed",
                time = timeFormat.format(orderTime.time),
                isCompleted = true,
                isActive = false
            ),
            OrderStatus(
                title = "Order Confirmed",
                description = "We've confirmed your order and payment",
                time = timeFormat.format(confirmTime.time),
                isCompleted = true,
                isActive = false
            ),
            OrderStatus(
                title = "Preparing Your Coffee",
                description = "Our baristas are crafting your perfect coffee",
                time = timeFormat.format(preparingTime.time),
                isCompleted = true,
                isActive = true
            ),
            OrderStatus(
                title = "Ready for Pickup",
                description = "Your order will be ready at the counter",
                time = timeFormat.format(readyTime.time),
                isCompleted = false,
                isActive = false
            )
        )
    }
}