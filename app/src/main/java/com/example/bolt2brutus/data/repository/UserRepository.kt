package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observe
import com.example.bolt2brutus.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/** CRUD for users/{uid} profile documents. */
class UserRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val users = db.collection(FirestoreCollections.USERS)

    suspend fun create(user: User) {
        users.document(user.uid).set(user).await()
    }

    suspend fun get(uid: String): User? =
        users.document(uid).get().await().toObject(User::class.java)

    fun observe(uid: String): Flow<User?> = users.document(uid).observe()

    suspend fun updateUsername(uid: String, username: String) {
        users.document(uid).update("username", username).await()
    }

    suspend fun recordStatueVisit(uid: String, statueId: String) {
        users.document(uid).update("statueVisits.$statueId", FieldValue.increment(1)).await()
    }

    suspend fun delete(uid: String) {
        users.document(uid).delete().await()
    }
}
