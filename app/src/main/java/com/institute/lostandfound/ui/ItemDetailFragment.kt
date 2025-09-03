package com.institute.lostandfound.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.institute.lostandfound.R
import com.institute.lostandfound.data.model.ItemType
import com.institute.lostandfound.databinding.FragmentItemDetailBinding
import com.institute.lostandfound.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailFragment : Fragment() {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSelectedItem()
    }

    private fun observeSelectedItem() {
        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            item?.let { displayItemDetails(it) }
        }
    }

    private fun displayItemDetails(item: com.institute.lostandfound.data.model.Item) {
        binding.apply {
            textTitle.text = item.title
            textDescription.text = item.description
            textCategory.text = item.category
            textLocation.text = "Location: ${item.location.address}"
            textDate.text = "Reported: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(item.dateReported.toDate())}"
            textReporter.text = "Reported by: ${item.reporterName}"
            textContact.text = item.contactInfo.email

            // Set type indicator
            chipType.text = if (item.type == ItemType.LOST) "LOST" else "FOUND"
            chipType.setChipBackgroundColorResource(
                if (item.type == ItemType.LOST) R.color.chip_lost else R.color.chip_found
            )

            // Show resolved status
            chipResolved.visibility = if (item.isResolved) View.VISIBLE else View.GONE

            // Load image if available
            if (item.imageUri.isNotEmpty()) {
                imageView.visibility = View.VISIBLE
                Glide.with(this@ItemDetailFragment)
                    .load(item.imageUri)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }

            // Setup contact button
            buttonContact.setOnClickListener {
                contactReporter(item.contactInfo.email, item.title)
            }

            // Setup resolve button (only show if not resolved)
            if (!item.isResolved) {
                buttonMarkResolved.visibility = View.VISIBLE
                buttonMarkResolved.setOnClickListener {
                    viewModel.markAsResolved(item.id)
                    Toast.makeText(context, "Item marked as resolved", Toast.LENGTH_SHORT).show()
                }
            } else {
                buttonMarkResolved.visibility = View.GONE
            }
        }
    }

    private fun contactReporter(email: String, itemTitle: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, "Regarding: $itemTitle")
            putExtra(Intent.EXTRA_TEXT, "Hi, I saw your post about the $itemTitle...")
        }
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 