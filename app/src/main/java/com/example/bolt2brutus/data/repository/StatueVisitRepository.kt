package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.model.StatueRef
import com.example.bolt2brutus.model.StatueVisit
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** CRUD for statueVisits/{visitId}, keeping the per-statue counts on users/{uid} in step. */
class StatueVisitRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val visits = db.collection(FirestoreCollections.STATUE_VISITS)
    private val users = db.collection(FirestoreCollections.USERS)

    /** Records a visit and bumps the user's count for that statue in one atomic write. */
    suspend fun record(uid: String, statue: StatueRef) {
        db.runBatch { batch ->
            batch.set(
                visits.document(),
                StatueVisit(userId = uid, statueId = statue.statueId, statueName = statue.name),
            )
            batch.update(users.document(uid), "statueVisits.${statue.statueId}", FieldValue.increment(1))
        }.await()
    }

    suspend fun deleteAllForUser(uid: String) {
        val snapshot = visits.whereEqualTo("userId", uid).get().await()
        if (snapshot.isEmpty) return
        db.runBatch { batch ->
            snapshot.documents.forEach { batch.delete(it.reference) }
        }.await()
    }
}
