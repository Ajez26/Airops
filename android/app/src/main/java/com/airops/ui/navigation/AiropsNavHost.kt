package com.airops.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import com.airops.domain.AuthState
import com.airops.ui.screen.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Lobby : Screen("lobby/{matchCode}") {
        fun create(code: String) = "lobby/$code"
    }
    object Match : Screen("match/{matchId}") {
        fun create(id: String) = "match/$id"
    }
    object Profile : Screen("profile")
    object CreateMatch : Screen("create_match")
    object JoinMatch : Screen("join_match")
}

@Composable
fun AiropsNavHost(
    navController: NavHostController,
    authState: AuthState
) {
    val startDest = if (authState is AuthState.Authenticated) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDest) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCreateMatch = { navController.navigate(Screen.CreateMatch.route) },
                onJoinMatch = { navController.navigate(Screen.JoinMatch.route) },
                onProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.CreateMatch.route) {
            CreateMatchScreen(onMatchCreated = { matchId ->
                navController.navigate(Screen.Match.create(matchId)) {
                    popUpTo(Screen.Home.route)
                }
            })
        }

        composable(Screen.JoinMatch.route) {
            JoinMatchScreen(onMatchJoined = { matchId ->
                navController.navigate(Screen.Match.create(matchId)) {
                    popUpTo(Screen.Home.route)
                }
            })
        }

        composable(
            Screen.Match.route,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            MatchScreen(
                matchId = backStackEntry.arguments?.getString("matchId") ?: "",
                onMatchEnd = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) } }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}
