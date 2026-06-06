package com.airops.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.airops.ui.theme.AiropsTheme
import com.airops.ui.navigation.AiropsNavHost
import com.airops.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiropsTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                
                AiropsNavHost(
                    navController = navController,
                    authState = authState
                )
            }
        }
    }
}
