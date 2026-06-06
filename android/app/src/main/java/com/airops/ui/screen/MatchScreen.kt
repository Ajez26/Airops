package com.airops.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airops.ui.theme.*
import com.airops.ui.viewmodel.MatchViewModel
import com.airops.domain.GameState

@Composable
fun MatchScreen(
    matchId: String,
    vm: MatchViewModel = hiltViewModel(),
    onMatchEnd: () -> Unit
) {
    val gameState by vm.gameState.collectAsState()
    val connected by vm.connected.collectAsState()

    LaunchedEffect(matchId) { vm.connect(matchId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HudBackground)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("MISSION: ${gameState?.matchName?.uppercase() ?: "UNKNOWN"}", color = HudGreen, fontSize = 16.sp)
                    Text("CODE: ${gameState?.matchCode}", color = HudAmber, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(if (connected) HudGreen else Color.Gray, shape = androidx.compose.foundation.shape.CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (connected) "LIVE" else "OFFLINE", color = HudText, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Timer / Status
            if (gameState?.status == "active") {
                Text(
                    "⏱ ${gameState?.elapsedTime ?: "00:00"}",
                    color = HudAmber,
                    fontSize = 36.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else if (gameState?.status == "lobby") {
                Text("WAITING FOR PLAYERS", color = HudText, fontSize = 14.sp)
                Text(gameState?.matchCode ?: "------", color = HudAmber, fontSize = 32.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, letterSpacing = 0.3.sp)
                Text("Share this code", color = HudText.copy(alpha = 0.5f), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Score
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ALPHA", color = TeamAlpha, fontSize = 12.sp)
                    Text("${gameState?.scoreAlpha ?: 0}", color = TeamAlpha, fontSize = 28.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BRAVO", color = TeamBravo, fontSize = 12.sp)
                    Text("${gameState?.scoreBravo ?: 0}", color = TeamBravo, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Player list
            HudDivider()
            Text("OPERATORS: ${gameState?.aliveCount ?: 0}/${gameState?.totalPlayers ?: 0}", color = HudText, fontSize = 10.sp)

            Spacer(modifier = Modifier.weight(1f))

            // Bottom HUD
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("HP: 100%", color = HudGreen, fontSize = 12.sp)
                Text("AMMO: ∞", color = HudGreen, fontSize = 12.sp)
                Text("TEAM: ${gameState?.teamName ?: "—"}", color = HudText, fontSize = 12.sp)
            }
        }
    }
}