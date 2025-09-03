package com.institute.lostandfound.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
import android.net.Uri

class FirebaseRepository {
    companion object {
        private const val TAG = "FirebaseRepository"
    }
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    // Collections
    private val usersCollection = firestore.collection("users")
    private val itemsCollection = firestore.collection("items")
    
    init {
        Log.d(TAG, "FirebaseRepository: Initialized with collections - users: $usersCollection, items: $itemsCollection")
    }
    
    // Authentication
    suspend fun signOut() {
        Log.d(TAG, "signOut: Signing out user")
        auth.signOut()
        Log.d(TAG, "signOut: User signed out successfully")
    }
    
    suspend fun signInWithGoogle(idToken: String): Result<String> {
        Log.d(TAG, "signInWithGoogle: Attempting to sign in with Google token")
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val userId = result.user?.uid ?: ""
            Log.d(TAG, "signInWithGoogle: Successfully signed in with user ID: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithGoogle: Failed to sign in with Google", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUserId(): String? {
        val userId = auth.currentUser?.uid
        Log.d(TAG, "getCurrentUserId: Current user ID: $userId")
        return userId
    }
    
    // User operations
    suspend fun createUser(user: User): Result<String> {
        Log.d(TAG, "createUser: Creating user with name: ${user.name}")
        return try {
            val userId = user.id.ifEmpty { UUID.randomUUID().toString() }
            val userWithId = user.copy(id = userId)
            Log.d(TAG, "createUser: Setting user document with ID: $userId")
            usersCollection.document(userId).set(userWithId).await()
            Log.d(TAG, "createUser: User created successfully with ID: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "createUser: Failed to create user", e)
            Result.failure(e)
        }
    }
    
    suspend fun getUser(userId: String): Result<User?> {
        Log.d(TAG, "getUser: Fetching user with ID: $userId")
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            if (user != null) {
                Log.d(TAG, "getUser: Successfully retrieved user: ${user.name}")
            } else {
                Log.w(TAG, "getUser: User document exists but couldn't be converted to User object")
            }
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "getUser: Failed to get user with ID: $userId", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(user: User): Result<Unit> {
        Log.d(TAG, "updateUser: Updating user with ID: ${user.id}")
        return try {
            usersCollection.document(user.id).set(user).await()
            Log.d(TAG, "updateUser: User updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateUser: Failed to update user", e)
            Result.failure(e)
        }
    }
    
    // Item operations
    suspend fun addItem(item: Item): Result<String> {
        Log.d(TAG, "addItem: Adding item with title: ${item.title}")
        return try {
            val itemId = item.id.ifEmpty { UUID.randomUUID().toString() }
            val itemWithId = item.copy(
                id = itemId,
                status = com.institute.lostandfound.data.model.ItemStatus.ACTIVE,
                createdAt = com.google.firebase.Timestamp.now(),
                updatedAt = com.google.firebase.Timestamp.now()
            )
            
            Log.d(TAG, "addItem: Item details - ID: $itemId, Title: ${itemWithId.title}, Type: ${itemWithId.type}, Status: ${itemWithId.status}")
            Log.d(TAG, "addItem: Adding to collection: $itemsCollection")
            
            itemsCollection.document(itemId).set(itemWithId).await()
            Log.d(TAG, "addItem: Item added successfully with ID: $itemId")
            Result.success(itemId)
        } catch (e: Exception) {
            Log.e(TAG, "addItem: Error adding item: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun insertItem(item: Item): Result<String> {
        Log.d(TAG, "insertItem: Calling addItem for item: ${item.title}")
        return addItem(item)
    }
    
    suspend fun getItems(
        type: String? = null,
        category: String? = null,
        status: String = "ACTIVE",
        limit: Long = 50
    ): Result<List<Item>> {
        Log.d(TAG, "getItems: Fetching items with filters - type: $type, category: $category, status: $status, limit: $limit")
        return try {
            var query: Query = itemsCollection
            Log.d(TAG, "getItems: Starting with base query: $query")
            
            // Temporarily simplify the query to avoid composite index requirement
            // TODO: Re-enable filters once the proper composite index is created
            Log.d(TAG, "getItems: Using simplified query to avoid index requirement")
            
            // For now, just get all items without complex filtering
            // This will be slower but will work immediately
            val snapshot = query
                .limit(limit)
                .get()
                .await()
            
            Log.d(TAG, "getItems: Query executed successfully, snapshot size: ${snapshot.size()}")
            
            // Log raw document data for debugging
            snapshot.documents.forEach { doc ->
                Log.d(TAG, "getItems: Raw document - ID: ${doc.id}, Data: ${doc.data}")
            }
            
            val items = snapshot.toObjects(Item::class.java)
            
            // Apply filters in memory after fetching
            var filteredItems = items
            
            if (status.isNotEmpty()) {
                filteredItems = filteredItems.filter { it.status.name == status }
                Log.d(TAG, "getItems: Applied status filter '$status' in memory, filtered to ${filteredItems.size} items")
            }
            
            if (type != null) {
                filteredItems = filteredItems.filter { it.type.name == type }
                Log.d(TAG, "getItems: Applied type filter '$type' in memory, filtered to ${filteredItems.size} items")
            }
            
            if (category != null) {
                filteredItems = filteredItems.filter { it.category == category }
                Log.d(TAG, "getItems: Applied category filter '$category' in memory, filtered to ${filteredItems.size} items")
            }
            
            // Sort by createdAt in memory
            filteredItems = filteredItems.sortedByDescending { it.createdAt.toDate() }
            
            // Debug logging
            Log.d(TAG, "getItems: Retrieved ${filteredItems.size} items after filtering and sorting")
            filteredItems.forEach { item ->
                Log.d(TAG, "getItems: Item details - Title: ${item.title}, Type: ${item.type}, Status: ${item.status}, ID: ${item.id}")
            }
            
            if (filteredItems.isEmpty()) {
                Log.w(TAG, "getItems: No items found after filtering. This might indicate:")
                Log.w(TAG, "getItems: 1. Database is empty")
                Log.w(TAG, "getItems: 2. Status filter '$status' doesn't match any items")
                Log.w(TAG, "getItems: 3. Type filter '$type' doesn't match any items")
                Log.w(TAG, "getItems: 4. Category filter '$category' doesn't match any items")
                Log.w(TAG, "getItems: 5. Data model conversion is failing")
                
                // Try to get raw documents without conversion to see if data exists
                try {
                    val rawSnapshot = itemsCollection.limit(5).get().await()
                    Log.w(TAG, "getItems: Raw collection access - Found ${rawSnapshot.size()} documents")
                    rawSnapshot.documents.forEach { doc ->
                        Log.w(TAG, "getItems: Raw doc - ID: ${doc.id}, Exists: ${doc.exists()}, Data: ${doc.data}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "getItems: Failed to access raw collection", e)
                }
            }
            
            Result.success(filteredItems)
        } catch (e: Exception) {
            Log.e(TAG, "getItems: Error getting items: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getItem(itemId: String): Result<Item?> {
        Log.d(TAG, "getItem: Fetching item with ID: $itemId")
        return try {
            val document = itemsCollection.document(itemId).get().await()
            val item = document.toObject(Item::class.java)
            if (item != null) {
                Log.d(TAG, "getItem: Successfully retrieved item: ${item.title}")
            } else {
                Log.w(TAG, "getItem: Item document exists but couldn't be converted to Item object")
            }
            Result.success(item)
        } catch (e: Exception) {
            Log.e(TAG, "getItem: Failed to get item with ID: $itemId", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateItem(item: Item): Result<Unit> {
        Log.d(TAG, "updateItem: Updating item with ID: ${item.id}, title: ${item.title}")
        return try {
            val updatedItem = item.copy(updatedAt = com.google.firebase.Timestamp.now())
            itemsCollection.document(item.id).set(updatedItem).await()
            Log.d(TAG, "updateItem: Item updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateItem: Failed to update item", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteItem(itemId: String): Result<Unit> {
        Log.d(TAG, "deleteItem: Deleting item with ID: $itemId")
        return try {
            itemsCollection.document(itemId).delete().await()
            Log.d(TAG, "deleteItem: Item deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteItem: Failed to delete item", e)
            Result.failure(e)
        }
    }
    
    suspend fun searchItems(query: String): Result<List<Item>> {
        Log.d(TAG, "searchItems: Searching for items with query: $query")
        return try {
            // Temporarily use simplified search to avoid index requirements
            Log.d(TAG, "searchItems: Using simplified search approach")
            
            // Get all active items and search in memory
            val snapshot = itemsCollection
                .limit(100) // Get more items for search
                .get()
                .await()
            
            val allItems = snapshot.toObjects(Item::class.java)
            val activeItems = allItems.filter { it.status.name == "ACTIVE" }
            
            // Search in memory
            val searchResults = activeItems.filter { item ->
                item.title.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)
            }
            
            Log.d(TAG, "searchItems: Found ${searchResults.size} items matching query: $query using simplified search")
            Result.success(searchResults)
        } catch (e: Exception) {
            Log.e(TAG, "searchItems: Failed to search items", e)
            Result.failure(e)
        }
    }
    
    // Image upload
    suspend fun uploadImage(imageUri: String, itemId: String): Result<String> {
        Log.d(TAG, "uploadImage: Starting image upload for item ID: $itemId")
        return try {
            // Convert string URI to actual URI
            val uri = Uri.parse(imageUri)
            Log.d(TAG, "uploadImage: Parsed URI: $uri")
            
            // Create a unique filename for the image
            val filename = "${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child("items/$itemId/$filename")
            Log.d(TAG, "uploadImage: Uploading to path: items/$itemId/$filename")
            
            // Upload the image
            val uploadTask = imageRef.putFile(uri)
            val snapshot = uploadTask.await()
            
            // Get the download URL
            val downloadUrl = snapshot.storage.downloadUrl.await()
            Log.d(TAG, "uploadImage: Image uploaded successfully. Download URL: $downloadUrl")
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Log.e(TAG, "uploadImage: Failed to upload image", e)
            Result.failure(e)
        }
    }
    
    // Upload multiple images
    suspend fun uploadImages(imageUris: List<String>, itemId: String): Result<List<String>> {
        Log.d(TAG, "uploadImages: Starting upload of ${imageUris.size} images for item ID: $itemId")
        return try {
            val downloadUrls = mutableListOf<String>()
            
            imageUris.forEachIndexed { index, imageUri ->
                Log.d(TAG, "uploadImages: Uploading image ${index + 1}/${imageUris.size}")
                val result = uploadImage(imageUri, itemId)
                if (result.isSuccess) {
                    downloadUrls.add(result.getOrNull()!!)
                    Log.d(TAG, "uploadImages: Successfully uploaded image ${index + 1}")
                } else {
                    Log.e(TAG, "uploadImages: Failed to upload image ${index + 1}")
                }
            }
            
            Log.d(TAG, "uploadImages: Completed upload of ${downloadUrls.size}/${imageUris.size} images")
            Result.success(downloadUrls)
        } catch (e: Exception) {
            Log.e(TAG, "uploadImages: Failed to upload images", e)
            Result.failure(e)
        }
    }
    
    // Test database connectivity
    suspend fun testDatabaseConnection(): Result<Boolean> {
        Log.d(TAG, "testDatabaseConnection: Testing database connectivity")
        return try {
            // Try to access the items collection
            val snapshot = itemsCollection.limit(1).get().await()
            Log.d(TAG, "testDatabaseConnection: Successfully connected to database, collection accessible")
            Log.d(TAG, "testDatabaseConnection: Collection path: ${itemsCollection.path}")
            Log.d(TAG, "testDatabaseConnection: Collection ID: ${itemsCollection.id}")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "testDatabaseConnection: Failed to connect to database", e)
            Result.failure(e)
        }
    }
    
    // Get collection statistics
    suspend fun getCollectionStats(): Result<Map<String, Any>> {
        Log.d(TAG, "getCollectionStats: Getting collection statistics")
        return try {
            val itemsSnapshot = itemsCollection.get().await()
            val usersSnapshot = usersCollection.get().await()
            
            val stats = mapOf(
                "itemsCount" to itemsSnapshot.size(),
                "usersCount" to usersSnapshot.size(),
                "itemsCollectionPath" to itemsCollection.path,
                "usersCollectionPath" to usersCollection.path
            )
            
            Log.d(TAG, "getCollectionStats: Collection stats - Items: ${stats["itemsCount"]}, Users: ${stats["usersCount"]}")
            Result.success(stats)
        } catch (e: Exception) {
            Log.e(TAG, "getCollectionStats: Failed to get collection statistics", e)
            Result.failure(e)
        }
    }
    
    // Check if the composite index exists and re-enable optimized queries
    suspend fun checkAndEnableOptimizedQueries(): Result<Boolean> {
        Log.d(TAG, "checkAndEnableOptimizedQueries: Checking if composite index exists")
        return try {
            // Try to execute the optimized query to see if the index exists
            val testQuery = itemsCollection
                .whereEqualTo("status", "ACTIVE")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            
            Log.d(TAG, "checkAndEnableOptimizedQueries: ✅ Composite index exists! Optimized queries are working")
            Log.d(TAG, "checkAndEnableOptimizedQueries: Test query returned ${testQuery.size()} documents")
            Result.success(true)
        } catch (e: Exception) {
            if (e.message?.contains("requires an index") == true) {
                Log.w(TAG, "checkAndEnableOptimizedQueries: ❌ Composite index not yet created, using simplified queries")
                Result.success(false)
            } else {
                Log.e(TAG, "checkAndEnableOptimizedQueries: Unexpected error checking index", e)
                Result.failure(e)
            }
        }
    }
    
    // Test the full optimized query to ensure it's working
    suspend fun testOptimizedQuery(): Result<Boolean> {
        Log.d(TAG, "testOptimizedQuery: Testing full optimized query with all filters")
        return try {
            val testQuery = itemsCollection
                .whereEqualTo("status", "ACTIVE")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()
            
            Log.d(TAG, "testOptimizedQuery: ✅ Full optimized query working! Retrieved ${testQuery.size()} items")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "testOptimizedQuery: ❌ Full optimized query failed", e)
            Result.failure(e)
        }
    }
}
