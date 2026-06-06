package com.airops.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airops.data.repository.MatchRepository
import com.airops.domain.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private var matchId: String = ""
    private var timerJob: kotlinx.coroutines.Job? = null

    fun connect(matchId: String) {
        this.matchId = matchId
        viewModelScope.launch {
            try {
                val state = matchRepository.getMatchState(matchId)
                _gameState.value = state
                _connected.value = true
                
                // Start timer if match is active
                if (state?.status == "active") {
                    startTimer()
                }
            } catch (e: Exception) {
                _connected.value = false
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val prev = _gameState.value ?: return@launch
                val newSeconds = prev.elapsedSeconds + 1L
                _gameState.value = prev.copy(
                    elapsedSeconds = newSeconds,
                    elapsedTime = formatTime(newSeconds)
                )
            }
        }
    }

    private fun formatTime(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    fun updateLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                matchRepository.updateLocation(matchId, lat, lng)
            } catch (_: Exception) {}
        }
    }

    fun reportKill(targetUserId: String) {
        viewModelScope.launch {
            try {
                matchRepository.reportKill(matchId, targetUserId)
            } catch (_: Exception) {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}