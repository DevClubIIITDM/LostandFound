package com.institute.lostandfound.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.institute.lostandfound.R
import com.institute.lostandfound.adapter.ItemAdapter
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.databinding.FragmentHomeBinding
import com.institute.lostandfound.viewmodel.ItemViewModel
import com.institute.lostandfound.viewmodel.AuthViewModel
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import com.institute.lostandfound.data.model.ItemType

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ItemViewModel by activityViewModels()
    private lateinit var itemAdapter: ItemAdapter
    private val authViewModel: AuthViewModel by activityViewModels()
    
    // References to count text views
    private lateinit var lostCountTextView: TextView
    private lateinit var foundCountTextView: TextView
    
    companion object {
        private const val TAG = "HomeFragment"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Creating view")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Setting up fragment")
        
        setupRecyclerView()
        setupObservers()
        setupRefreshLayout()
        setupClickListeners()
        initializeCountTextViews()
        
        // Test database connectivity first
        Log.d(TAG, "onViewCreated: Testing database connectivity")
        viewModel.testDatabaseConnection()
        viewModel.getCollectionStats()
        
        // Test optimized queries now that indexes are enabled
        Log.d(TAG, "onViewCreated: Testing optimized queries")
        viewModel.testOptimizedQueries()
        
        // Load initial data
        Log.d(TAG, "onViewCreated: Loading initial data")
        viewModel.loadAllItems()
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Refreshing data")
        // Refresh data when fragment becomes visible
        viewModel.loadAllItems()
    }
    
    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Setting up RecyclerView")
        itemAdapter = ItemAdapter(
            onItemClick = { item ->
                Log.d(TAG, "setupRecyclerView: Item clicked: ${item.title}")
                // Navigate to item detail
                navigateToItemDetail(item)
            }
        )
        
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
        Log.d(TAG, "setupRecyclerView: RecyclerView setup complete")
    }
    
    private fun setupObservers() {
        Log.d(TAG, "setupObservers: Setting up observers")
        
        viewModel.items.observe(viewLifecycleOwner) { items ->
            Log.d(TAG, "setupObservers: Items updated - Count: ${items.size}")
            items.forEach { item ->
                Log.d(TAG, "setupObservers: Item: ${item.title}, Type: ${item.type}, Status: ${item.status}")
            }
            
            itemAdapter.updateItems(items)
            updateEmptyState(items.isEmpty())
            updateActionButtonCounts(items)
            
            Log.d(TAG, "setupObservers: Adapter updated, empty state: ${items.isEmpty()}")
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d(TAG, "setupObservers: Loading state changed: $isLoading")
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e(TAG, "setupObservers: Error received: $it")
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Add observer for current user to debug authentication
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            Log.d(TAG, "setupObservers: Current user: ${user?.name ?: "null"}")
        }
        
        Log.d(TAG, "setupObservers: All observers set up")
    }
    
    private fun setupRefreshLayout() {
        Log.d(TAG, "setupRefreshLayout: Setting up refresh layout")
        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d(TAG, "setupRefreshLayout: Refresh triggered by user")
            viewModel.loadAllItems()
        }
    }
    
    private fun setupClickListeners() {
        Log.d(TAG, "setupClickListeners: Setting up click listeners")
        
        binding.cardLostItems.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Lost items card clicked")
            // Navigate to lost items fragment
            findNavController().navigate(R.id.action_home_to_lost)
        }
        
        binding.cardFoundItems.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Found items card clicked")
            // Navigate to found items fragment
            findNavController().navigate(R.id.action_home_to_found)
        }
        
        binding.fabPost.setOnClickListener {
            Log.d(TAG, "setupClickListeners: FAB clicked")
            // Navigate to post item fragment
            findNavController().navigate(R.id.action_home_to_post)
        }
        
        // Add sign out button click listener
        binding.buttonSignOut.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Sign out clicked")
            authViewModel.signOut()
            findNavController().navigate(R.id.action_home_to_auth)
        }
        
        // Add debug button for testing data loading
        binding.buttonSignOut.setOnLongClickListener {
            Log.d(TAG, "setupClickListeners: Sign out button long pressed - Debug mode activated")
            // Test database connection and reload data
            viewModel.testDatabaseConnection()
            viewModel.getCollectionStats()
            viewModel.loadAllItems()
            Toast.makeText(context, "Debug: Testing database connection and reloading data", Toast.LENGTH_SHORT).show()
            true
        }
    }
    
    private fun initializeCountTextViews() {
        // Find the count text views within the card layouts
        lostCountTextView = binding.cardLostItems.findViewById(R.id.text_lost_count)
        foundCountTextView = binding.cardFoundItems.findViewById(R.id.text_found_count)
        
        Log.d(TAG, "initializeCountTextViews: Count text views initialized")
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        Log.d(TAG, "updateEmptyState: Updating empty state - isEmpty: $isEmpty")
        if (isEmpty) {
            binding.textEmptyState.visibility = View.VISIBLE
            binding.recyclerViewItems.visibility = View.GONE
            Log.d(TAG, "updateEmptyState: Showing empty state")
        } else {
            binding.textEmptyState.visibility = View.GONE
            binding.recyclerViewItems.visibility = View.VISIBLE
            Log.d(TAG, "updateEmptyState: Showing items list")
        }
    }
    
    private fun updateActionButtonCounts(items: List<Item>) {
        val lostItemsCount = items.count { it.type == ItemType.LOST }
        val foundItemsCount = items.count { it.type == ItemType.FOUND }
        
        // Update the count text views
        lostCountTextView.text = "$lostItemsCount"
        foundCountTextView.text = "$foundItemsCount"
        
        Log.d(TAG, "updateActionButtonCounts: Lost: $lostItemsCount, Found: $foundItemsCount")
    }
    
    private fun navigateToItemDetail(item: Item) {
        Log.d(TAG, "navigateToItemDetail: Navigating to item detail: ${item.title}")
        // Navigate to item detail fragment
        viewModel.setSelectedItem(item)
        findNavController().navigate(R.id.action_home_to_item_detail)
    }
    
    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: Destroying view")
        super.onDestroyView()
        _binding = null
    }
} 