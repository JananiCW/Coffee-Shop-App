package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyFavoritesLayout: LinearLayout
    private lateinit var favoritesAdapter: PopularAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var favoriteItems = mutableListOf<PopularModel>()

    companion object {
        const val FAVORITES_KEY = "favorites_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)
        emptyFavoritesLayout = findViewById(R.id.emptyFavoritesLayout)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.cartBtn).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        loadFavorites()
        setupRecyclerView()
        updateUI()
    }

    private fun loadFavorites() {
        val json = sharedPreferences.getString(FAVORITES_KEY, null)
        favoriteItems.clear()
        if (json != null) {
            val type = object : TypeToken<MutableList<PopularModel>>() {}.type
            val list: MutableList<PopularModel> = gson.fromJson(json, type)
            favoriteItems.addAll(list)
        }
    }

    private fun setupRecyclerView() {
        // Only pass favoriteItems to adapter
        favoritesAdapter = PopularAdapter(favoriteItems)
        favoritesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        favoritesRecyclerView.adapter = favoritesAdapter
    }

    private fun toggleFavorite(item: PopularModel) {
        if (favoriteItems.any { it.title == item.title }) {
            // Remove from favorites
            favoriteItems.removeAll { it.title == item.title }
        } else {
            // Add to favorites
            favoriteItems.add(item)
        }
        saveFavorites()
        updateUI()
    }

    private fun saveFavorites() {
        sharedPreferences.edit().putString(FAVORITES_KEY, gson.toJson(favoriteItems)).apply()
    }

    private fun updateUI() {
        if (favoriteItems.isEmpty()) {
            favoritesRecyclerView.visibility = View.GONE
            emptyFavoritesLayout.visibility = View.VISIBLE
        } else {
            favoritesRecyclerView.visibility = View.VISIBLE
            emptyFavoritesLayout.visibility = View.GONE
        }

        // ✅ Instead of updateList, just refresh the adapter
        favoritesAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
        updateUI()
    }
}
