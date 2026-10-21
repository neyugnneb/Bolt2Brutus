package com.example.bolt2brutus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore document: users/{uid}. The document ID is the Firebase Auth UID.
 *
 * STATUE_VISIT from the relational model is denormalized into [statueVisits]
 * (statueId -> number of visits) so a profile needs only one read.
 */
data class User(
    @DocumentId var uid: String = "",
    var email: String = "",
    var username: String = "",
    @ServerTimestamp var createdAt: Timestamp? = null,
    var statueVisits: Map<String, Long> = emptyMap(),
)
