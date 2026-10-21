package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observeAll
import com.example.bolt2brutus.model.Race
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/** CRUD for races/{raceId}. */
class RaceRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val races = db.collection(FirestoreCollections.RACES)

    fun observeAll(): Flow<List<Race>> = races.orderBy("raceName").observeAll()

    suspend fun get(raceId: String): Race? =
        races.document(raceId).get().await().toObject(Race::class.java)

    suspend fun isEmpty(): Boolean = races.limit(1).get().await().isEmpty

    /** Creates or replaces the race. A blank [Race.id] gets a generated ID. */
    suspend fun save(race: Race): String {
        val doc = if (race.id.isBlank()) races.document() else races.document(race.id)
        doc.set(race).await()
        return doc.id
    }

    suspend fun delete(raceId: String) {
        races.document(raceId).delete().await()
    }
}
