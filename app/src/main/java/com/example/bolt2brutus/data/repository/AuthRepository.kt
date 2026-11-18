package com.example.bolt2brutus.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/** Wraps Firebase Authentication (accounts and sessions). Profile data lives in [UserRepository]. */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentEmail: String?
        get() = auth.currentUser?.email

    /** True when signed in with a confirmed email. Reads the cached account; [logIn] refreshes it. */
    val isSignedInAndVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    /** Creates the account, sends a confirmation email, and returns the new UID. */
    suspend fun signUp(email: String, password: String): String {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
            ?: error("Account creation returned no user")
        user.sendEmailVerification().await()
        return user.uid
    }

    /**
     * Signs in and returns whether the email is confirmed. An unconfirmed user stays
     * signed in so [resendVerification] can reach them, but the app does not let them in.
     */
    suspend fun logIn(email: String, password: String): Boolean {
        val user = auth.signInWithEmailAndPassword(email, password).await().user
            ?: error("Sign-in returned no user")
        user.reload().await()
        return auth.currentUser?.isEmailVerified == true
    }

    suspend fun resendVerification() {
        requireNotNull(auth.currentUser) { "Not signed in" }.sendEmailVerification().await()
    }

    fun logOut() = auth.signOut()

    /** Sends a confirmation link to [newEmail]; the email changes once the link is opened. */
    suspend fun updateEmail(currentPassword: String, newEmail: String) {
        reauthenticate(currentPassword).verifyBeforeUpdateEmail(newEmail).await()
    }

    suspend fun updatePassword(currentPassword: String, newPassword: String) {
        reauthenticate(currentPassword).updatePassword(newPassword).await()
    }

    suspend fun deleteAccount() {
        requireNotNull(auth.currentUser) { "Not signed in" }.delete().await()
    }

    /** Firebase requires a recent sign-in before changing the email or password. */
    private suspend fun reauthenticate(password: String): FirebaseUser {
        val user = requireNotNull(auth.currentUser) { "Not signed in" }
        val email = requireNotNull(user.email) { "Account has no email" }
        user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
        return user
    }
}
