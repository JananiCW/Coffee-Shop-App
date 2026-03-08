package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val DELIVERY_FEE = 2.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        val subtotal = intent.getDoubleExtra("subtotal_amount", 0.0)
        val totalAmount = subtotal + DELIVERY_FEE

        // Views
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val addressInput = findViewById<EditText>(R.id.addressInput)
        val subtotalText = findViewById<TextView>(R.id.subtotalText)
        val deliveryText = findViewById<TextView>(R.id.deliveryText)
        val totalText = findViewById<TextView>(R.id.totalText)
        val checkoutBtn = findViewById<Button>(R.id.placeOrderBtn)

        // Payment Views
        val paymentGroup = findViewById<RadioGroup>(R.id.paymentMethodGroup)
        val cardDetailsLayout = findViewById<LinearLayout>(R.id.cardDetailsLayout)

        // Display amounts
        subtotalText.text = "$%.2f".format(subtotal)
        deliveryText.text = "$%.2f".format(DELIVERY_FEE)
        totalText.text = "$%.2f".format(totalAmount)

        // Payment method selection
        paymentGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioCash -> cardDetailsLayout.visibility = LinearLayout.GONE
                R.id.radioCard -> cardDetailsLayout.visibility = LinearLayout.VISIBLE
            }
        }

        checkoutBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val address = addressInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter your delivery address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Optional: validate card details if Card payment is selected
            if (paymentGroup.checkedRadioButtonId == R.id.radioCard) {
                val cardNumber = findViewById<EditText>(R.id.cardNumberInput).text.toString().trim()
                val cardName = findViewById<EditText>(R.id.cardNameInput).text.toString().trim()
                val expiry = findViewById<EditText>(R.id.expiryInput).text.toString().trim()
                val cvv = findViewById<EditText>(R.id.cvvInput).text.toString().trim()

                if (cardNumber.length != 16 || cardName.isEmpty() || expiry.length != 5 || cvv.length != 3) {
                    Toast.makeText(this, "Please enter valid card details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val orderID = generateOrderID()
            val estimatedTime = generateEstimatedTime()

            saveOrderToFirebase(orderID, totalAmount, estimatedTime)

            // Clear cart
            sharedPreferences.edit().remove(CartActivity.CART_KEY).apply()

            val intent = Intent(this, OrderSuccessActivity::class.java)
            intent.putExtra("order_id", orderID)
            intent.putExtra("subtotal_amount", subtotal)
            intent.putExtra("delivery_fee", DELIVERY_FEE)
            intent.putExtra("total_amount", totalAmount)
            intent.putExtra("estimated_time", estimatedTime)
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    private fun saveOrderToFirebase(orderID: String, total: Double, estimated: String) {
        val db = FirebaseDatabase.getInstance().reference

        val orderData = OrderData(
            orderID = orderID,
            totalAmount = total,
            estimatedTime = estimated,
            status = "preparing",
            timestamp = System.currentTimeMillis()
        )

        // Save order to Realtime Database
        db.child("Orders").child(orderID).setValue(orderData)

        // --- Add Notification for this user ---
        val userId = FirebaseHelper.auth.currentUser?.uid
        if (userId != null) {
            val notificationData = hashMapOf(
                "id" to orderID,
                "title" to "Order Placed",
                "message" to "Your order $orderID of \$${"%.2f".format(total)} is confirmed. Estimated pickup: $estimated",
                "time" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                "isRead" to false
            )

            FirebaseHelper.firestore
                .collection("Users")
                .document(userId)
                .collection("Notifications")
                .document(orderID)
                .set(notificationData)
        }
    }
}
