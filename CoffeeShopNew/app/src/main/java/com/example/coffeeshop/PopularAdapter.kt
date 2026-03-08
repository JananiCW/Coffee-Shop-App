package com.example.coffeeshop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PopularAdapter(
    private var popularList: MutableList<PopularModel>
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val fullList = popularList.toMutableList() // Keep a master copy

    inner class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.textView8)
        val subtitleTxt: TextView = itemView.findViewById(R.id.textView12)
        val priceTxt: TextView = itemView.findViewById(R.id.textView10)
        val imageView: ImageView = itemView.findViewById(R.id.imageView3)
        val favoriteBtn: ImageView = itemView.findViewById(R.id.favoriteBtn)
        val addBtn: ImageView = itemView.findViewById(R.id.imageView6)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        sharedPreferences = parent.context.getSharedPreferences("CoffeeShopPrefs", Context.MODE_PRIVATE)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = popularList[position]

        holder.itemView.setOnClickListener {
            openDetail(holder, item)
        }

        holder.imageView.setOnClickListener {
            openDetail(holder, item)
        }

        holder.titleTxt.text = item.title
        holder.subtitleTxt.text = item.subtitle
        holder.priceTxt.text = "LKR %.2f".format(item.price)
        holder.imageView.setImageResource(item.imageResId)
        updateFavoriteIcon(holder, item)

        // Favorite toggle
        holder.favoriteBtn.setOnClickListener {
            toggleFavorite(item)
            updateFavoriteIcon(holder, item)
            Toast.makeText(
                holder.itemView.context,
                if (isFavorite(item)) "Added to Favorites" else "Removed from Favorites",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Add to cart
        holder.addBtn.setOnClickListener {
            addToCart(item)
            Toast.makeText(holder.itemView.context, "${item.title} added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDetail(holder: PopularViewHolder, item: PopularModel) {
        val context = holder.itemView.context
        val intent = Intent(context, DetailActivity::class.java)

        intent.putExtra("coffee_name", item.title)
        intent.putExtra("coffee_subtitle", item.subtitle)
        intent.putExtra("coffee_price", item.price)
        intent.putExtra("coffee_image", item.imageResId)

        context.startActivity(intent)
    }




    override fun getItemCount(): Int = popularList.size

    // ===== Favorites =====
    private fun loadFavorites(): MutableList<PopularModel> {
        val json = sharedPreferences.getString(DetailActivity.FAVORITES_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<PopularModel>>() {}.type
            gson.fromJson(json, type)
        } else mutableListOf()
    }

    private fun saveFavorites(list: List<PopularModel>) {
        sharedPreferences.edit().putString(DetailActivity.FAVORITES_KEY, gson.toJson(list)).apply()
    }

    private fun toggleFavorite(item: PopularModel) {
        val favorites = loadFavorites()
        if (favorites.any { it.title == item.title }) favorites.removeAll { it.title == item.title }
        else favorites.add(item)
        saveFavorites(favorites)
    }

    private fun isFavorite(item: PopularModel): Boolean = loadFavorites().any { it.title == item.title }

    private fun updateFavoriteIcon(holder: PopularViewHolder, item: PopularModel) {
        holder.favoriteBtn.setImageResource(
            if (isFavorite(item)) R.drawable.heart_filled else R.drawable.heart_icon
        )
    }

    // ===== Cart =====
    companion object { const val CART_KEY = "cart_items" }

    private fun loadCart(): MutableList<CartItem> {
        val json = sharedPreferences.getString(CART_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            gson.fromJson(json, type)
        } else mutableListOf()
    }

    private fun saveCart(list: List<CartItem>) {
        sharedPreferences.edit().putString(CART_KEY, gson.toJson(list)).apply()
    }

    private fun addToCart(item: PopularModel) {
        val cart = loadCart()
        val existing = cart.find { it.name == item.title }
        if (existing != null) existing.quantity += 1
        else cart.add(CartItem(item.title, item.subtitle, item.price, "M", 1, item.imageResId))
        saveCart(cart)
    }

    // ===== Search / Filter =====
    fun filter(query: String) {
        popularList.clear()
        if (query.isEmpty()) {
            popularList.addAll(fullList)
        } else {
            popularList.addAll(fullList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.subtitle.contains(query, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }
}
