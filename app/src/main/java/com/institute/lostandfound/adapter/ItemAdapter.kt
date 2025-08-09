package com.institute.lostandfound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.institute.lostandfound.R
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.ItemType
import com.institute.lostandfound.databinding.ItemCardBinding
import java.text.SimpleDateFormat
import java.util.*

class ItemAdapter(
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.apply {
                textTitle.text = item.title
                textDescription.text = item.description
                textCategory.text = item.category
                textLocation.text = item.location
                textDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(item.dateReported)
                textReporter.text = "Reported by: ${item.reporterName}"

                // Set type indicator
                chipType.text = if (item.type == ItemType.LOST) "LOST" else "FOUND"
                chipType.setChipBackgroundColorResource(
                    if (item.type == ItemType.LOST) R.color.chip_lost else R.color.chip_found
                )

                // Show resolved status
                chipResolved.visibility = if (item.isResolved) android.view.View.VISIBLE else android.view.View.GONE

                // Load image if available
                item.imageUri?.let { uri ->
                    Glide.with(imageView.context)
                        .load(uri)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(imageView)
                } ?: run {
                    imageView.setImageResource(R.drawable.ic_placeholder)
                }

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
} 