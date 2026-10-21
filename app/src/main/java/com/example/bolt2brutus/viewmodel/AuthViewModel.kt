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
        get() = authRepository.currentUserId != null

    fun signUp(email: String, password: String, username: String) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _uiState.value = AuthUiState.Error("Enter a username, email, and password")
            return
        }
        runAuth {
            val uid = authRepository.signUp(email.trim(), password)
            userRepository.create(User(uid = uid, email = email.trim(), username = username.trim()))
        }
    }

    fun logIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Enter your email and password")
            return
        }
        runAuth { authRepository.logIn(email.trim(), password) }
    }

    /** Call after the UI has shown an error so it is not shown again on rotation. */
    fun errorShown() {
        if (_uiState.value is AuthUiState.Error) _uiState.value = AuthUiState.Idle
    }

    private fun runAuth(block: suspend () -> Unit) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                block()
                AuthUiState.SignedIn
            } catch (e: Exception) {
                AuthUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
