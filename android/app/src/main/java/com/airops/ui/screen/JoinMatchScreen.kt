package com.airops.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airops.ui.theme.*
import com.airops.ui.viewmodel.JoinMatchViewModel

@Composable
fun JoinMatchScreen(
    vm: JoinMatchViewModel = hiltViewModel(),
    onMatchJoined: (matchId: String) -> Unit
) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(uiState.joinedMatchId) {
        uiState.joinedMatchId?.let { onMatchJoined(it) }
    }

    var code by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HudBackground)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

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
                        text = "JOIN MATCH",
                        color = HudGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "ENTER MISSION CODE",
                        color = HudText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(48.dp))

            // Mission code display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HudAmber.copy(alpha = 0.4f))
                    .background(HudSurface)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (code.isEmpty()) "------" else code.padEnd(6, '-'),
                    color = HudAmber,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    letterSpacing = 0.4.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ASK YOUR COMMANDER FOR THE MISSION CODE",
                color = HudText.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Hidden text field (auto-uppercase, max 6 chars)
            @OptIn(ExperimentalMaterial3Api::class)
            TextField(
                value = code,
                onValueChange = { raw ->
                    val cleaned = raw.uppercase().filter { it.isLetterOrDigit() }
                    if (cleaned.length <= 6) code = cleaned
                },
                label = {
                    Text(
                        "MISSION CODE",
                        color = HudText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboard?.hide()
                        if (code.length == 6 && !uiState.loading) {
                            vm.joinMatch(code)
                        }
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = HudSurface,
                    focusedTextColor = HudAmber,
                    unfocusedTextColor = HudText,
                    cursorColor = HudAmber,
                    focusedIndicatorColor = HudAmber,
                    unfocusedIndicatorColor = HudBorder,
                    focusedLabelColor = HudAmber,
                    unfocusedLabelColor = HudText
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Error
            if (uiState.error != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HudRed.copy(alpha = 0.08f))
                        .border(1.dp, HudRed.copy(alpha = 0.3f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "⚠ ${uiState.error}",
                        color = HudRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Join button
            HudButton(
                label = when {
                    uiState.loading -> "CONNECTING..."
                    code.length < 6 -> "ENTER 6-CHAR CODE"
                    else -> "⟶  JOIN MISSION"
                },
                onClick = {
                    keyboard?.hide()
                    if (code.length == 6 && !uiState.loading) {
                        vm.joinMatch(code)
                    }
                },
                color = when {
                    code.length < 6 -> HudText
                    uiState.loading -> HudAmber.copy(alpha = 0.5f)
                    else -> HudAmber
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
