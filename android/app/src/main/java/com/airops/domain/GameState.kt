package com.airops.domain

import com.google.gson.annotations.SerializedName

data class Match(
    val id: String,
    val code: String,
    val name: String,
    @SerializedName("game_mode") val gameMode: GameMode,
    val status: MatchStatus,
    @SerializedName("max_players") val maxPlayers: Int,
    val players: List<MatchPlayer> = emptyList(),
    val teams: List<Team> = emptyList(),
    val objectives: List<Objective> = emptyList()
)

enum class GameMode(val displayName: String) {
    @SerializedName("team_deathmatch") TEAM_DEATHMATCH("Team Deathmatch"),
    @SerializedName("domination") DOMINATION("Domination"),
    @SerializedName("free_for_all") FREE_FOR_ALL("Free for All"),
    @SerializedName("capture_the_flag") CAPTURE_THE_FLAG("Capture The Flag"),
    @SerializedName("vip") VIP("VIP"),
    @SerializedName("assault") ASSAULT("Assault")
}

enum class MatchStatus {
    @SerializedName("lobby") LOBBY,
    @SerializedName("active") ACTIVE,
    @SerializedName("paused") PAUSED,
    @SerializedName("finished") FINISHED
}

data class MatchPlayer(
    val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("team_id") val teamId: String?,
    val role: PlayerRole,
    val status: PlayerStatus,
    val kills: Int = 0,
    val deaths: Int = 0,
    val lat: Double? = null,
    val lng: Double? = null
)

enum class PlayerRole {
    @SerializedName("commander") COMMANDER,
    @SerializedName("soldier") SOLDIER
}

enum class PlayerStatus {
    @SerializedName("alive") ALIVE,
    @SerializedName("dead") DEAD,
    @SerializedName("spectating") SPECTATING
}

data class Team(
    val id: String,
    val name: String,
    val color: String
)

data class Objective(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    @SerializedName("controlled_by") val controlledBy: String?
)

data class LocationUpdate(
    val playerId: String,
    val teamId: String?,
    val lat: Double,
    val lng: Double,
    val timestamp: Long
)

data class ChatMessage(
    val playerId: String,
    val displayName: String,
    val message: String,
    val scope: String, // "team" | "all"
    val timestamp: Long
)

/**
 * GameState — real-time match snapshot pushed via WebSocket.
 * All fields referenced in MatchScreen.kt are present here.
 */
data class GameState(
    /** Human-readable match name, e.g. "Operation Desert Storm" */
    val matchName: String = "",

    /** 6-char join code, e.g. "AB3X7K" */
    val matchCode: String = "",

    /** One of: lobby | active | paused | finished */
    val status: String = "lobby",

    /** Alpha team score */
    val scoreAlpha: Int = 0,

    /** Bravo team score */
    val scoreBravo: Int = 0,

    /** Number of alive players currently in the match */
    val aliveCount: Int = 0,

    /** Total players in the match (alive + dead + spectating) */
    val totalPlayers: Int = 0,

    /** This player's team name, e.g. "Alpha" or "Bravo" */
    val teamName: String = "",

    /** Formatted elapsed time, e.g. "04:27" */
    val elapsedTime: String = "00:00",

    /** Elapsed seconds since match started (used for timer math) */
    val elapsedSeconds: Long = 0L,

    /** Full player list for the scoreboard */
    val players: List<MatchPlayer> = emptyList(),

    /** Objectives list for domination / CTF modes */
    val objectives: List<Objective> = emptyList()
)

data class User(
    val id: String,
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null
)

data class UserStats(
    val totalMatches: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val totalKills: Int = 0,
    val totalDeaths: Int = 0,
    val kdRatio: Double = 0.0
)
