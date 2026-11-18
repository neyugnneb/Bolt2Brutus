package com.example.bolt2brutus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bolt2brutus.data.repository.AuthRepository
import com.example.bolt2brutus.data.repository.RaceAttemptRepository
import com.example.bolt2brutus.data.repository.StatueVisitRepository
import com.example.bolt2brutus.data.repository.UserRepository
import com.example.bolt2brutus.data.seed.TestLeaderboardData
import com.example.bolt2brutus.model.User
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/** Presenter for the signed-in user's profile: read, change details, log out, delete account. */
class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val raceAttemptRepository: RaceAttemptRepository = RaceAttemptRepository(),
    private val statueVisitRepository: StatueVisitRepository = StatueVisitRepository(),
) : ViewModel() {

    private val uid: String? = authRepository.currentUserId

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    val user: LiveData<User?> =
        (uid?.let { userRepository.observe(it) } ?: emptyFlow())
            .catch { _message.value = it.message }
            .asLiveData()

    private val _signedOut = MutableLiveData(uid == null)
    val signedOut: LiveData<Boolean> = _signedOut

    init {
        if (uid != null) {
            viewModelScope.launch {
                try {
                    userRepository.syncWithAccount(uid, authRepository.currentEmail)
                } catch (e: Exception) {
                    _message.value = e.message
                }
            }
        }
    }

    fun updateUsername(username: String) {
        val uid = uid ?: return
        if (username.isBlank()) {
            _message.value = "Username can't be empty"
            return
        }
        launchWithMessage("Username updated") {
            userRepository.updateUsername(uid, username.trim())
            raceAttemptRepository.updateUsernameForUser(uid, username.trim())
        }
    }

    fun updateEmail(currentPassword: String, newEmail: String) {
        if (currentPassword.isBlank() || newEmail.isBlank()) {
            _message.value = "Enter your new email and current password"
            return
        }
        launchWithMessage("Check ${newEmail.trim()} for a confirmation link, then log in with your new email") {
            authRepository.updateEmail(currentPassword, newEmail.trim())
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        if (currentPassword.isBlank() || newPassword.isBlank()) {
            _message.value = "Enter your new and current passwords"
            return
        }
        launchWithMessage("Password updated") {
            authRepository.updatePassword(currentPassword, newPassword)
        }
    }

    /** Debug builds only: fills the leaderboard with fake runners. */
    fun addTestLeaderboardData() {
        _message.value = "Adding test data…"
        viewModelScope.launch {
            _message.value = try {
                val me = uid?.let { userRepository.get(it) }
                val count = TestLeaderboardData().add(me)
                "Added $count test race times"
            } catch (e: Exception) {
                e.message ?: "Something went wrong"
            }
        }
    }

    fun removeTestLeaderboardData() {
        _message.value = "Removing test data…"
        launchWithMessage("Test data removed") {
            TestLeaderboardData().remove(uid?.let { userRepository.get(it) })
        }
    }

    fun logOut() {
        authRepository.logOut()
        _signedOut.value = true
    }

    fun deleteAccount() {
        val uid = uid ?: return
        launchWithMessage("Account deleted") {
            raceAttemptRepository.deleteAllForUser(uid)
            statueVisitRepository.deleteAllForUser(uid)
            userRepository.delete(uid)
            authRepository.deleteAccount()
            _signedOut.value = true
        }
    }

    fun messageShown() {
        _message.value = null
    }

    private fun launchWithMessage(success: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            _message.value = try {
                block()
                success
            } catch (e: Exception) {
                e.message ?: "Something went wrong"
            }
        }
    }
}
