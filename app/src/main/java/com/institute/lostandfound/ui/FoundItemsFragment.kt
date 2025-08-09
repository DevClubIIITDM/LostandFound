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
import com.institute.lostandfound.databinding.FragmentFoundItemsBinding
import com.institute.lostandfound.viewmodel.ItemViewModel

class FoundItemsFragment : Fragment() {
    private var _binding: FragmentFoundItemsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoundItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter { item ->
            viewModel.selectItem(item)
            findNavController().navigate(R.id.action_found_to_item_detail)
        }
        
        binding.recyclerViewFoundItems.apply {
            adapter = this@FoundItemsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeData() {
        viewModel.getItemsByType(ItemType.FOUND).observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.textEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 