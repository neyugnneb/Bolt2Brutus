package com.example.bolt2brutus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore document: users/{uid}. The document ID is the Firebase Auth UID.
 *
 * [usernameLower] backs case-insensitive username search.
 * [statueVisits] (statueId -> number of visits) caches the counts from the statueVisits
 * collection so a profile needs only one read.
 */
data class User(
    @DocumentId var uid: String = "",
    var email: String = "",
    var username: String = "",
    var usernameLower: String = "",
    @ServerTimestamp var createdAt: Timestamp? = null,
    var statueVisits: Map<String, Long> = emptyMap(),
)
