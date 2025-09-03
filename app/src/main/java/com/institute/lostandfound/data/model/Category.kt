package com.institute.lostandfound.data.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val parentCategoryId: String? = null
)

object Categories {
    val ELECTRONICS = Category(
        name = "Electronics",
        icon = "ic_electronics",
        color = "#FF5722"
    )
    
    val BOOKS = Category(
        name = "Books & Stationery",
        icon = "ic_book",
        color = "#2196F3"
    )
    
    val CLOTHING = Category(
        name = "Clothing & Accessories",
        icon = "ic_clothing",
        color = "#4CAF50"
    )
    
    val PERSONAL_ITEMS = Category(
        name = "Personal Items",
        icon = "ic_personal",
        color = "#9C27B0"
    )
    
    val BAGS = Category(
        name = "Bags & Backpacks",
        icon = "ic_bag",
        color = "#FF9800"
    )
    
    val JEWELRY = Category(
        name = "Jewelry",
        icon = "ic_jewelry",
        color = "#E91E63"
    )
    
    val DOCUMENTS = Category(
        name = "Documents & Cards",
        icon = "ic_document",
        color = "#607D8B"
    )
    
    val KEYS = Category(
        name = "Keys",
        icon = "ic_key",
        color = "#795548"
    )
    
    val SPORTS = Category(
        name = "Sports Equipment",
        icon = "ic_sports",
        color = "#00BCD4"
    )
    
    val OTHER = Category(
        name = "Other",
        icon = "ic_other",
        color = "#9E9E9E"
    )
    
    fun getAllCategories(): List<Category> = listOf(
        ELECTRONICS, BOOKS, CLOTHING, PERSONAL_ITEMS, BAGS, 
        JEWELRY, DOCUMENTS, KEYS, SPORTS, OTHER
    )
}
