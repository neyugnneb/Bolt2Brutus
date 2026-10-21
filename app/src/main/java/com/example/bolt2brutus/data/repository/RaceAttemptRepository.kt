package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observeAll
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.model.RaceAttempt
import com.example.bolt2brutus.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/** CRUD for raceAttempts/{attemptId}. The leaderboard is a query over completed attempts. */
class RaceAttemptRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val attempts = db.collection(FirestoreCollections.RACE_ATTEMPTS)

    /** Starts the timer for [user] on [race] and returns the new attempt ID. */
    suspend fun start(user: User, race: Race): String {
        val doc = attempts.document()
        doc.set(
            RaceAttempt(
                userId = user.uid,
                username = user.username,
                raceId = race.id,
                raceName = race.raceName,
                startTime = Timestamp.now(),
            )
        ).await()
        return doc.id
    }

    /** Stops the timer and records the elapsed time. */
    suspend fun complete(attempt: RaceAttempt) {
        val start = requireNotNull(attempt.startTime) { "Attempt has no start time" }
        val end = Timestamp.now()
        val elapsedMs = end.toDate().time - start.toDate().time
        attempts.document(attempt.id).update(
            mapOf(
                "endTime" to end,
                "elapsedTimeMs" to elapsedMs,
                "completed" to true,
            )
        ).await()
    }

    /** All of a user's attempts, newest first. Sorted on the device to avoid a composite index. */
    fun observeForUser(uid: String): Flow<List<RaceAttempt>> =
        attempts.whereEqualTo("userId", uid)
            .observeAll<RaceAttempt>()
            .map { list -> list.sortedByDescending { it.startTime } }

    /**
     * Fastest completed attempts for a race.
     * Needs a composite index on (raceId, completed, elapsedTimeMs); see firestore.indexes.json.
     */
    fun observeLeaderboard(raceId: String, limit: Long = 20): Flow<List<RaceAttempt>> =
        attempts.whereEqualTo("raceId", raceId)
            .whereEqualTo("completed", true)
            .orderBy("elapsedTimeMs")
            .limit(limit)
            .observeAll()

    suspend fun delete(attemptId: String) {
        attempts.document(attemptId).delete().await()
    }

    /** Keeps the denormalized username in sync after a user renames themselves. */
    suspend fun updateUsernameForUser(uid: String, username: String) {
        val snapshot = attempts.whereEqualTo("userId", uid).get().await()
        if (snapshot.isEmpty) return
        db.runBatch { batch ->
            snapshot.documents.forEach { batch.update(it.reference, "username", username) }
        }.await()
    }

    suspend fun deleteAllForUser(uid: String) {
        val snapshot = attempts.whereEqualTo("userId", uid).get().await()
        if (snapshot.isEmpty) return
        db.runBatch { batch ->
            snapshot.documents.forEach { batch.delete(it.reference) }
        }.await()
    }
}
