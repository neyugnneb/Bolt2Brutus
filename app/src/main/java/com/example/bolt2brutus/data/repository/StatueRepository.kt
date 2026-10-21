package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observeAll
import com.example.bolt2brutus.model.Statue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/** CRUD for statues/{statueId}. */
class StatueRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val statues = db.collection(FirestoreCollections.STATUES)

    fun observeAll(): Flow<List<Statue>> = statues.orderBy("name").observeAll()

    suspend fun get(statueId: String): Statue? =
        statues.document(statueId).get().await().toObject(Statue::class.java)

    /** Creates or replaces the statue. A blank [Statue.id] gets a generated ID. */
    suspend fun save(statue: Statue): String {
        val doc = if (statue.id.isBlank()) statues.document() else statues.document(statue.id)
        doc.set(statue).await()
        return doc.id
    }

    suspend fun delete(statueId: String) {
        statues.document(statueId).delete().await()
    }
}
