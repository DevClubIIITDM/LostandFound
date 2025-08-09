package com.institute.lostandfound.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val type: ItemType, // LOST or FOUND
    val location: String,
    val dateReported: Date,
    val contactInfo: String,
    val imageUri: String? = null,
    val isResolved: Boolean = false,
    val reporterName: String,
    val reporterEmail: String
)

enum class ItemType {
    LOST, FOUND
}

enum class Category(val displayName: String) {
    ELECTRONICS("Electronics"),
    BOOKS("Books & Stationery"),
    CLOTHING("Clothing & Accessories"),
    PERSONAL_ITEMS("Personal Items"),
    BAGS("Bags & Backpacks"),
    JEWELRY("Jewelry"),
    DOCUMENTS("Documents & Cards"),
    KEYS("Keys"),
    SPORTS("Sports Equipment"),
    OTHER("Other")
} 