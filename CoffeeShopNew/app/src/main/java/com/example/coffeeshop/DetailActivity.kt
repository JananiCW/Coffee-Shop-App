package com.example.coffeeshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class DetailActivity : AppCompatActivity() {

    private var selectedSize = "Medium"
    private var quantity = 1
    private var basePrice = 0.0
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    private lateinit var coffeeName: String
    private lateinit var coffeeSubtitle: String
    private var coffeeImage: Int = 0
    private var coffeeItem: PopularModel? = null

    companion object {
        const val FAVORITES_KEY = "favorites_list"
        const val CART_KEY = "cart_items"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        // Read intent data
        coffeeName = intent.getStringExtra("coffee_name") ?: "Coffee"
        coffeeSubtitle = intent.getStringExtra("coffee_subtitle") ?: "Espresso"
        val coffeePrice = intent.getDoubleExtra("coffee_price", 0.0)
        coffeeImage = intent.getIntExtra("coffee_image", R.drawable.coffee1)

        basePrice = coffeePrice
        coffeeItem = PopularModel(coffeeName, coffeeSubtitle, coffeePrice, coffeeImage)

        setupViews()
        setupDescription()
        setupSizeSelection()
        setupQuantityControls()
        setupButtons()
        updateFavoriteIcon()
    }

    private fun setupViews() {
        findViewById<TextView>(R.id.coffeeNameDetail).text = coffeeName
        findViewById<TextView>(R.id.coffeeSubtitleDetail).text = coffeeSubtitle
        findViewById<TextView>(R.id.coffeePriceDetail).text = "$$basePrice"
        findViewById<ImageView>(R.id.coffeeImageDetail).setImageResource(coffeeImage)
        findViewById<TextView>(R.id.quantityText).text = quantity.toString()
    }

    private fun setupDescription() {
        val description = loadDescriptionFromFile(coffeeName)
        findViewById<TextView>(R.id.coffeeDescriptionDetail).text = description
    }

    private fun setupSizeSelection() {
        val sizeSmall = findViewById<LinearLayout>(R.id.sizeSmall)
        val sizeMedium = findViewById<LinearLayout>(R.id.sizeMedium)
        val sizeLarge = findViewById<LinearLayout>(R.id.sizeLarge)

        updateSizeSelection(selectedSize)

        sizeSmall.setOnClickListener { changeSize("Small") }
        sizeMedium.setOnClickListener { changeSize("Medium") }
        sizeLarge.setOnClickListener { changeSize("Large") }
    }

    private fun changeSize(size: String) {
        selectedSize = size
        updateSizeSelection(size)
        updatePrice()
    }

    private fun updateSizeSelection(size: String) {
        val sizeSmall = findViewById<LinearLayout>(R.id.sizeSmall)
        val sizeMedium = findViewById<LinearLayout>(R.id.sizeMedium)
        val sizeLarge = findViewById<LinearLayout>(R.id.sizeLarge)

        // Reset
        sizeSmall.setBackgroundResource(R.drawable.category_bg_unselected)
        sizeMedium.setBackgroundResource(R.drawable.category_bg_unselected)
        sizeLarge.setBackgroundResource(R.drawable.category_bg_unselected)

        // Select
        when (size) {
            "Small" -> sizeSmall.setBackgroundResource(R.drawable.category_bg_selected)
            "Medium" -> sizeMedium.setBackgroundResource(R.drawable.category_bg_selected)
            "Large" -> sizeLarge.setBackgroundResource(R.drawable.category_bg_selected)
        }
    }

    private fun setupQuantityControls() {
        val minusBtn = findViewById<ImageView>(R.id.minusBtn)
        val plusBtn = findViewById<ImageView>(R.id.plusBtn)
        val quantityText = findViewById<TextView>(R.id.quantityText)

        minusBtn.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.text = quantity.toString()
                updatePrice()
            }
        }

        plusBtn.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
            updatePrice()
        }
    }

    private fun updatePrice() {
        var finalPrice = basePrice

        finalPrice *= when (selectedSize) {
            "Small" -> 0.8
            "Medium" -> 1.0
            "Large" -> 1.3
            else -> 1.0
        }

        finalPrice *= quantity

        // Update the main price (top)
        findViewById<TextView>(R.id.coffeePriceDetail).text =
            "$%.2f".format(finalPrice)

        // Update bottom price (Add to Cart button)
        findViewById<TextView>(R.id.totalPriceText).text =
            "$%.2f".format(finalPrice)
    }


    private fun setupButtons() {
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<Button>(R.id.addToCartBtn).setOnClickListener { addToCart() }
        findViewById<ImageView>(R.id.favoriteBtn).setOnClickListener { toggleFavorite() }
    }

    private fun addToCart() {
        // Calculate unit price for selected size (without multiplying quantity)
        val unitPrice = basePrice * when (selectedSize) {
            "Small" -> 0.8
            "Medium" -> 1.0
            "Large" -> 1.3
            else -> 1.0
        }

        val cartList = loadCartItems().toMutableList()

        // Check if same coffee + size exists (ignore case and trim)
        val existingItem = cartList.find {
            it.name.trim().equals(coffeeName.trim(), ignoreCase = true) &&
                    it.size.trim().equals(selectedSize.trim(), ignoreCase = true)
        }

        if (existingItem != null) {
            // Increase quantity only
            existingItem.quantity += quantity
            // Always update unit price to latest price (important to avoid duplicates)
            existingItem.price = unitPrice
        } else {
            // Add new item
            val cartItem = CartItem(
                name = coffeeName,
                subtitle = coffeeSubtitle,
                price = unitPrice, // unit price
                size = selectedSize,
                quantity = quantity,
                imageResId = coffeeImage
            )
            cartList.add(cartItem)
        }

        saveCartItems(cartList)

        Toast.makeText(this, "$coffeeName added to cart!", Toast.LENGTH_SHORT).show()
        finish()
    }




    private fun loadCartItems(): List<CartItem> {
        val json = sharedPreferences.getString(CART_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    private fun saveCartItems(list: List<CartItem>) {
        sharedPreferences.edit().putString(CART_KEY, gson.toJson(list)).apply()
    }

    private fun toggleFavorite() {
        val favorites = loadFavorites().toMutableList()

        coffeeItem?.let { item ->
            if (favorites.any { it.title == item.title }) {
                favorites.removeAll { it.title == item.title }
                Toast.makeText(this, "$coffeeName removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                favorites.add(item)
                Toast.makeText(this, "$coffeeName added to favorites!", Toast.LENGTH_SHORT).show()
            }
        }

        saveFavorites(favorites)
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        val favorites = loadFavorites()
        val isFavorite = favorites.any { it.title == coffeeItem?.title }

        findViewById<ImageView>(R.id.favoriteBtn).setImageResource(
            if (isFavorite) R.drawable.heart_filled else R.drawable.heart_icon
        )
    }

    private fun loadFavorites(): List<PopularModel> {
        val json = sharedPreferences.getString(FAVORITES_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<PopularModel>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    private fun saveFavorites(list: List<PopularModel>) {
        sharedPreferences.edit().putString(FAVORITES_KEY, gson.toJson(list)).apply()
    }

    private fun loadDescriptionFromFile(coffeeName: String): String {
        return try {
            val fileName = when (coffeeName.lowercase()) {
                "espresso" -> "espresso.txt"
                "double espresso" -> "double_espresso.txt"
                "cappuccino" -> "cappuccino.txt"
                "iced cappuccino" -> "iced_cappuccino.txt"
                "latino" -> "latte.txt"
                "vanilla latte" -> "vanilla_latte.txt"
                "caramel latte" -> "caramel_latte.txt"
                "americano" -> "americano.txt"
                "iced americano" -> "iced_americano.txt"
                "hot chocolate" -> "hot_chocolate.txt"
                "white hot chocolate" -> "white_hot_chocolate.txt"
                else -> "espresso.txt"
            }

            val inputStream = assets.open("descriptions/$fileName")
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            "A delicious coffee prepared with love. Perfect for coffee lovers!"
        }
    }
}
