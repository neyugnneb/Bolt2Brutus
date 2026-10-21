package com.example.bolt2brutus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/** Firestore document: friendships/{friendshipId}. Usernames are copied in for display. */
data class Friendship(
    @DocumentId var id: String = "",
    var requesterId: String = "",
    var requesterUsername: String = "",
    var receiverId: String = "",
    var receiverUsername: String = "",
    var status: String = RequestStatus.PENDING,
    @ServerTimestamp var createdAt: Timestamp? = null,
)
