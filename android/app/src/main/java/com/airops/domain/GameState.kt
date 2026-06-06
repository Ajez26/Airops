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
    @SerializedName("domination") DOMINATION("Dominación"),
    @SerializedName("battle_royale") BATTLE_ROYALE("Battle Royale"),
    @SerializedName("custom") CUSTOM("Personalizado")
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
