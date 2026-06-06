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

data class JoinMatchUiState(
    val loading: Boolean = false,
    val joinedMatchId: String? = null,
    val error: String? = null
)

@HiltViewModel
class JoinMatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinMatchUiState())
    val uiState: StateFlow<JoinMatchUiState> = _uiState.asStateFlow()

    fun joinMatch(code: String) {
        if (code.length != 6) {
            _uiState.update { it.copy(error = "CODE MUST BE 6 CHARACTERS") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val matchId = matchRepository.joinMatch(code)
            if (matchId != null) {
                _uiState.update { it.copy(loading = false, joinedMatchId = matchId) }
            } else {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "MISSION NOT FOUND OR ALREADY STARTED"
                    )
                }
            }
        }
    }

    fun onBack() {
        // No-op hook for future use
    }
}
