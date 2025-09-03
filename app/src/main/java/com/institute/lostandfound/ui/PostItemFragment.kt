package com.institute.lostandfound.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.google.firebase.Timestamp
import com.institute.lostandfound.data.model.ContactInfo
import com.institute.lostandfound.data.model.Location
import com.institute.lostandfound.data.model.Categories

class PostItemFragment : Fragment() {
    companion object {
        private const val TAG = "PostItemFragment"
    }
    
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
                Log.d(TAG, "Image selected: $uri")
                Glide.with(this)
                    .load(uri)
                    .into(binding.imagePreview)
                binding.imagePreview.visibility = View.VISIBLE
                binding.buttonRemoveImage.visibility = View.VISIBLE
                binding.buttonAddImage.text = "Change Image"
            }
        }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri?.let { uri ->
                Log.d(TAG, "Photo captured: $uri")
                Glide.with(this)
                    .load(uri)
                    .into(binding.imagePreview)
                binding.imagePreview.visibility = View.VISIBLE
                binding.buttonRemoveImage.visibility = View.VISIBLE
                binding.buttonAddImage.text = "Change Image"
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Storage permission result: $isGranted")
        if (isGranted) {
            Log.d(TAG, "Storage permission granted, showing image source dialog")
            showImageSourceDialog()
        } else {
            Log.w(TAG, "Storage permission denied")
            Toast.makeText(context, "Storage permission denied. Cannot access gallery.", Toast.LENGTH_LONG).show()
            // Show image source dialog anyway, but only camera option
            showImageSourceDialogCameraOnly()
        }
    }
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Camera permission result: $isGranted")
        if (isGranted) {
            Log.d(TAG, "Camera permission granted, showing image source dialog")
            showImageSourceDialog()
        } else {
            Log.w(TAG, "Camera permission denied")
            Toast.makeText(context, "Camera permission denied. Cannot take photos.", Toast.LENGTH_LONG).show()
            // Show image source dialog anyway, but only gallery option
            showImageSourceDialogGalleryOnly()
        }
    }
    
    private fun showImageSourceDialogCameraOnly() {
        val options = arrayOf("Camera")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setMessage("Storage permission denied. Only camera option available.")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                }
            }
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showImageSourceDialogGalleryOnly() {
        val options = arrayOf("Gallery")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setMessage("Camera permission denied. Only gallery option available.")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openImagePicker()
                }
            }
            .setPositiveButton("OK", null)
            .show()
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
        setupObservers()
    }

    private fun setupSpinners() {
        // Category spinner
        val categories = Categories.getAllCategories().map { it.name }
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
            Log.d(TAG, "Add image button clicked")
            checkPermissionAndOpenPicker()
        }

        binding.buttonRemoveImage.setOnClickListener {
            Log.d(TAG, "Remove image button clicked")
            selectedImageUri = null
            binding.imagePreview.visibility = View.GONE
            binding.buttonRemoveImage.visibility = View.GONE
            binding.buttonAddImage.text = "Add Image"
        }

        binding.buttonSubmit.setOnClickListener {
            Log.d(TAG, "Submit button clicked")
            submitItem()
        }
        
        binding.buttonViewItems.setOnClickListener {
            Log.d(TAG, "View items button clicked")
            // Navigate back to home to view posted items
            findNavController().popBackStack()
        }
    }
    
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.buttonSubmit.isEnabled = !isLoading
            if (isLoading) {
                binding.buttonSubmit.text = "Posting Item..."
                Log.d(TAG, "Item posting started")
            } else {
                binding.buttonSubmit.text = "Post Item"
                Log.d(TAG, "Item posting completed")
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e(TAG, "Error posting item: $it")
                Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Observe items to detect when a new item is added successfully
        // But only navigate after a delay to allow user to see the success message
        viewModel.items.observe(viewLifecycleOwner) { items ->
            // Only navigate if we're not loading and items were just added
            if (items.isNotEmpty() && viewModel.isLoading.value == false) {
                // Check if this is a new item (we can detect this by checking if we just finished posting)
                // For now, we'll use a simple approach: show success message and let user navigate manually
                Log.d(TAG, "Item added successfully, showing success message")
                Toast.makeText(context, "✅ Item posted successfully! Use 'View Posted Items' to see it.", Toast.LENGTH_LONG).show()
                
                // Clear the form for the next item
                clearForm()
                
                // Don't auto-navigate - let user decide when to go back
                // findNavController().popBackStack() // Commented out to prevent auto-navigation
            }
        }
    }
    
    private fun clearForm() {
        Log.d(TAG, "Clearing form for next item")
        binding.editTextTitle.text?.clear()
        binding.editTextDescription.text?.clear()
        binding.editTextLocation.text?.clear()
        binding.editTextContact.text?.clear()
        binding.editTextReporterName.text?.clear()
        binding.editTextReporterEmail.text?.clear()
        binding.spinnerCategory.setSelection(0)
        binding.spinnerType.setSelection(0)
        
        // Clear image
        selectedImageUri = null
        binding.imagePreview.visibility = View.GONE
        binding.buttonRemoveImage.visibility = View.GONE
        binding.buttonAddImage.text = "Add Image"
        
        // Show the "View Posted Items" button
        binding.buttonViewItems.visibility = View.VISIBLE
        
        // Reset focus to title field
        binding.editTextTitle.requestFocus()
    }

    private fun checkPermissionAndOpenPicker() {
        Log.d(TAG, "checkPermissionAndOpenPicker: Starting permission check")
        Log.d(TAG, "checkPermissionAndOpenPicker: Android SDK version: ${android.os.Build.VERSION.SDK_INT}")
        Log.d(TAG, "checkPermissionAndOpenPicker: TIRAMISU version: ${android.os.Build.VERSION_CODES.TIRAMISU}")
        
        // For Android 13+ (API 33+), we don't need READ_EXTERNAL_STORAGE for media access
        // For camera, we always need CAMERA permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - only check camera permission
            Log.d(TAG, "checkPermissionAndOpenPicker: Android 13+, checking camera permission only")
            checkCameraPermission()
        } else {
            // Android 12 and below - check storage permission
            Log.d(TAG, "checkPermissionAndOpenPicker: Android 12 or below, checking storage permission")
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "checkPermissionAndOpenPicker: Storage permission already granted")
                    showImageSourceDialog()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Log.d(TAG, "checkPermissionAndOpenPicker: Should show permission rationale")
                    showPermissionRationaleDialog()
                }
                else -> {
                    Log.d(TAG, "checkPermissionAndOpenPicker: Requesting storage permission")
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
    
    private fun checkCameraPermission() {
        Log.d(TAG, "checkCameraPermission: Checking camera permission")
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "checkCameraPermission: Camera permission already granted")
                showImageSourceDialog()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Log.d(TAG, "checkCameraPermission: Should show permission rationale")
                showCameraPermissionRationaleDialog()
            }
            else -> {
                Log.d(TAG, "checkCameraPermission: Requesting camera permission")
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun showPermissionRationaleDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Storage Permission Required")
            .setMessage("This app needs access to your device's storage to select images from your gallery. Please grant the permission to continue.")
            .setPositiveButton("Grant Permission") { _, _ ->
                Log.d(TAG, "showPermissionRationaleDialog: User clicked grant, launching permission request")
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showCameraPermissionRationaleDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Camera Permission Required")
            .setMessage("This app needs access to your device's camera to take photos. Please grant the permission to continue.")
            .setPositiveButton("Grant Permission") { _, _ ->
                Log.d(TAG, "showCameraPermissionRationaleDialog: User clicked grant, launching permission request")
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openImagePicker()
                }
            }
            .show()
    }
    
    private fun openImagePicker() {
        Log.d(TAG, "Opening image picker")
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening image picker", e)
            Toast.makeText(context, "Error opening gallery: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openCamera() {
        Log.d(TAG, "Opening camera")
        try {
            // Create a temporary file for the photo
            val photoFile = createImageFile()
            selectedImageUri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            
            cameraLauncher.launch(selectedImageUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening camera", e)
            Toast.makeText(context, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun createImageFile(): java.io.File {
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return java.io.File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun submitItem() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val location = binding.editTextLocation.text.toString().trim()
        val contactInfo = binding.editTextContact.text.toString().trim()
        val reporterName = binding.editTextReporterName.text.toString().trim()
        val reporterEmail = binding.editTextReporterEmail.text.toString().trim()

        Log.d(TAG, "Submitting item - Title: $title, Has Image: ${selectedImageUri != null}")

        if (validateInput(title, description, location, contactInfo, reporterName, reporterEmail)) {
            // Hide the "View Posted Items" button when starting to post
            binding.buttonViewItems.visibility = View.GONE
            
            val category = Categories.getAllCategories()[binding.spinnerCategory.selectedItemPosition].name
            val itemType = if (binding.spinnerType.selectedItemPosition == 0) ItemType.LOST else ItemType.FOUND

            val item = Item(
                title = title,
                description = description,
                category = category,
                type = itemType,
                location = Location(
                    address = location,
                    placeName = location
                ),
                dateReported = Timestamp.now(),
                contactInfo = ContactInfo(
                    phone = contactInfo,
                    email = reporterEmail
                ),
                images = if (selectedImageUri != null) listOf(selectedImageUri.toString()) else emptyList(),
                imageUri = selectedImageUri?.toString() ?: "",
                reporterName = reporterName,
                reporterEmail = reporterEmail
            )

            Log.d(TAG, "Item created successfully, calling viewModel.addItem()")
            viewModel.addItem(item)
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