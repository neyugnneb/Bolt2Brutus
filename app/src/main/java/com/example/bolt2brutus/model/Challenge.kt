package com.example.bolt2brutus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/** Firestore document: challenges/{challengeId}. Usernames and race name are copied in for display. */
data class Challenge(
    @DocumentId var id: String = "",
    var challengerId: String = "",
    var challengerUsername: String = "",
    var challengedId: String = "",
    var challengedUsername: String = "",
    var raceId: String = "",
    var raceName: String = "",
    var status: String = RequestStatus.PENDING,
    @ServerTimestamp var createdAt: Timestamp? = null,
)
