package com.airops.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airops.ui.theme.*
import com.airops.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    vm: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val user by vm.user.collectAsState()
    val stats by vm.stats.collectAsState()
    val logoutDone by vm.logoutDone.collectAsState()

    LaunchedEffect(logoutDone) {
        if (logoutDone) onBack()
    }

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
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "OPERATOR PROFILE",
                        color = HudGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "PERSONNEL RECORD",
                        color = HudText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar + name block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HudSurface)
                    .border(1.dp, HudBorder)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(HudGreen.copy(alpha = 0.15f))
                        .border(2.dp, HudGreen.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.displayName?.firstOrNull()?.uppercase() ?: "?",
                        color = HudGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = user?.displayName?.uppercase() ?: "UNKNOWN OPERATOR",
                        color = HudGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user?.email ?: "",
                        color = HudText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(HudGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACTIVE DUTY",
                            color = HudGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats
            Text(
                text = "COMBAT STATISTICS",
                color = HudText,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(
                    label = "MATCHES",
                    value = "${stats?.totalMatches ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "WINS",
                    value = "${stats?.wins ?: 0}",
                    color = HudAmber,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(
                    label = "KILLS",
                    value = "${stats?.totalKills ?: 0}",
                    color = HudRed,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "DEATHS",
                    value = "${stats?.totalDeaths ?: 0}",
                    color = HudRed,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "K/D",
                    value = stats?.kdRatio?.let { "%.2f".format(it) } ?: "—",
                    color = HudAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // Logout button
            HudButton(
                label = "⏻  LOGOUT",
                onClick = { vm.logout() },
                color = HudRed,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
