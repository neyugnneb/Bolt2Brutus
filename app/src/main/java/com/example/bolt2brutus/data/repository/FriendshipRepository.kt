package com.example.bolt2brutus.data.repository

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.observeAll
import com.example.bolt2brutus.model.Friendship
import com.example.bolt2brutus.model.User
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/** CRUD for friendships/{friendshipId}. */
class FriendshipRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val friendships = db.collection(FirestoreCollections.FRIENDSHIPS)

    suspend fun sendRequest(from: User, to: User): String {
        val doc = friendships.document()
        doc.set(
            Friendship(
                requesterId = from.uid,
                requesterUsername = from.username,
                receiverId = to.uid,
                receiverUsername = to.username,
            )
        ).await()
        return doc.id
    }

    /** Every friendship the user sent or received. */
    fun observeForUser(uid: String): Flow<List<Friendship>> =
        friendships.where(
            Filter.or(
                Filter.equalTo("requesterId", uid),
                Filter.equalTo("receiverId", uid),
            )
        ).observeAll()

    suspend fun updateStatus(friendshipId: String, status: String) {
        friendships.document(friendshipId).update("status", status).await()
    }

    suspend fun delete(friendshipId: String) {
        friendships.document(friendshipId).delete().await()
    }
}
