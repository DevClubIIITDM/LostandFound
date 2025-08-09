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
import com.institute.lostandfound.databinding.FragmentHomeBinding
import com.institute.lostandfound.viewmodel.ItemViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter { item ->
            viewModel.selectItem(item)
            findNavController().navigate(R.id.action_home_to_item_detail)
        }
        
        binding.recyclerViewRecentItems.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeData() {
        viewModel.getUnresolvedItems().observe(viewLifecycleOwner) { items ->
            adapter.submitList(items.take(10)) // Show only recent 10 items
            binding.textEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.cardLostItems.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_lost)
        }
        
        binding.cardFoundItems.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_found)
        }
        
        binding.fabPost.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_post)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 