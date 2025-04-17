package edu.quinnipiac.ser210.finalproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.model.GameDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: FanWagerViewModel = viewModel()
    val games by viewModel.games.collectAsState()

    // Fetch games when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchGames()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FanWager - MLB Games") }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(games) { game ->
                GameCard(game = game) {
                    val gameId = "${game.home}_${game.away}"
                    navController.navigate("place_bet/$gameId")
                }
            }
        }
    }
}

@Composable
fun GameCard(game: GameDetails, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${game.away} @ ${game.home}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Game Time: ${game.gameTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
