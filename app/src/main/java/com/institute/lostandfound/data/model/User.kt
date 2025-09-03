package com.institute.lostandfound.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val lastActive: Timestamp = Timestamp.now()
)
