package edu.quinnipiac.ser210.finalproject.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// ✨ Now includes SplashScreen in Screens enum
enum class Screens(val route: String) {
    SplashScreen("splash"), // <-- added this
    HomeScreen("home"),
    HistoryScreen("history"),
    LeaderBoardScreen("leaderboard"),
    SettingsScreen("settings")
}

// 👇 This stays the same! SplashScreen is NOT part of the bottom nav bar
data class NavItems(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String
)

val listOfNavItems = listOf(
    NavItems(
        title = "Home",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        route = Screens.HomeScreen.route
    ),
    NavItems(
        title = "LeaderBoard",
        unselectedIcon = Icons.Outlined.MailOutline,
        selectedIcon = Icons.Filled.Email,
        route = Screens.LeaderBoardScreen.route
    ),
    NavItems(
        title = "History",
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite,
        route = Screens.HistoryScreen.route
    ),
    NavItems(
        title = "Settings",
        unselectedIcon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        route = Screens.SettingsScreen.route
    )
)
