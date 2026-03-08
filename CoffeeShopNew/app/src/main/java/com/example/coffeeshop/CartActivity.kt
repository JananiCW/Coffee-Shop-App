package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var cartSummary: LinearLayout
    private lateinit var totalPriceText: TextView
    private lateinit var subtotalText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cartAdapter: CartAdapter

    private var cartItems = mutableListOf<CartItem>()
    private val gson = Gson()
    private val DELIVERY_FEE = 2.0  // still used for checkout

    companion object {
        const val CART_KEY = "cart_items"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        initViews()
        loadCartItems()
        setupRecyclerView()
        updateUI()
    }

    private fun initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        cartSummary = findViewById(R.id.cartSummary)
        totalPriceText = findViewById(R.id.totalPriceText)
        subtotalText = findViewById(R.id.subtotalText)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<Button>(R.id.checkoutBtn).setOnClickListener {
            if (cartItems.isNotEmpty()) {
                val subtotal = cartItems.sumOf { it.price * it.quantity }

                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("subtotal_amount", subtotal)
                intent.putExtra("delivery_fee", DELIVERY_FEE)
                intent.putExtra("total_amount", subtotal + DELIVERY_FEE)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCartItems() {
        val json = sharedPreferences.getString(CART_KEY, null)
        cartItems = if (json != null) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            gson.fromJson(json, type)
        } else mutableListOf()
    }

    private fun saveCartItems() {
        sharedPreferences.edit().putString(CART_KEY, gson.toJson(cartItems)).apply()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems,
            onRemoveClick = { position -> removeItem(position) },
            onQuantityChange = { position, newQty -> updateQuantity(position, newQty) }
        )
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter
    }

    private fun updateQuantity(position: Int, newQuantity: Int) {
        if (newQuantity > 0) {
            cartItems[position].quantity = newQuantity
            cartAdapter.notifyItemChanged(position)
            saveCartItems()
            updateUI()
        }
    }

    private fun removeItem(position: Int) {
        cartItems.removeAt(position)
        cartAdapter.notifyItemRemoved(position)
        saveCartItems()
        updateUI()
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        if (cartItems.isEmpty()) {
            cartRecyclerView.visibility = LinearLayout.GONE
            cartSummary.visibility = LinearLayout.GONE
            emptyCartLayout.visibility = LinearLayout.VISIBLE
        } else {
            cartRecyclerView.visibility = LinearLayout.VISIBLE
            cartSummary.visibility = LinearLayout.VISIBLE
            emptyCartLayout.visibility = LinearLayout.GONE

            val subtotal = cartItems.sumOf { it.price * it.quantity }

            subtotalText.text = "$%.2f".format(subtotal)
            // REMOVE delivery fee from total shown in cart
            totalPriceText.text = "$%.2f".format(subtotal)
        }
    }

    private fun clearCart() {
        cartItems.clear()
        cartAdapter.updateList(cartItems)
        saveCartItems()
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
        cartAdapter.updateList(cartItems)
        updateUI()
    }
}
