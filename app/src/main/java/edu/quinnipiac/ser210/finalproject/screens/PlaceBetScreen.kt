package edu.quinnipiac.ser210.finalproject.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.model.GameDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceBetScreen(
    navController: NavController,
    gameId: String,
    viewModel: FanWagerViewModel = viewModel()
) {
    val games by viewModel.games.collectAsState()
    val game = games.find { "${it.home}_${it.away}" == gameId }

    // Trigger API call if needed
    LaunchedEffect(Unit) {
        if (games.isEmpty()) {
            viewModel.fetchGames()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Place Bet") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            games.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            game == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Game not found")
                }
            }

            else -> {
                PlaceBetForm(game = game, navController = navController, modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun PlaceBetForm(
    game: GameDetails,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTeam by remember { mutableStateOf("") }
    var wagerAmount by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "${game.away} @ ${game.home}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Game Time: ${game.gameTime}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Select Team to Win:")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { selectedTeam = game.away }) {
                Text(game.away)
            }
            Button(onClick = { selectedTeam = game.home }) {
                Text(game.home)
            }
        }

        Text("Enter Wager Amount:")
        TextField(
            value = wagerAmount,
            onValueChange = { wagerAmount = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            placeholder = { Text("e.g., 50") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // TODO: Save bet
                navController.popBackStack()
            },
            enabled = selectedTeam.isNotEmpty() && wagerAmount.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Place Bet on $selectedTeam")
        }
    }
}

