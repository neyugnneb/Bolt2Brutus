package com.example.bolt2brutus.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/** Wraps Firebase Authentication (accounts and sessions). Profile data lives in [UserRepository]. */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    /** Creates the account, sends a confirmation email, and returns the new UID. */
    suspend fun signUp(email: String, password: String): String {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
            ?: error("Account creation returned no user")
        user.sendEmailVerification()
        return user.uid
    }

    suspend fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logOut() = auth.signOut()

    suspend fun updatePassword(newPassword: String) {
        requireNotNull(auth.currentUser) { "Not signed in" }.updatePassword(newPassword).await()
    }

    suspend fun deleteAccount() {
        requireNotNull(auth.currentUser) { "Not signed in" }.delete().await()
    }
}
