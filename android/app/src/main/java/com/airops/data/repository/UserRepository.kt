package com.airops.data.repository

import com.airops.data.remote.AiropsApiService
import com.airops.domain.User
import com.airops.domain.UserStats
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for user / profile data.
 */
@Singleton
class UserRepository @Inject constructor(
    private val api: AiropsApiService
) {

    /**
     * Fetch the currently authenticated user's profile.
     * Returns null on any error (network, 401, etc.).
     */
    suspend fun getCurrentUser(): User? {
        return try {
            val response = api.getMyProfile()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Fetch the current user's aggregate statistics.
     * Returns null on any error.
     */
    suspend fun getMyStats(): UserStats? {
        return try {
            val response = api.getMyStats()
            if (response.isSuccessful) {
                val body = response.body() ?: return null
                UserStats(
                    totalMatches = body.total_matches,
                    wins = body.wins,
                    losses = body.total_matches - body.wins,
                    totalKills = body.total_kills,
                    totalDeaths = body.total_deaths,
                    kdRatio = body.kd_ratio
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
