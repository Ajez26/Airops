package com.airops.data.remote

import com.airops.domain.Match
import com.airops.domain.User
import com.airops.domain.UserStats
import retrofit2.Response
import retrofit2.http.*

// ── Request bodies ──────────────────────────────────────────────────────────

data class CreateMatchRequest(
    val name: String,
    val game_mode: String,
    val max_players: Int,
    val duration_minutes: Int = 30
)

data class JoinMatchRequest(val code: String)

data class LocationUpdateRequest(val lat: Double, val lng: Double)

data class ReportKillRequest(val target_user_id: String)

data class FirebaseLoginRequest(val firebase_token: String? = null)

// ── Response wrappers ────────────────────────────────────────────────────────

data class JoinMatchResponse(
    val match: Match,
    val team_id: String?
)

data class CreateMatchResponse(
    val id: String,
    val code: String,
    val name: String,
    val game_mode: String,
    val status: String,
    val max_players: Int
)

data class MessageResponse(val ok: Boolean, val message: String? = null)

data class LeaderboardEntry(
    val id: String,
    val display_name: String,
    val avatar_url: String?,
    val total_kills: Int,
    val total_deaths: Int,
    val wins: Int,
    val total_matches: Int,
    val kd_ratio: Double
)

data class StatsResponse(
    val id: String,
    val display_name: String,
    val total_kills: Int = 0,
    val total_deaths: Int = 0,
    val wins: Int = 0,
    val total_matches: Int = 0,
    val kd_ratio: Double = 0.0,
    val achievements: List<Achievement> = emptyList(),
    val recent_matches: List<RecentMatch> = emptyList()
)

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String
)

data class RecentMatch(
    val name: String,
    val game_mode: String,
    val finished_at: String?,
    val kills: Int,
    val deaths: Int,
    val status: String
)

// ── Retrofit interface ───────────────────────────────────────────────────────

interface AiropsApiService {

    // Auth
    @POST("auth/login")
    suspend fun loginWithFirebase(
        @Header("Authorization") bearerToken: String
    ): Response<User>

    // Matches
    @POST("matches")
    suspend fun createMatch(
        @Body request: CreateMatchRequest
    ): Response<CreateMatchResponse>

    @POST("matches/join")
    suspend fun joinMatch(
        @Body request: JoinMatchRequest
    ): Response<JoinMatchResponse>

    @GET("matches/{id}")
    suspend fun getMatch(
        @Path("id") matchId: String
    ): Response<Match>

    @POST("matches/{id}/start")
    suspend fun startMatch(
        @Path("id") matchId: String
    ): Response<Match>

    @POST("matches/{id}/end")
    suspend fun endMatch(
        @Path("id") matchId: String
    ): Response<Match>

    @GET("matches/{id}/players")
    suspend fun getMatchPlayers(
        @Path("id") matchId: String
    ): Response<List<com.airops.domain.MatchPlayer>>

    @PATCH("matches/{id}/player")
    suspend fun updateLocation(
        @Path("id") matchId: String,
        @Body request: LocationUpdateRequest
    ): Response<MessageResponse>

    @POST("matches/{id}/kill")
    suspend fun reportKill(
        @Path("id") matchId: String,
        @Body request: ReportKillRequest
    ): Response<MessageResponse>

    // Players / Profile
    @GET("players/me")
    suspend fun getMyProfile(): Response<User>

    @GET("players/{id}")
    suspend fun getPlayer(
        @Path("id") userId: String
    ): Response<User>

    // Stats
    @GET("stats/me")
    suspend fun getMyStats(): Response<StatsResponse>

    @GET("stats/leaderboard")
    suspend fun getLeaderboard(): Response<List<LeaderboardEntry>>

    @GET("stats/match/{id}")
    suspend fun getMatchStats(
        @Path("id") matchId: String
    ): Response<StatsResponse>
}
