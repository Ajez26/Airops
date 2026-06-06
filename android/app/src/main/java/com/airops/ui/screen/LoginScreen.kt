package com.airops.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airops.ui.theme.*
import com.airops.domain.AuthState
import com.airops.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val authState by vm.authState.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HudBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            Text("AIROPS", color = HudGreen, fontSize = 48.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontFamily = FontFamily.Monospace, letterSpacing = 0.3.sp)
            Text("TACTICAL FIELD SYSTEM v1.0", style = MaterialTheme.typography.labelSmall, color = HudText, letterSpacing = 0.5.sp)

            Spacer(modifier = Modifier.height(32.dp))
            HudDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // Login button
            if (error != null) {
                Text(error!!, color = HudRed, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            HudButton(
                label = if (loading) "AUTHENTICATING..." else "⟶  SIGN IN WITH GOOGLE",
                onClick = { vm.loginWithGoogle() },
                color = HudGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HudGreen.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("OPERATOR AUTHENTICATION REQUIRED", style = MaterialTheme.typography.labelSmall, color = HudText.copy(alpha = 0.5f))
        }
    }
}