package com.airops.data.repository

import com.airops.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "Unknown",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString(),
            role = "player"
        )
    }

    suspend fun loginWithGoogle(): User {
        // Note: In production, use Google Sign-In button and get ID token from it
        // This is a placeholder for the actual implementation
        val currentUser = auth.currentUser ?: throw Exception("No user signed in")
        return User(
            id = currentUser.uid,
            displayName = currentUser.displayName ?: "Unknown",
            email = currentUser.email ?: "",
            avatarUrl = currentUser.photoUrl?.toString(),
            role = "player"
        )
    }

    suspend fun loginWithToken(idToken: String): User {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: throw Exception("Sign-in failed")
        return User(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "Unknown",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString(),
            role = "player"
        )
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun logout() {
        auth.signOut()
    }

    fun getIdToken(): String? {
        return auth.currentUser?.getIdToken(false)?.result?.token
    }
}