package edu.quinnipiac.ser210.finalproject.navigation

import java.lang.IllegalArgumentException



enum class AppScreens {
    HomeScreen,
    PlaceBetScreen,
    LeaderboardScreen,
    HistoryScreen,
    SettingsScreen;
    companion object {
        fun fromRoute (route: String?): AppScreens
                = when(route?.substringBefore("/"))
        {
            HomeScreen.name -> HomeScreen
            PlaceBetScreen.name -> PlaceBetScreen
            LeaderboardScreen.name -> LeaderboardScreen
            HistoryScreen.name -> HistoryScreen
            //SettingsScreen.name -> SettingsScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }

    }

}




