package com.airops.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airops.ui.theme.*
import com.airops.ui.viewmodel.CreateMatchViewModel

@Composable
fun CreateMatchScreen(
    vm: CreateMatchViewModel = hiltViewModel(),
    onMatchCreated: (matchId: String) -> Unit
) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(uiState.createdMatchId) {
        uiState.createdMatchId?.let { onMatchCreated(it) }
    }

    var matchName by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(GameModeOption.TEAM_DEATHMATCH) }
    var maxPlayers by remember { mutableStateOf("20") }
    var durationMinutes by remember { mutableStateOf("30") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HudBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◀",
                    color = HudAmber,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable { vm.onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "CREATE MATCH",
                        color = HudGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "MISSION CONFIGURATION",
                        color = HudText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // Mission name
            HudLabel("MISSION NAME")
            Spacer(modifier = Modifier.height(4.dp))
            HudTextField(
                value = matchName,
                onValueChange = { matchName = it },
                placeholder = "OPERATION DESERT STORM"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Game mode selector
            HudLabel("GAME MODE")
            Spacer(modifier = Modifier.height(8.dp))
            GameModeOption.values().forEach { mode ->
                val isSelected = selectedMode == mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .border(
                            1.dp,
                            if (isSelected) HudGreen.copy(alpha = 0.8f) else HudBorder
                        )
                        .background(
                            if (isSelected) HudGreen.copy(alpha = 0.08f) else HudSurface
                        )
                        .clickable { selectedMode = mode }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSelected) "▶ " else "  ",
                        color = HudGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    Text(
                        text = mode.displayName,
                        color = if (isSelected) HudGreen else HudText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Max players + duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    HudLabel("MAX PLAYERS")
                    Spacer(modifier = Modifier.height(4.dp))
                    HudTextField(
                        value = maxPlayers,
                        onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) maxPlayers = it },
                        placeholder = "20",
                        keyboardType = KeyboardType.Number
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    HudLabel("DURATION (MIN)")
                    Spacer(modifier = Modifier.height(4.dp))
                    HudTextField(
                        value = durationMinutes,
                        onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) durationMinutes = it },
                        placeholder = "30",
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error message
            if (uiState.error != null) {
                Text(
                    text = "⚠ ${uiState.error}",
                    color = HudRed,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Deploy button
            HudButton(
                label = if (uiState.loading) "DEPLOYING..." else "⊕  DEPLOY MISSION",
                onClick = {
                    if (!uiState.loading) {
                        vm.createMatch(
                            name = matchName.ifBlank { "MISSION ALPHA" },
                            gameMode = selectedMode.apiValue,
                            maxPlayers = maxPlayers.toIntOrNull() ?: 20,
                            durationMinutes = durationMinutes.toIntOrNull() ?: 30
                        )
                    }
                },
                color = if (uiState.loading) HudText else HudGreen,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Supporting composables ───────────────────────────────────────────────────

@Composable
private fun HudLabel(text: String) {
    Text(
        text = text,
        color = HudText,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        letterSpacing = 0.2.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HudTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = HudText.copy(alpha = 0.4f),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = HudSurface,
            focusedTextColor = HudGreen,
            unfocusedTextColor = HudText,
            cursorColor = HudGreen,
            focusedIndicatorColor = HudGreen,
            unfocusedIndicatorColor = HudBorder
        ),
        textStyle = LocalTextStyle.current.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// ── Game mode options ────────────────────────────────────────────────────────

enum class GameModeOption(val displayName: String, val apiValue: String) {
    TEAM_DEATHMATCH("Team Deathmatch", "team_deathmatch"),
    DOMINATION("Domination", "domination"),
    FREE_FOR_ALL("Free for All", "free_for_all"),
    CAPTURE_THE_FLAG("Capture the Flag", "capture_the_flag"),
    VIP("VIP Escort", "vip"),
    ASSAULT("Assault", "assault")
}
