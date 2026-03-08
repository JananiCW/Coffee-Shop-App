package com.example.coffeeshop

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

class CategoryAdapter(
    private val categoryList: ArrayList<CategoryModel>,
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleCat: TextView = itemView.findViewById(R.id.titleCat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categoryList[position]
        holder.titleCat.text = item.title

        // Update appearance based on selection state
        if (item.isSelected) {
            holder.titleCat.setBackgroundResource(R.drawable.category_bg_selected)
            holder.titleCat.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.titleCat.setBackgroundResource(R.drawable.category_bg_unselected)
            holder.titleCat.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

        // Handle click events
        holder.itemView.setOnClickListener {
            // Deselect all items
            for (i in categoryList.indices) {
                categoryList[i].isSelected = false
            }
            // Select clicked item
            item.isSelected = true
            // Refresh the adapter
            notifyDataSetChanged()
            // Notify the activity about category selection
            onCategorySelected(item.title)
        }
    }

    override fun getItemCount(): Int = categoryList.size
    
    fun deselectAll() {
        for (i in categoryList.indices) {
            categoryList[i].isSelected = false
        }
        notifyDataSetChanged()
    }
}
