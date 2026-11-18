package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observe
import com.example.bolt2brutus.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/** CRUD for users/{uid} profile documents. */
class UserRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val users = db.collection(FirestoreCollections.USERS)

    suspend fun create(user: User) {
        users.document(user.uid).set(user.copy(usernameLower = user.username.lowercase())).await()
    }

    suspend fun get(uid: String): User? =
        users.document(uid).get().await().toObject(User::class.java)

    fun observe(uid: String): Flow<User?> = users.document(uid).observe()

    suspend fun updateUsername(uid: String, username: String) {
        users.document(uid).update(
            mapOf("username" to username, "usernameLower" to username.lowercase())
        ).await()
    }

    /** Users whose username starts with [prefix], ignoring case. */
    suspend fun searchByUsername(prefix: String, limit: Long = 20): List<User> {
        val query = prefix.trim().lowercase()
        if (query.isEmpty()) return emptyList()
        return users.orderBy("usernameLower")
            .startAt(query)
            .endAt(query + "")
            .limit(limit)
            .get().await()
            .toObjects(User::class.java)
    }

    /**
     * Brings the profile in line with the signed-in account: copies over an email changed
     * through Firebase Auth, and fills [User.usernameLower] on profiles created before it existed.
     */
    suspend fun syncWithAccount(uid: String, authEmail: String?) {
        val user = get(uid) ?: return
        val updates = buildMap {
            if (authEmail != null && authEmail != user.email) put("email", authEmail)
            if (user.usernameLower != user.username.lowercase()) put("usernameLower", user.username.lowercase())
        }
        if (updates.isNotEmpty()) users.document(uid).update(updates).await()
    }

    suspend fun delete(uid: String) {
        users.document(uid).delete().await()
    }
}
