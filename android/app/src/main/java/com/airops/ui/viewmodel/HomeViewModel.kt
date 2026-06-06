package com.airops.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airops.data.repository.UserRepository
import com.airops.domain.User
import com.airops.domain.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Ready(val user: User?, val stats: UserStats?) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Kept for backward compatibility with HomeScreen
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _stats = MutableStateFlow<UserStats?>(null)
    val stats: StateFlow<UserStats?> = _stats.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val user = userRepository.getCurrentUser()
                val stats = userRepository.getMyStats()
                _user.value = user
                _stats.value = stats
                _uiState.value = HomeUiState.Ready(user, stats)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun refresh() = loadData()
}
