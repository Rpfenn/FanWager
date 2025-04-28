package edu.quinnipiac.ser210.finalproject.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.model.FanWagerViewModelFactory
import edu.quinnipiac.ser210.finalproject.model.GameDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val viewModel: FanWagerViewModel = viewModel(
        factory = FanWagerViewModelFactory(db)
    )
    val games by viewModel.games.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchDailyScoreboardLive()
    }

    val inProgressGames = games.filter { it.gameStatus == "In Progress" }
    val scheduledGames = games.filter { it.gameStatus == "Scheduled" }
    val completedGames = games.filter { it.gameStatus == "Completed" }

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
            if (inProgressGames.isNotEmpty()) {
                item { SectionHeader("In Progress") }
                items(inProgressGames) { game ->
                    GameCard(game = game) {
                        navController.navigate("place_bet/${game.gameId}")
                    }
                }
            }

            if (scheduledGames.isNotEmpty()) {
                item { SectionHeader("Scheduled") }
                items(scheduledGames) { game ->
                    GameCard(game = game) {
                        navController.navigate("place_bet/${game.gameId}")
                    }
                }
            }

            if (completedGames.isNotEmpty()) {
                item { SectionHeader("Completed") }
                items(completedGames) { game ->
                    GameCard(game = game) {
                        navController.navigate("place_bet/${game.gameId}")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}

@Composable
fun GameCard(game: GameDetails, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        val awayLogo = getLogoResId(game.away.lowercase())
        val homeLogo = getLogoResId(game.home.lowercase())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = awayLogo),
                contentDescription = "${game.away} logo",
                modifier = Modifier.size(48.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${game.away} @ ${game.home}",
                    style = MaterialTheme.typography.titleMedium
                )

                if (game.gameStatus == "In Progress" || game.gameStatus == "Completed") {
                    Text(
                        text = "Score: ${game.awayScore ?: "-"} - ${game.homeScore ?: "-"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "Game Time: ${game.gameTime}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Status: ${game.gameStatus}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Image(
                painter = painterResource(id = homeLogo),
                contentDescription = "${game.home} logo",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun getLogoResId(teamAbbreviation: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(
        teamAbbreviation,
        "drawable",
        context.packageName
    )
}
