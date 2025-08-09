package com.institute.lostandfound.repository

import androidx.lifecycle.LiveData
import com.institute.lostandfound.data.database.ItemDao
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.ItemType

class ItemRepository(private val itemDao: ItemDao) {

    fun getAllItems(): LiveData<List<Item>> = itemDao.getAllItems()

    fun getItemsByType(type: ItemType): LiveData<List<Item>> = itemDao.getItemsByType(type)

    fun getItemsByCategory(category: String): LiveData<List<Item>> = itemDao.getItemsByCategory(category)

    fun getUnresolvedItems(): LiveData<List<Item>> = itemDao.getUnresolvedItems()

    fun searchItems(query: String): LiveData<List<Item>> = itemDao.searchItems(query)

    suspend fun getItemById(id: Long): Item? = itemDao.getItemById(id)

    suspend fun insertItem(item: Item): Long = itemDao.insertItem(item)

    suspend fun updateItem(item: Item) = itemDao.updateItem(item)

    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)

    suspend fun markAsResolved(id: Long) = itemDao.markAsResolved(id)
} 