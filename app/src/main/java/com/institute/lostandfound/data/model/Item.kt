package com.institute.lostandfound.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Item(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val type: ItemType = ItemType.LOST,
    val status: ItemStatus = ItemStatus.ACTIVE,
    val location: Location = Location(),
    val images: List<String> = emptyList(),
    val contactInfo: ContactInfo = ContactInfo(),
    val tags: List<String> = emptyList(),
    val reward: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp = Timestamp.now(),
    val dateReported: Timestamp = Timestamp.now(),
    val reporterName: String = "",
    val reporterEmail: String = "",
    val imageUri: String = "",
    val isResolved: Boolean = false
)

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val placeName: String = ""
)

data class ContactInfo(
    val phone: String = "",
    val email: String = "",
    val preferredContact: String = "email"
)

enum class ItemType {
    LOST, FOUND
}

enum class ItemStatus {
    ACTIVE, RESOLVED, EXPIRED
} 