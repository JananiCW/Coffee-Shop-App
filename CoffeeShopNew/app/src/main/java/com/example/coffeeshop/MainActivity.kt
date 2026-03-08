package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var popularAdapter: PopularAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        displayUsername()
        setupRecyclerViews()
        setupBottomNavigation()
        setupSearchAndSettings()
    }

    private fun displayUsername() {
        val usernameText = findViewById<TextView>(R.id.usernameText)
        val currentUser = FirebaseHelper.auth.currentUser

        if (currentUser != null) {
            FirebaseHelper.firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val username = doc.getString("name") ?: "User"
                    usernameText.text = username

                    // Update SharedPreferences as backup
                    sharedPreferences.edit().putString("username", username).apply()
                }
                .addOnFailureListener {
                    usernameText.text = "User"
                }
        } else {
            usernameText.text = "User"
        }
    }


    private fun setupRecyclerViews() {
        val popularRecycler = findViewById<RecyclerView>(R.id.popularView)
        val popularItems = getAllCoffeeItems()

        popularRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize adapter with the list only
        popularAdapter = PopularAdapter(popularItems)
        popularRecycler.adapter = popularAdapter

        val recycler = findViewById<RecyclerView>(R.id.recyclerview)
        val categories = arrayListOf(
            CategoryModel("All", true),
            CategoryModel("Espresso"),
            CategoryModel("Cappuccino"),
            CategoryModel("Latte"),
            CategoryModel("Americano"),
            CategoryModel("Hot Chocolate")
        )

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(categories) { selectedCategory ->
            // Filter by category
            if (selectedCategory == "All") popularAdapter.filter("")
            else popularAdapter.filter(selectedCategory)
        }
        recycler.adapter = categoryAdapter
    }

    private fun getAllCoffeeItems(): ArrayList<PopularModel> {
        return arrayListOf(
            PopularModel("Espresso", "Rich & Bold Italian Classic", 3.5, R.drawable.coffee1),
            PopularModel("Double Espresso", "Extra Strong Double Shot", 4.5, R.drawable.coffee2),
            PopularModel("Cappuccino", "Creamy Foam & Smooth Espresso", 4.5, R.drawable.coffee3),
            PopularModel("Iced Cappuccino", "Refreshing Cold Cappuccino", 5.0, R.drawable.coffee4),
            PopularModel("Latte", "Silky Smooth Milk & Espresso", 4.8, R.drawable.coffee5),
            PopularModel("Vanilla Latte", "Sweet Vanilla Bean Delight", 5.5, R.drawable.coffee6),
            PopularModel("Caramel Latte", "Buttery Caramel Perfection", 5.5, R.drawable.coffee7),
            PopularModel("Americano", "Classic Bold & Smooth", 3.8, R.drawable.coffee8),
            PopularModel("Iced Americano", "Chilled Bold Coffee", 4.0, R.drawable.coffee9),
            PopularModel("Hot Chocolate", "Rich Cocoa & Whipped Cream", 4.2, R.drawable.coffee10),
            PopularModel("White Hot Chocolate", "Creamy White Chocolate Dream", 4.5, R.drawable.coffee11)
        )
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navExplore = findViewById<LinearLayout>(R.id.navExplore)
        val navFavorites = findViewById<LinearLayout>(R.id.navFavorites)
        val navCart = findViewById<LinearLayout>(R.id.navCart)

        navHome.setOnClickListener { /* Already in Home */ }
        navExplore.setOnClickListener { startActivity(Intent(this, ExploreActivity::class.java)) }
        navFavorites.setOnClickListener { startActivity(Intent(this, FavoritesActivity::class.java)) }
        navCart.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
    }

    private fun setupSearchAndSettings() {
        val searchBar = findViewById<EditText>(R.id.editTextTextPersonName)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                popularAdapter.filter(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val bellButton = findViewById<ImageView>(R.id.imageView2)
        bellButton.setOnClickListener { startActivity(Intent(this, NotificationsActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        popularAdapter.notifyDataSetChanged()
    }
}
