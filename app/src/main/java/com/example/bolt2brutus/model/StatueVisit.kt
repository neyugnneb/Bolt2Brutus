package com.example.bolt2brutus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/** Firestore document: statueVisits/{visitId}. One document per visit; counts per statue live on [User.statueVisits]. */
data class StatueVisit(
    @DocumentId var id: String = "",
    var userId: String = "",
    var statueId: String = "",
    var statueName: String = "",
    @ServerTimestamp var visitedAt: Timestamp? = null,
)
