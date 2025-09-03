package com.institute.lostandfound.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.institute.lostandfound.R
import com.institute.lostandfound.adapter.ItemAdapter
import com.institute.lostandfound.data.model.ItemType
import com.institute.lostandfound.databinding.FragmentLostItemsBinding
import com.institute.lostandfound.viewmodel.ItemViewModel

class LostItemsFragment : Fragment() {
    private var _binding: FragmentLostItemsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLostItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupRefreshLayout()
        observeData()
        // Load lost items when fragment is created
        viewModel.loadLostItems()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        viewModel.loadLostItems()
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter { item ->
            // Navigate to item detail
            viewModel.setSelectedItem(item)
            findNavController().navigate(R.id.action_lost_to_item_detail)
        }
        
        binding.recyclerViewLostItems.apply {
            adapter = this@LostItemsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    
    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadLostItems()
        }
    }

    private fun observeData() {
        viewModel.lostItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
            binding.textEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // Also observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
        
        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Handle error if needed
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 