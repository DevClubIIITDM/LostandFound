package com.institute.lostandfound.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.User
import com.institute.lostandfound.repository.FirebaseRepository
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {
    companion object {
        private const val TAG = "ItemViewModel"
    }
    
    private val repository = FirebaseRepository()
    
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items
    
    private val _lostItems = MutableLiveData<List<Item>>()
    val lostItems: LiveData<List<Item>> = _lostItems
    
    private val _foundItems = MutableLiveData<List<Item>>()
    val foundItems: LiveData<List<Item>> = _foundItems
    
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _selectedItem = MutableLiveData<Item?>()
    val selectedItem: LiveData<Item?> = _selectedItem
    
    init {
        Log.d(TAG, "ItemViewModel: Initializing ViewModel")
        initializeUser()
        loadAllItems()
    }
    
    private fun initializeUser() {
        Log.d(TAG, "initializeUser: Starting user initialization")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "initializeUser: Loading state set to true")
                
                // Try to get existing user
                val userId = repository.getCurrentUserId()
                Log.d(TAG, "initializeUser: Retrieved current user ID: $userId")
                
                if (userId != null) {
                    val user = repository.getUser(userId)
                    if (user.isSuccess && user.getOrNull() != null) {
                        val userObj = user.getOrNull()
                        Log.d(TAG, "initializeUser: Successfully retrieved user: ${userObj?.name}")
                        _currentUser.value = userObj
                    } else {
                        val errorMsg = "User not found. Please sign in with Google."
                        Log.w(TAG, "initializeUser: $errorMsg")
                        _error.value = errorMsg
                    }
                } else {
                    val errorMsg = "Please sign in with Google to continue."
                    Log.w(TAG, "initializeUser: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Failed to initialize user: ${e.message}"
                Log.e(TAG, "initializeUser: $errorMsg", e)
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "initializeUser: Loading state set to false")
            }
        }
    }
    
    fun loadAllItems() {
        Log.d(TAG, "loadAllItems: Starting to load all items")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "loadAllItems: Loading state set to true, error cleared")
                
                Log.d(TAG, "loadAllItems: Calling repository.getItems()")
                val result = repository.getItems()
                
                if (result.isSuccess) {
                    val allItems = result.getOrNull() ?: emptyList()
                    Log.d(TAG, "loadAllItems: Successfully retrieved ${allItems.size} items from repository")
                    
                    // Log each item for debugging
                    allItems.forEach { item ->
                        Log.d(TAG, "loadAllItems: Item - ID: ${item.id}, Title: ${item.title}, Type: ${item.type}, Status: ${item.status}")
                    }
                    
                    _items.value = allItems
                    Log.d(TAG, "loadAllItems: Updated _items LiveData with ${allItems.size} items")
                    
                    // Separate lost and found items
                    val lostItems = allItems.filter { it.type.name == "LOST" }
                    val foundItems = allItems.filter { it.type.name == "FOUND" }
                    
                    Log.d(TAG, "loadAllItems: Separated items - Lost: ${lostItems.size}, Found: ${foundItems.size}")
                    
                    _lostItems.value = lostItems
                    _foundItems.value = foundItems
                    
                    Log.d(TAG, "loadAllItems: Updated _lostItems and _foundItems LiveData")
                } else {
                    val errorMsg = "Failed to load items"
                    Log.e(TAG, "loadAllItems: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error loading items: ${e.message}"
                Log.e(TAG, "loadAllItems: $errorMsg", e)
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadAllItems: Loading state set to false")
            }
        }
    }
    
    fun loadLostItems() {
        Log.d(TAG, "loadLostItems: Starting to load lost items")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "loadLostItems: Loading state set to true, error cleared")
                
                Log.d(TAG, "loadLostItems: Calling repository.getItems(type = 'LOST')")
                val result = repository.getItems(type = "LOST")
                
                if (result.isSuccess) {
                    val lostItems = result.getOrNull() ?: emptyList()
                    Log.d(TAG, "loadLostItems: Successfully retrieved ${lostItems.size} lost items")
                    
                    // Log each lost item for debugging
                    lostItems.forEach { item ->
                        Log.d(TAG, "loadLostItems: Lost Item - ID: ${item.id}, Title: ${item.title}, Status: ${item.status}")
                    }
                    
                    _lostItems.value = lostItems
                    Log.d(TAG, "loadLostItems: Updated _lostItems LiveData")
                } else {
                    val errorMsg = "Failed to load lost items"
                    Log.e(TAG, "loadLostItems: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error loading lost items: ${e.message}"
                Log.e(TAG, "loadLostItems: $errorMsg", e)
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadLostItems: Loading state set to false")
            }
        }
    }
    
    fun loadFoundItems() {
        Log.d(TAG, "loadFoundItems: Starting to load found items")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "loadFoundItems: Loading state set to true, error cleared")
                
                Log.d(TAG, "loadFoundItems: Calling repository.getItems(type = 'FOUND')")
                val result = repository.getItems(type = "FOUND")
                
                if (result.isSuccess) {
                    val foundItems = result.getOrNull() ?: emptyList()
                    Log.d(TAG, "loadFoundItems: Successfully retrieved ${foundItems.size} found items")
                    
                    // Log each found item for debugging
                    foundItems.forEach { item ->
                        Log.d(TAG, "loadFoundItems: Found Item - ID: ${item.id}, Title: ${item.title}, Status: ${item.status}")
                    }
                    
                    _foundItems.value = foundItems
                    Log.d(TAG, "loadFoundItems: Updated _foundItems LiveData")
                } else {
                    val errorMsg = "Failed to load found items"
                    Log.e(TAG, "loadFoundItems: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error loading found items: ${e.message}"
                Log.e(TAG, "loadFoundItems: $errorMsg", e)
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadFoundItems: Loading state set to false")
            }
        }
    }
    
    fun addItem(item: Item) {
        Log.d(TAG, "addItem: Starting to add item: ${item.title}")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "addItem: Loading state set to true, error cleared")
                
                val currentUser = _currentUser.value
                Log.d(TAG, "addItem: Current user: ${currentUser?.name ?: "null"}")
                
                if (currentUser != null) {
                    val itemWithUser = item.copy(userId = currentUser.id)
                    Log.d(TAG, "addItem: Item with user ID: ${itemWithUser.userId}")
                    
                    // First, add the item to get an ID
                    Log.d(TAG, "addItem: Calling repository.addItem() to get item ID")
                    val result = repository.addItem(itemWithUser)
                    
                    if (result.isSuccess) {
                        val newItemId = result.getOrNull() ?: ""
                        Log.d(TAG, "addItem: Item added successfully with ID: $newItemId")
                        
                        // If there are images, upload them to Firebase Storage
                        if (itemWithUser.images.isNotEmpty()) {
                            Log.d(TAG, "addItem: Uploading ${itemWithUser.images.size} images to Firebase Storage")
                            val uploadResult = repository.uploadImages(itemWithUser.images, newItemId)
                            
                            if (uploadResult.isSuccess) {
                                val downloadUrls = uploadResult.getOrNull() ?: emptyList()
                                Log.d(TAG, "addItem: Images uploaded successfully. Download URLs: $downloadUrls")
                                
                                // Update the item with the download URLs
                                val itemWithImages = itemWithUser.copy(
                                    id = newItemId,
                                    images = downloadUrls,
                                    imageUri = downloadUrls.firstOrNull() ?: ""
                                )
                                
                                // Update the item in Firestore with the image URLs
                                val updateResult = repository.updateItem(itemWithImages)
                                if (updateResult.isSuccess) {
                                    Log.d(TAG, "addItem: Item updated with image URLs successfully")
                                } else {
                                    Log.w(TAG, "addItem: Failed to update item with image URLs")
                                }
                                
                                // Add to current lists with image URLs
                                addItemToLists(itemWithImages)
                            } else {
                                Log.w(TAG, "addItem: Failed to upload images, but item was created")
                                // Item was created but images failed to upload
                                val itemWithoutImages = itemWithUser.copy(
                                    id = newItemId,
                                    images = emptyList(),
                                    imageUri = ""
                                )
                                addItemToLists(itemWithoutImages)
                            }
                        } else {
                            Log.d(TAG, "addItem: No images to upload")
                            // No images, just add to current lists
                            val itemWithoutImages = itemWithUser.copy(id = newItemId)
                            addItemToLists(itemWithoutImages)
                        }
                        
                        // Show success message and reload for consistency
                        Log.d(TAG, "addItem: Item creation completed successfully")
                        loadAllItems()
                    } else {
                        val errorMsg = "Failed to add item"
                        Log.e(TAG, "addItem: $errorMsg")
                        _error.value = errorMsg
                    }
                } else {
                    val errorMsg = "User not initialized. Please sign in with Google."
                    Log.w(TAG, "addItem: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error adding item: ${e.message}"
                Log.e(TAG, "addItem: $errorMsg", e)
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
                Log.d(TAG, "addItem: Loading state set to false")
            }
        }
    }
    
    private fun addItemToLists(newItem: Item) {
        Log.d(TAG, "addItemToLists: Adding item to current lists - Title: ${newItem.title}")
        
        val currentAllItems = _items.value?.toMutableList() ?: mutableListOf()
        val currentLostItems = _lostItems.value?.toMutableList() ?: mutableListOf()
        val currentFoundItems = _foundItems.value?.toMutableList() ?: mutableListOf()
        
        Log.d(TAG, "addItemToLists: Current lists - All: ${currentAllItems.size}, Lost: ${currentLostItems.size}, Found: ${currentFoundItems.size}")
        
        // Add to all items list
        currentAllItems.add(0, newItem)
        _items.value = currentAllItems
        Log.d(TAG, "addItemToLists: Added to all items list, new count: ${currentAllItems.size}")
        
        // Add to appropriate type list
        if (newItem.type.name == "LOST") {
            currentLostItems.add(0, newItem)
            _lostItems.value = currentLostItems
            Log.d(TAG, "addItemToLists: Added to lost items list, new count: ${currentLostItems.size}")
        } else {
            currentFoundItems.add(0, newItem)
            _foundItems.value = currentFoundItems
            Log.d(TAG, "addItemToLists: Added to found items list, new count: ${currentFoundItems.size}")
        }
    }
    
    fun updateItem(item: Item) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.updateItem(item)
                if (result.isSuccess) {
                    // Reload items
                    loadAllItems()
                } else {
                    _error.value = "Failed to update item"
                }
            } catch (e: Exception) {
                _error.value = "Error updating item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.deleteItem(itemId)
                if (result.isSuccess) {
                    // Reload items
                    loadAllItems()
                } else {
                    _error.value = "Failed to delete item"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchItems(query: String) {
        if (query.isBlank()) {
            loadAllItems()
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.searchItems(query)
                if (result.isSuccess) {
                    val searchResults = result.getOrNull() ?: emptyList()
                    _items.value = searchResults
                    
                    // Update lost and found items based on search results
                    _lostItems.value = searchResults.filter { it.type.name == "LOST" }
                    _foundItems.value = searchResults.filter { it.type.name == "FOUND" }
                } else {
                    _error.value = "Failed to search items"
                }
            } catch (e: Exception) {
                _error.value = "Error searching items: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getItemsByType(type: String): List<Item> {
        return when (type.uppercase()) {
            "LOST" -> _lostItems.value ?: emptyList()
            "FOUND" -> _foundItems.value ?: emptyList()
            else -> _items.value ?: emptyList()
        }
    }

    fun clearError() {
        _error.value = null
    }
    
    fun setSelectedItem(item: Item?) {
        _selectedItem.value = item
    }
    
    // Test database connectivity
    fun testDatabaseConnection() {
        Log.d(TAG, "testDatabaseConnection: Testing database connectivity")
        viewModelScope.launch {
            try {
                val result = repository.testDatabaseConnection()
                if (result.isSuccess) {
                    Log.d(TAG, "testDatabaseConnection: Database connection successful")
                } else {
                    Log.e(TAG, "testDatabaseConnection: Database connection failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "testDatabaseConnection: Exception during connection test", e)
            }
        }
    }
    
    // Get collection statistics
    fun getCollectionStats() {
        Log.d(TAG, "getCollectionStats: Getting collection statistics")
        viewModelScope.launch {
            try {
                val result = repository.getCollectionStats()
                if (result.isSuccess) {
                    val stats = result.getOrNull()
                    Log.d(TAG, "getCollectionStats: Collection statistics - $stats")
                } else {
                    Log.e(TAG, "getCollectionStats: Failed to get collection statistics")
                }
            } catch (e: Exception) {
                Log.e(TAG, "getCollectionStats: Exception getting collection stats", e)
            }
        }
    }
    
    // Test optimized queries
    fun testOptimizedQueries() {
        Log.d(TAG, "testOptimizedQueries: Testing optimized queries")
        viewModelScope.launch {
            try {
                val result = repository.testOptimizedQuery()
                if (result.isSuccess) {
                    Log.d(TAG, "testOptimizedQueries: ✅ Optimized queries are working correctly")
                } else {
                    Log.e(TAG, "testOptimizedQueries: ❌ Optimized queries failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "testOptimizedQueries: Exception testing optimized queries", e)
            }
        }
    }
    
    fun markAsResolved(itemId: String) {
        Log.d(TAG, "markAsResolved: Marking item as resolved: $itemId")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val currentItems = _items.value ?: emptyList()
                val itemToUpdate = currentItems.find { it.id == itemId }
                
                if (itemToUpdate != null) {
                    val updatedItem = itemToUpdate.copy(isResolved = true)
                    val result = repository.updateItem(updatedItem)
                    if (result.isSuccess) {
                        // Reload items
                        loadAllItems()
                    } else {
                        _error.value = "Failed to mark item as resolved"
                    }
                } else {
                    _error.value = "Item not found"
                }
            } catch (e: Exception) {
                _error.value = "Error marking item as resolved: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 