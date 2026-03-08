package com.example.coffeeshop

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExploreActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var exploreRecyclerView: RecyclerView
    private lateinit var exploreAdapter: PopularAdapter
    private var allCoffeeItems = mutableListOf<PopularModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        initViews()
        loadAllCoffeeItems()
        setupRecyclerView()
        setupSearch()
    }

    private fun initViews() {
        searchInput = findViewById(R.id.searchInput)
        exploreRecyclerView = findViewById(R.id.exploreRecyclerView)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.cartBtn).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun loadAllCoffeeItems() {
        allCoffeeItems.clear()
        allCoffeeItems.add(PopularModel("Espresso", "Rich & Bold Italian Classic", 3.5, R.drawable.coffee1))
        allCoffeeItems.add(PopularModel("Double Espresso", "Extra Strong Double Shot", 4.5, R.drawable.coffee2))
        allCoffeeItems.add(PopularModel("Cappuccino", "Creamy Foam & Smooth Espresso", 4.5, R.drawable.coffee3))
        allCoffeeItems.add(PopularModel("Iced Cappuccino", "Refreshing Cold Cappuccino", 5.0, R.drawable.coffee4))
        allCoffeeItems.add(PopularModel("Latte", "Silky Smooth Milk & Espresso", 4.8, R.drawable.coffee5))
        allCoffeeItems.add(PopularModel("Vanilla Latte", "Sweet Vanilla Bean Delight", 5.5, R.drawable.coffee6))
        allCoffeeItems.add(PopularModel("Caramel Latte", "Buttery Caramel Perfection", 5.5, R.drawable.coffee7))
        allCoffeeItems.add(PopularModel("Americano", "Classic Bold & Smooth", 3.8, R.drawable.coffee8))
        allCoffeeItems.add(PopularModel("Iced Americano", "Chilled Bold Coffee", 4.0, R.drawable.coffee9))
        allCoffeeItems.add(PopularModel("Hot Chocolate", "Rich Cocoa & Whipped Cream", 4.2, R.drawable.coffee10))
        allCoffeeItems.add(PopularModel("White Hot Chocolate", "Creamy White Chocolate Dream", 4.5, R.drawable.coffee11))
        allCoffeeItems.add(PopularModel("Mocha", "Chocolate & Espresso Blend", 5.2, R.drawable.coffee12))
        allCoffeeItems.add(PopularModel("Macchiato", "Espresso Marked with Foam", 4.8, R.drawable.coffee7))
        allCoffeeItems.add(PopularModel("Frappuccino", "Icy Blended Coffee Treat", 6.0, R.drawable.coffee3))
        allCoffeeItems.add(PopularModel("Cold Brew", "Smooth Cold Brewed Coffee", 4.5, R.drawable.coffee6))
    }

    private fun setupRecyclerView() {
        exploreAdapter = PopularAdapter(allCoffeeItems)
        exploreRecyclerView.layoutManager = GridLayoutManager(this, 2)
        exploreRecyclerView.adapter = exploreAdapter

        val sideSpacing = 8
        val middleGap = 12

        exploreRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: android.view.View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % 2
                outRect.left = if (column == 0) sideSpacing else middleGap / 2
                outRect.right = if (column == 1) sideSpacing else middleGap / 2
                outRect.bottom = 16
                if (position < 2) outRect.top = 8
            }
        })
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                exploreAdapter.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
