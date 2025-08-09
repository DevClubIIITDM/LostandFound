package com.institute.lostandfound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.institute.lostandfound.data.database.AppDatabase
import com.institute.lostandfound.data.model.Item
import com.institute.lostandfound.data.model.ItemType
import com.institute.lostandfound.repository.ItemRepository
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    
    init {
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
    }

    fun getAllItems(): LiveData<List<Item>> = repository.getAllItems()

    fun getItemsByType(type: ItemType): LiveData<List<Item>> = repository.getItemsByType(type)

    fun getItemsByCategory(category: String): LiveData<List<Item>> = repository.getItemsByCategory(category)

    fun getUnresolvedItems(): LiveData<List<Item>> = repository.getUnresolvedItems()

    fun searchItems(query: String): LiveData<List<Item>> = repository.searchItems(query)

    private val _selectedItem = MutableLiveData<Item?>()
    val selectedItem: LiveData<Item?> = _selectedItem

    fun selectItem(item: Item) {
        _selectedItem.value = item
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun markAsResolved(id: Long) {
        viewModelScope.launch {
            repository.markAsResolved(id)
        }
    }

    suspend fun getItemById(id: Long): Item? {
        return repository.getItemById(id)
    }
} 