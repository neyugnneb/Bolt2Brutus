package com.example.bolt2brutus.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bolt2brutus.data.repository.AuthRepository
import com.example.bolt2brutus.data.repository.RaceAttemptRepository
import com.example.bolt2brutus.data.repository.RaceRepository
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.model.RaceAttempt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** One runner's row: their best completed time on the selected race. */
data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val username: String,
    val bestMs: Long,
    val isMe: Boolean,
)

data class LeaderboardState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val myBestMs: Long? = null,
) {
    /** Null when the signed-in user is not on the board. */
    val myRank: Int? get() = entries.firstOrNull { it.isMe }?.rank
}

data class RunnerStats(val runs: Int, val bestMs: Long?, val averageMs: Long?)

data class TimeComparison(
    val raceName: String,
    val otherUsername: String,
    val me: RunnerStats,
    val other: RunnerStats,
)

/** Presenter for the public leaderboard and comparing times with another runner. */
@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModel(
    authRepository: AuthRepository = AuthRepository(),
    raceRepository: RaceRepository = RaceRepository(),
    private val raceAttemptRepository: RaceAttemptRepository = RaceAttemptRepository(),
) : ViewModel() {

    companion object {
        private const val tag = "LeaderboardViewModel"

        /** Runners shown on the board. */
        const val TOP_RUNNERS = 20

        /**
         * Attempts fetched per race. The board shows each runner's best only, so this is
         * larger than [TOP_RUNNERS] to leave room for runners with many attempts.
         */
        private const val FETCH_LIMIT = 100L
    }

    private val uid: String? = authRepository.currentUserId

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    val races: LiveData<List<Race>> = raceRepository.observeAll()
        .catch { _message.value = it.message }
        .asLiveData()

    private val selectedRace = MutableStateFlow<Race?>(null)
    val selectedRaceId: String? get() = selectedRace.value?.id

    val state: LiveData<LeaderboardState> = selectedRace.flatMapLatest { race ->
        if (race == null) {
            flowOf(LeaderboardState())
        } else {
            combine(fastestAttempts(race.id), myAttempts()) { fastest, mine ->
                LeaderboardState(
                    entries = rankBestPerRunner(fastest),
                    myBestMs = mine.filter { it.raceId == race.id && it.completed }
                        .mapNotNull { it.elapsedTimeMs }
                        .minOrNull(),
                )
            }
        }
    }.asLiveData()

    private val _comparison = MutableLiveData<TimeComparison?>()
    val comparison: LiveData<TimeComparison?> = _comparison

    fun selectRace(race: Race) {
        selectedRace.value = race
    }

    /** Compares the signed-in user's times on the selected race with [other]'s. */
    fun compareTimes(other: LeaderboardEntry) {
        val uid = uid ?: return
        val race = selectedRace.value ?: return
        if (other.isMe) {
            _message.value = "That's you. Tap another runner to compare times."
            return
        }
        viewModelScope.launch {
            try {
                val mine = raceAttemptRepository.getCompletedForUser(uid, race.id)
                val theirs = raceAttemptRepository.getCompletedForUser(other.userId, race.id)
                _comparison.value = TimeComparison(race.raceName, other.username, stats(mine), stats(theirs))
            } catch (e: Exception) {
                _message.value = e.message ?: "Something went wrong"
            }
        }
    }

    fun comparisonShown() {
        _comparison.value = null
    }

    fun messageShown() {
        _message.value = null
    }

    private fun fastestAttempts(raceId: String): Flow<List<RaceAttempt>> =
        raceAttemptRepository.observeLeaderboard(raceId, FETCH_LIMIT).catch {
            // A missing composite index fails here; the message contains a link that creates it.
            Log.w(tag, "Leaderboard query failed", it)
            _message.value = it.message
            emit(emptyList())
        }

    private fun myAttempts(): Flow<List<RaceAttempt>> =
        (uid?.let { raceAttemptRepository.observeForUser(it) } ?: flowOf(emptyList())).catch {
            _message.value = it.message
            emit(emptyList())
        }

    /** [fastest] is sorted by time, so each runner's first attempt is their best. */
    private fun rankBestPerRunner(fastest: List<RaceAttempt>): List<LeaderboardEntry> =
        fastest.filter { it.elapsedTimeMs != null }
            .distinctBy { it.userId }
            .take(TOP_RUNNERS)
            .mapIndexed { index, attempt ->
                LeaderboardEntry(
                    rank = index + 1,
                    userId = attempt.userId,
                    username = attempt.username,
                    bestMs = attempt.elapsedTimeMs!!,
                    isMe = attempt.userId == uid,
                )
            }

    private fun stats(attempts: List<RaceAttempt>): RunnerStats {
        val times = attempts.mapNotNull { it.elapsedTimeMs }
        return RunnerStats(
            runs = times.size,
            bestMs = times.minOrNull(),
            averageMs = times.takeIf { it.isNotEmpty() }?.average()?.toLong(),
        )
    }
}
