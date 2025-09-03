package com.institute.lostandfound.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.institute.lostandfound.R
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.ItemType
import java.text.SimpleDateFormat
import java.util.Locale

class ItemAdapter(
    private var items: List<Item> = emptyList(),
    private val onItemClick: ((Item) -> Unit)? = null
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    companion object {
        private const val TAG = "ItemAdapter"
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.image_view)
        val itemTitle: TextView = itemView.findViewById(R.id.text_title)
        val itemDescription: TextView = itemView.findViewById(R.id.text_description)
        val itemCategory: TextView = itemView.findViewById(R.id.text_category)
        val itemDate: TextView = itemView.findViewById(R.id.text_date)
        val itemTypeChip: TextView = itemView.findViewById(R.id.chip_type)
        val itemLocation: TextView = itemView.findViewById(R.id.text_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.d(TAG, "onCreateViewHolder: Creating new ViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        Log.d(TAG, "onBindViewHolder: Binding item at position $position - Title: ${item.title}, Type: ${item.type}")
        
        // Set item data
        holder.itemTitle.text = item.title
        holder.itemDescription.text = item.description
        holder.itemCategory.text = item.category
        holder.itemLocation.text = item.location.placeName.ifEmpty { 
            item.location.address.ifEmpty { "Location not specified" }
        }
        
        // Format date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.itemDate.text = dateFormat.format(item.createdAt.toDate())
        
        // Set item type chip
        holder.itemTypeChip.text = item.type.name
        holder.itemTypeChip.setBackgroundResource(
            if (item.type == ItemType.LOST) R.drawable.chip_lost_background
            else R.drawable.chip_found_background
        )
        
        // Load image if available
        if (item.images.isNotEmpty()) {
            Log.d(TAG, "onBindViewHolder: Loading image for item: ${item.title}")
            Glide.with(holder.itemImage.context)
                .load(item.images.first())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.itemImage)
        } else {
            Log.d(TAG, "onBindViewHolder: No image for item: ${item.title}, using placeholder")
            holder.itemImage.setImageResource(R.drawable.ic_placeholder)
        }
        
        // Set click listener
        holder.itemView.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: Item clicked: ${item.title}")
            onItemClick?.invoke(item)
        }
        
        Log.d(TAG, "onBindViewHolder: Successfully bound item: ${item.title}")
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: Returning ${items.size} items")
        return items.size
    }

    fun updateItems(newItems: List<Item>) {
        Log.d(TAG, "updateItems: Updating items - Old count: ${items.size}, New count: ${newItems.size}")
        newItems.forEach { item ->
            Log.d(TAG, "updateItems: New item - Title: ${item.title}, Type: ${item.type}, Status: ${item.status}")
        }
        
        items = newItems
        notifyDataSetChanged()
        Log.d(TAG, "updateItems: Items updated and adapter notified")
    }

    fun addItem(item: Item) {
        Log.d(TAG, "addItem: Adding new item - Title: ${item.title}")
        val newList = items.toMutableList()
        newList.add(0, item)
        items = newList
        notifyItemInserted(0)
        Log.d(TAG, "addItem: Item added at position 0")
    }

    fun removeItem(itemId: String) {
        Log.d(TAG, "removeItem: Removing item with ID: $itemId")
        val index = items.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val newList = items.toMutableList()
            newList.removeAt(index)
            items = newList
            notifyItemRemoved(index)
            Log.d(TAG, "removeItem: Item removed from position $index")
        } else {
            Log.w(TAG, "removeItem: Item with ID $itemId not found")
        }
    }
} 