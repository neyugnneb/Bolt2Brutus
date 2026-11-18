package com.example.bolt2brutus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolt2brutus.data.repository.AuthRepository
import com.example.bolt2brutus.data.repository.UserRepository
import com.example.bolt2brutus.model.User
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object SignedIn : AuthUiState

    /** The account exists but its email is unconfirmed. [sent] is true when a link was just emailed. */
    data class VerifyEmail(val email: String, val sent: Boolean) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

/** Presenter for the login and sign-up screens. */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {

    private val _uiState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val uiState: LiveData<AuthUiState> = _uiState

    val isSignedIn: Boolean
        get() = authRepository.isSignedInAndVerified

    fun signUp(email: String, password: String, username: String) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _uiState.value = AuthUiState.Error("Enter a username, email, and password")
            return
        }
        runAuth {
            val uid = authRepository.signUp(email.trim(), password)
            userRepository.create(User(uid = uid, email = email.trim(), username = username.trim()))
            AuthUiState.VerifyEmail(email.trim(), sent = true)
        }
    }

    fun logIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Enter your email and password")
            return
        }
        runAuth {
            if (authRepository.logIn(email.trim(), password)) {
                AuthUiState.SignedIn
            } else {
                AuthUiState.VerifyEmail(email.trim(), sent = false)
            }
        }
    }

    fun resendVerification() {
        runAuth {
            authRepository.resendVerification()
            AuthUiState.VerifyEmail(authRepository.currentEmail.orEmpty(), sent = true)
        }
    }

    /** Call after the UI has shown an error or dialog so it is not shown again on rotation. */
    fun messageShown() {
        val state = _uiState.value
        if (state is AuthUiState.Error || state is AuthUiState.VerifyEmail) _uiState.value = AuthUiState.Idle
    }

    private fun runAuth(block: suspend () -> AuthUiState) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                block()
            } catch (e: Exception) {
                AuthUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
