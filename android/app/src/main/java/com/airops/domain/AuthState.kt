package com.airops.domain

/**
 * Authentication state for the app.
 * User, UserStats, Achievement data classes are defined in GameState.kt
 * to avoid duplicate class definitions in the same package.
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}