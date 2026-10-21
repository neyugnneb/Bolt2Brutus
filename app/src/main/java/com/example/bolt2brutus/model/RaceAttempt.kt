package com.example.bolt2brutus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Firestore document: raceAttempts/{attemptId}.
 *
 * [username] and [raceName] are copied in so the leaderboard can be rendered
 * from a single query without joining users or races.
 */
data class RaceAttempt(
    @DocumentId var id: String = "",
    var userId: String = "",
    var username: String = "",
    var raceId: String = "",
    var raceName: String = "",
    var startTime: Timestamp? = null,
    var endTime: Timestamp? = null,
    var elapsedTimeMs: Long? = null,
    var completed: Boolean = false,
)
