package com.institute.lostandfound.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.ItemType

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY dateReported DESC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE type = :type ORDER BY dateReported DESC")
    fun getItemsByType(type: ItemType): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE category = :category ORDER BY dateReported DESC")
    fun getItemsByCategory(category: String): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isResolved = 0 ORDER BY dateReported DESC")
    fun getUnresolvedItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY dateReported DESC")
    fun searchItems(query: String): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): Item?

    @Insert
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("UPDATE items SET isResolved = 1 WHERE id = :id")
    suspend fun markAsResolved(id: Long)
} 