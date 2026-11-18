package com.example.bolt2brutus.data.seed

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.repository.RaceRepository
import com.example.bolt2brutus.data.repository.StatueRepository
import com.example.bolt2brutus.model.Race
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Writes [SEED_STATUES] and [SEED_RACES] to Firestore whenever the database holds an older
 * [SEED_VERSION], and removes statues and races that are no longer in the seed data.
 * Bump [SEED_VERSION] after editing the seed data so every database picks up the change.
 */
class DataSeeder(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val statueRepository: StatueRepository = StatueRepository(),
    private val raceRepository: RaceRepository = RaceRepository(),
) {
    private val seedInfo = db.collection(FirestoreCollections.META).document("seed")

    suspend fun seedIfOutdated() {
        val version = seedInfo.get().await().getLong("version") ?: 0
        if (version >= SEED_VERSION) return

        val statuesById = SEED_STATUES.associateBy { it.id }
        val races = SEED_RACES.map { (startId, endId) ->
            val start = requireNotNull(statuesById[startId]) { "Unknown seed statue $startId" }
            val end = requireNotNull(statuesById[endId]) { "Unknown seed statue $endId" }
            Race(
                id = "${start.id}_to_${end.id}",
                raceName = "${start.siteName} to ${end.siteName}",
                description = "Race from ${start.name} at ${start.siteName} to ${end.name} at ${end.siteName}.",
                startStatue = start.toRef(),
                endStatue = end.toRef(),
            )
        }

        SEED_STATUES.forEach { statueRepository.save(it) }
        races.forEach { raceRepository.save(it) }

        val raceIds = races.map { it.id }.toSet()
        raceRepository.getAll().filter { it.id !in raceIds }.forEach { raceRepository.delete(it.id) }
        statueRepository.getAll().filter { it.id !in statuesById }.forEach { statueRepository.delete(it.id) }

        seedInfo.set(mapOf("version" to SEED_VERSION)).await()
    }

    private companion object {
        const val SEED_VERSION = 2L
    }
}
