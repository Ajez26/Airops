package com.airops.data.repository

import com.airops.data.remote.AiropsApiService
import com.airops.data.remote.CreateMatchRequest
import com.airops.data.remote.JoinMatchRequest
import com.airops.data.remote.LocationUpdateRequest
import com.airops.data.remote.ReportKillRequest
import com.airops.domain.GameState
import com.airops.domain.Match
import com.airops.domain.MatchPlayer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for match-related remote data.
 *
 * All methods are suspend functions designed to be called from a ViewModel
 * scope (viewModelScope + Dispatchers.IO).  No LiveData is exposed here —
 * the ViewModel decides how to map results to UI state.
 */
@Singleton
class MatchRepository @Inject constructor(
    private val api: AiropsApiService
) {

    /**
     * Fetch the current match state and map it to a [GameState] snapshot.
     * Returns null on any network/HTTP error.
     */
    suspend fun getMatchState(matchId: String): GameState? {
        return try {
            val response = api.getMatch(matchId)
            if (response.isSuccessful) {
                response.body()?.toGameState()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Send a GPS location update for the current player.
     * Returns true on success.
     */
    suspend fun updateLocation(matchId: String, lat: Double, lng: Double): Boolean {
        return try {
            val response = api.updateLocation(matchId, LocationUpdateRequest(lat, lng))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Report a kill (current player eliminated [targetUserId]).
     * Returns true on success.
     */
    suspend fun reportKill(matchId: String, targetUserId: String): Boolean {
        return try {
            val response = api.reportKill(matchId, ReportKillRequest(targetUserId))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create a new match. Returns the new match ID on success, null on failure.
     */
    suspend fun createMatch(
        name: String,
        gameMode: String,
        maxPlayers: Int,
        durationMinutes: Int = 30
    ): String? {
        return try {
            val response = api.createMatch(
                CreateMatchRequest(
                    name = name,
                    game_mode = gameMode,
                    max_players = maxPlayers,
                    duration_minutes = durationMinutes
                )
            )
            if (response.isSuccessful) response.body()?.id else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Join an existing match using its 6-char code.
     * Returns the match ID on success, null on failure.
     */
    suspend fun joinMatch(code: String): String? {
        return try {
            val response = api.joinMatch(JoinMatchRequest(code.uppercase().trim()))
            if (response.isSuccessful) response.body()?.match?.id else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get the full player list for a match.
     */
    suspend fun getMatchPlayers(matchId: String): List<MatchPlayer> {
        return try {
            val response = api.getMatchPlayers(matchId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// ── Extension: Match → GameState ─────────────────────────────────────────────

private fun Match.toGameState(): GameState {
    val alphaTeam = teams.firstOrNull { it.name.equals("Alpha", ignoreCase = true) }
    val bravoTeam = teams.firstOrNull { it.name.equals("Bravo", ignoreCase = true) }

    val alphaPlayers = players.filter { it.teamId == alphaTeam?.id }
    val bravoPlayers = players.filter { it.teamId == bravoTeam?.id }

    val scoreAlpha = alphaPlayers.sumOf { it.kills }
    val scoreBravo = bravoPlayers.sumOf { it.kills }

    val alivePlayers = players.count {
        it.status == com.airops.domain.PlayerStatus.ALIVE
    }

    return GameState(
        matchName = name,
        matchCode = code,
        status = status.name.lowercase(),
        scoreAlpha = scoreAlpha,
        scoreBravo = scoreBravo,
        aliveCount = alivePlayers,
        totalPlayers = players.size,
        teamName = alphaTeam?.name ?: "",
        elapsedTime = "00:00",
        elapsedSeconds = 0L,
        players = players,
        objectives = objectives
    )
}
