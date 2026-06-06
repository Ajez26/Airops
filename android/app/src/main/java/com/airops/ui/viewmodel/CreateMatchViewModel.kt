package com.airops.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airops.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateMatchUiState(
    val loading: Boolean = false,
    val createdMatchId: String? = null,
    val error: String? = null
)

@HiltViewModel
class CreateMatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMatchUiState())
    val uiState: StateFlow<CreateMatchUiState> = _uiState.asStateFlow()

    fun createMatch(
        name: String,
        gameMode: String,
        maxPlayers: Int,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val matchId = matchRepository.createMatch(
                name = name,
                gameMode = gameMode,
                maxPlayers = maxPlayers,
                durationMinutes = durationMinutes
            )
            if (matchId != null) {
                _uiState.update { it.copy(loading = false, createdMatchId = matchId) }
            } else {
                _uiState.update {
                    it.copy(loading = false, error = "MISSION DEPLOYMENT FAILED. CHECK CONNECTION.")
                }
            }
        }
    }

    fun onBack() {
        // Navigation is handled by the NavHost — this is a no-op hook
        // that lets the UI call vm.onBack() for future analytics or cleanup.
    }
}
