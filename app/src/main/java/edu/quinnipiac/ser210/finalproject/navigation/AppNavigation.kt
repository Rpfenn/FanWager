package edu.quinnipiac.ser210.finalproject.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.screens.*

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val PLACE_BET = "place_bet"
    const val LEADERBOARD = "leaderboard"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FanWagerNavigation(
    navController: NavHostController,
    viewModel: FanWagerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Screens.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(Screens.HistoryScreen.route) {
            HistoryScreen()
        }

        // ✅ Correctly passing the viewModel
        composable(Screens.LeaderBoardScreen.route) {
            LeaderBoardScreen(viewModel = viewModel)
        }

        composable(Screens.SettingsScreen.route) {
            SettingsScreen(viewModel = viewModel)
        }

        composable("place_bet/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            PlaceBetScreen(navController = navController, gameId = gameId, viewModel = viewModel)
        }
    }
}
