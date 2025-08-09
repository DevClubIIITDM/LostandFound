package com.institute.lostandfound.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.institute.lostandfound.R
import com.institute.lostandfound.data.model.Category
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.ItemType
import com.institute.lostandfound.databinding.FragmentPostItemBinding
import com.institute.lostandfound.viewmodel.ItemViewModel
import java.util.*

class PostItemFragment : Fragment() {
    private var _binding: FragmentPostItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(binding.imagePreview)
                binding.imagePreview.visibility = View.VISIBLE
                binding.buttonRemoveImage.visibility = View.VISIBLE
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupClickListeners()
    }

    private fun setupSpinners() {
        // Category spinner
        val categories = Category.values().map { it.displayName }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        // Type spinner
        val types = listOf("Lost", "Found")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter
    }

    private fun setupClickListeners() {
        binding.buttonAddImage.setOnClickListener {
            checkPermissionAndOpenPicker()
        }

        binding.buttonRemoveImage.setOnClickListener {
            selectedImageUri = null
            binding.imagePreview.visibility = View.GONE
            binding.buttonRemoveImage.visibility = View.GONE
        }

        binding.buttonSubmit.setOnClickListener {
            submitItem()
        }
    }

    private fun checkPermissionAndOpenPicker() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun submitItem() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val location = binding.editTextLocation.text.toString().trim()
        val contactInfo = binding.editTextContact.text.toString().trim()
        val reporterName = binding.editTextReporterName.text.toString().trim()
        val reporterEmail = binding.editTextReporterEmail.text.toString().trim()

        if (validateInput(title, description, location, contactInfo, reporterName, reporterEmail)) {
            val category = Category.values()[binding.spinnerCategory.selectedItemPosition].name
            val itemType = if (binding.spinnerType.selectedItemPosition == 0) ItemType.LOST else ItemType.FOUND

            val item = Item(
                title = title,
                description = description,
                category = category,
                type = itemType,
                location = location,
                dateReported = Date(),
                contactInfo = contactInfo,
                imageUri = selectedImageUri?.toString(),
                reporterName = reporterName,
                reporterEmail = reporterEmail
            )

            viewModel.insertItem(item)
            Toast.makeText(context, "Item posted successfully!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun validateInput(
        title: String,
        description: String,
        location: String,
        contactInfo: String,
        reporterName: String,
        reporterEmail: String
    ): Boolean {
        return when {
            title.isEmpty() -> {
                binding.editTextTitle.error = "Title is required"
                false
            }
            description.isEmpty() -> {
                binding.editTextDescription.error = "Description is required"
                false
            }
            location.isEmpty() -> {
                binding.editTextLocation.error = "Location is required"
                false
            }
            contactInfo.isEmpty() -> {
                binding.editTextContact.error = "Contact info is required"
                false
            }
            reporterName.isEmpty() -> {
                binding.editTextReporterName.error = "Name is required"
                false
            }
            reporterEmail.isEmpty() -> {
                binding.editTextReporterEmail.error = "Email is required"
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 