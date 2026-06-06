package com.airops.domain

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val role: String // "player" | "organizer"
)

data class UserStats(
    val totalMatches: Int,
    val wins: Int,
    val totalKills: Int,
    val totalDeaths: Int,
    val kdRatio: Double,
    val accuracy: Int,
    val mvpCount: Int,
    val objectivesCaptured: Int,
    val achievements: List<Achievement>
)

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val unlockedAt: String?
)