package com.airops.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airops.data.repository.MatchRepository
import com.airops.domain.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Sealed UI state following the recommended unidirectional data flow pattern */
sealed interface MatchUiState {
    object Loading : MatchUiState
    data class Active(val gameState: GameState, val connected: Boolean = true) : MatchUiState
    data class Error(val message: String) : MatchUiState
    object Finished : MatchUiState
}

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Loading)
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    // Keep backward-compatible accessors for screens that reference them directly
    val gameState: StateFlow<GameState?> = MutableStateFlow<GameState?>(null)
    val connected: StateFlow<Boolean> = MutableStateFlow(false)

    private var currentMatchId: String = ""
    private var timerJob: Job? = null
    private var pollJob: Job? = null

    fun connect(matchId: String) {
        if (currentMatchId == matchId) return   // idempotent
        currentMatchId = matchId
        _uiState.value = MatchUiState.Loading
        loadMatchState()
    }

    private fun loadMatchState() {
        viewModelScope.launch {
            val state = matchRepository.getMatchState(currentMatchId)
            if (state == null) {
                _uiState.value = MatchUiState.Error("Failed to load match. Check connection.")
                return@launch
            }

            _uiState.value = MatchUiState.Active(state)
            (gameState as MutableStateFlow).value = state
            (connected as MutableStateFlow).value = true

            when (state.status) {
                "active" -> {
                    startTimer()
                    startPolling()
                }
                "finished" -> _uiState.value = MatchUiState.Finished
            }
        }
    }

    /** Live timer — increments every second while match is active */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = (_uiState.value as? MatchUiState.Active) ?: break
                val newSeconds = current.gameState.elapsedSeconds + 1L
                val updated = current.gameState.copy(
                    elapsedSeconds = newSeconds,
                    elapsedTime = formatTime(newSeconds)
                )
                _uiState.value = current.copy(gameState = updated)
                (gameState as MutableStateFlow).value = updated
            }
        }
    }

    /** Poll server every 5s for scoreboard / player-status changes */
    private fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                delay(5_000L)
                val fresh = matchRepository.getMatchState(currentMatchId) ?: continue
                val current = (_uiState.value as? MatchUiState.Active) ?: break

                // Preserve locally-tracked elapsed time
                val merged = fresh.copy(
                    elapsedSeconds = current.gameState.elapsedSeconds,
                    elapsedTime = current.gameState.elapsedTime
                )
                _uiState.value = current.copy(gameState = merged)
                (gameState as MutableStateFlow).value = merged

                if (fresh.status == "finished") {
                    _uiState.value = MatchUiState.Finished
                    break
                }
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        if (currentMatchId.isEmpty()) return
        viewModelScope.launch {
            matchRepository.updateLocation(currentMatchId, lat, lng)
        }
    }

    fun reportKill(targetUserId: String) {
        if (currentMatchId.isEmpty()) return
        viewModelScope.launch {
            matchRepository.reportKill(currentMatchId, targetUserId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        pollJob?.cancel()
    }

    companion object {
        private fun formatTime(seconds: Long): String {
            val m = seconds / 60
            val s = seconds % 60
            return "%02d:%02d".format(m, s)
        }
    }
}
