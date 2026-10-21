package com.example.bolt2brutus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bolt2brutus.data.repository.AuthRepository
import com.example.bolt2brutus.data.repository.RaceAttemptRepository
import com.example.bolt2brutus.data.repository.RaceRepository
import com.example.bolt2brutus.data.repository.UserRepository
import com.example.bolt2brutus.data.seed.DataSeeder
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.model.RaceAttempt
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/** Presenter for choosing a race and creating, finishing, and deleting race attempts. */
class RaceViewModel(
    authRepository: AuthRepository = AuthRepository(),
    raceRepository: RaceRepository = RaceRepository(),
    private val raceAttemptRepository: RaceAttemptRepository = RaceAttemptRepository(),
    private val userRepository: UserRepository = UserRepository(),
    dataSeeder: DataSeeder = DataSeeder(),
) : ViewModel() {

    private val uid: String? = authRepository.currentUserId

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    val races: LiveData<List<Race>> = raceRepository.observeAll()
        .catch { _message.value = it.message }
        .asLiveData()

    val myAttempts: LiveData<List<RaceAttempt>> =
        (uid?.let { raceAttemptRepository.observeForUser(it) } ?: emptyFlow())
            .catch { _message.value = it.message }
            .asLiveData()

    init {
        viewModelScope.launch {
            try {
                dataSeeder.seedIfEmpty()
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    fun startRace(race: Race) {
        val uid = uid ?: return
        launchCatching {
            val user = userRepository.get(uid) ?: error("Profile not found")
            raceAttemptRepository.start(user, race)
            _message.value = "Started ${race.raceName}"
        }
    }

    // TODO: Replace with GPS arrival detection at the destination statue.
    fun finishRace(attempt: RaceAttempt) {
        launchCatching { raceAttemptRepository.complete(attempt) }
    }

    fun deleteAttempt(attempt: RaceAttempt) {
        launchCatching { raceAttemptRepository.delete(attempt.id) }
    }

    fun messageShown() {
        _message.value = null
    }

    private fun launchCatching(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                _message.value = e.message ?: "Something went wrong"
            }
        }
    }
}
