package com.example.coffeeshop

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotificationDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        val titleText = findViewById<TextView>(R.id.detailTitle)
        val timeText = findViewById<TextView>(R.id.detailTime)
        val messageContainer = findViewById<LinearLayout>(R.id.messageContainer)
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        // Get data from intent
        val title = intent.getStringExtra("title") ?: ""
        val message = intent.getStringExtra("message") ?: ""
        val time = intent.getStringExtra("time") ?: ""

        titleText.text = title
        timeText.text = time

        // Split message into lines by "." or "\n" for receipt-like display
        val lines = message.split("\n", ". ").filter { it.isNotBlank() }
        for (line in lines) {
            val textView = TextView(this).apply {
                text = line.trim()
                setTextColor(resources.getColor(R.color.creamBackground))
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            messageContainer.addView(textView)
        }

        backBtn.setOnClickListener { finish() }
    }
}

