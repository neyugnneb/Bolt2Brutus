package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observeAll
import com.example.bolt2brutus.model.Challenge
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.model.User
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/** CRUD for challenges/{challengeId}. */
class ChallengeRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val challenges = db.collection(FirestoreCollections.CHALLENGES)

    suspend fun issue(challenger: User, opponent: User, race: Race): String {
        val doc = challenges.document()
        doc.set(
            Challenge(
                challengerId = challenger.uid,
                challengerUsername = challenger.username,
                challengedId = opponent.uid,
                challengedUsername = opponent.username,
                raceId = race.id,
                raceName = race.raceName,
            )
        ).await()
        return doc.id
    }

    /** Every challenge the user issued or received. */
    fun observeForUser(uid: String): Flow<List<Challenge>> =
        challenges.where(
            Filter.or(
                Filter.equalTo("challengerId", uid),
                Filter.equalTo("challengedId", uid),
            )
        ).observeAll()

    suspend fun updateStatus(challengeId: String, status: String) {
        challenges.document(challengeId).update("status", status).await()
    }

    suspend fun delete(challengeId: String) {
        challenges.document(challengeId).delete().await()
    }
}
