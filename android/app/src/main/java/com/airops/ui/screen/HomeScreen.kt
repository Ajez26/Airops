package com.airops.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.airops.ui.theme.*
import com.airops.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onCreateMatch: () -> Unit,
    onJoinMatch: () -> Unit,
    onProfile: () -> Unit,
) {
    val stats by vm.stats.collectAsState()
    val user by vm.user.collectAsState()

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AIROPS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = HudGreen
                    )
                    Text(
                        text = "TACTICAL FIELD SYSTEM",
                        style = MaterialTheme.typography.labelSmall,
                        color = HudText
                    )
                }
                // Profile button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, HudBorder)
                        .background(HudSurface)
                        .clickable { onProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("◉", color = HudGreen, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(modifier = Modifier.weight(1f), label = "MATCHES", value = "${stats?.totalMatches ?: 0}")
                StatBox(modifier = Modifier.weight(1f), label = "WINS", value = "${stats?.wins ?: 0}", color = HudAmber)
                StatBox(modifier = Modifier.weight(1f), label = "K/D", value = stats?.kdRatio?.let { "%.2f".format(it) } ?: "—", color = HudAmber)
                StatBox(modifier = Modifier.weight(1f), label = "KILLS", value = "${stats?.totalKills ?: 0}", color = HudRed)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            HudButton(
                label = "⊕  CREATE MATCH",
                onClick = onCreateMatch,
                color = HudGreen,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            HudButton(
                label = "⟶  JOIN MATCH",
                onClick = onJoinMatch,
                color = HudAmber,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Operator info
            Text(
                text = "OPERATOR: ${user?.displayName?.uppercase() ?: "UNKNOWN"}",
                style = MaterialTheme.typography.bodySmall,
                color = HudText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(HudGreen, shape = androidx.compose.foundation.shape.CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SYS ONLINE", style = MaterialTheme.typography.labelSmall, color = HudText)
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color = HudGreen, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(HudSurface)
            .border(1.dp, HudBorder)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = HudText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }
    }
}

@Composable
fun HudButton(label: String, onClick: () -> Unit, color: Color = HudGreen, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.5f))
            .background(color.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 13.sp, letterSpacing = 0.1.sp)
    }
}

@Composable
fun HudDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(HudBorder)
    )
}
