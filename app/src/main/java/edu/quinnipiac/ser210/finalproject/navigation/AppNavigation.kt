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
import edu.quinnipiac.ser210.finalproject.screens.HomeScreen
import edu.quinnipiac.ser210.finalproject.screens.PlaceBetScreen
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavHostController
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.screens.HistoryScreen
import edu.quinnipiac.ser210.finalproject.screens.LeaderBoardScreen
import edu.quinnipiac.ser210.finalproject.screens.SettingsScreen

object Routes {
    const val HOME = "home"
    // Can add more routes here like:
    // const val PLACE_BET = "place_bet"
}

//@OptIn(ExperimentalAnimationApi::class)
//@Composable
//fun FanWagerNavigation(navController: NavHostController) {
//
//    NavHost(
//        navController = navController,
//        startDestination = Screens.HomeScreen.route,
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Screens.entries.forEach { screen ->
//            composable(screen.route) {
//                when (screen) {
//                    Screens.HomeScreen -> HomeScreen(navController)
//                    Screens.HistoryScreen -> HistoryScreen()
//                    Screens.LeaderBoardScreen -> LeaderBoardScreen()
//                    Screens.SettingsScreen -> SettingsScreen()
//                }
//            }
//        }
////        composable(Routes.HOME) {
////            HomeScreen(navController = navController)
////        }
//        composable("place_bet/{gameId}") { backStackEntry ->
//            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
//            PlaceBetScreen(navController = navController, gameId = gameId)
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppBar(
//    currentScreen: String,
//    navController: NavController,
//    navigateUp: () -> Unit,
//    context: Context,
//    textToShare: String,
//    onHelpClick: () -> Unit,
//    onSettingsClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val canNavigateBack = navController.previousBackStackEntry != null
//    var menuExpanded by remember { mutableStateOf(false) }
//
//    TopAppBar(
//        title = { Text("FanWager - MLB") },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primary
//        ),
//        modifier = modifier,
//        navigationIcon = {
//            if (canNavigateBack) {
//                IconButton(onClick = navigateUp) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                }
//            }
//        },
//        actions = {
//            if (textToShare.isNotBlank()) {
//                IconButton(onClick = {
//                    val intent = Intent(Intent.ACTION_SEND).apply {
//                        type = "text/plain"
//                        putExtra(Intent.EXTRA_SUBJECT, "Check this out")
//                        putExtra(Intent.EXTRA_TEXT, textToShare)
//                    }
//                    context.startActivity(Intent.createChooser(intent, "Share via"))
//                }) {
//                    Icon(Icons.Default.Share, contentDescription = "Share")
//                }
//            }
//
//            IconButton(onClick = { menuExpanded = true }) {
//                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
//            }
//
//            DropdownMenu(
//                expanded = menuExpanded,
//                onDismissRequest = { menuExpanded = false }
//            ) {
//                DropdownMenuItem(
//                    text = { Text("Settings") },
//                    onClick = {
//                        menuExpanded = false
//                        onSettingsClick()
//                    }
//                )
//                DropdownMenuItem(
//                    text = { Text("Help") },
//                    onClick = {
//                        menuExpanded = false
//                        onHelpClick()
//                    }
//                )
//            }
//        }
//    )
//}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FanWagerNavigation(navController: NavHostController,
                       viewModel: FanWagerViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route,
        modifier = Modifier.fillMaxSize()
    ) {

        Screens.entries.forEach { screen ->
            composable(screen.route) {
                when (screen) {
                    Screens.HomeScreen -> HomeScreen(navController)
                    Screens.HistoryScreen -> HistoryScreen()
                    Screens.LeaderBoardScreen -> LeaderBoardScreen()
                    Screens.SettingsScreen -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
//        composable(Routes.HOME) {
//            HomeScreen(navController = navController)
//        }
        composable("place_bet/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            PlaceBetScreen(navController = navController, gameId = gameId)
        }
    }
}
