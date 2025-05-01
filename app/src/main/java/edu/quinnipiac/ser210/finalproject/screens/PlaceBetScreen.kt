package edu.quinnipiac.ser210.finalproject.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.data.GlobalVariables
import edu.quinnipiac.ser210.finalproject.data.Prediction
import edu.quinnipiac.ser210.finalproject.model.FanWagerViewModelFactory
import edu.quinnipiac.ser210.finalproject.model.GameDetails
import edu.quinnipiac.ser210.finalproject.model.GameOdds
import edu.quinnipiac.ser210.finalproject.model.SportsBookOdds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceBetScreen(
    navController: NavController,
    gameId: String,
    viewModel: FanWagerViewModel
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    val games by viewModel.games.collectAsState()
    val odds by viewModel.odds.collectAsState()
    val game = games.find { it.gameId == gameId }

    LaunchedEffect(Unit) {
        if (games.isEmpty()) {
            viewModel.fetchGames()
        }
    }

    LaunchedEffect(game) {
        if (game != null) {
            viewModel.fetchOdds(gameId = game.gameId, gameDate = game.gameDate)
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
                PlaceBetForm(
                    game = game,
                    odds = odds,
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun PlaceBetForm(
    game: GameDetails,
    odds: GameOdds?,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FanWagerViewModel
) {
    var selectedTeam by remember { mutableStateOf("") }
    var wagerAmount by remember { mutableStateOf("") }
    var selectedOdds by remember { mutableStateOf("") }
    var betType by remember { mutableStateOf("") }
    var line by remember { mutableStateOf("") }
    val context = LocalContext.current
    val awayLogo = getLogoResId(game.away.lowercase())
    val homeLogo = getLogoResId(game.home.lowercase())
    val errorMessage by viewModel.errorMessage.collectAsState()
    val betPlaced by viewModel.betPlacedSuccessfully.collectAsState()

    LaunchedEffect(betPlaced) {
        if (betPlaced) {
            Toast.makeText(context, "Bet placed on $selectedTeam for \$$wagerAmount", Toast.LENGTH_SHORT).show()
            viewModel.resetBetPlacedFlag()
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = awayLogo),
                contentDescription = "${game.away} logo",
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "${game.away} @ ${game.home}",
                style = MaterialTheme.typography.headlineMedium
            )
            Image(
                painter = painterResource(id = homeLogo),
                contentDescription = "${game.home} logo",
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = "Game Time: ${game.gameTime}",
            style = MaterialTheme.typography.bodyMedium
        )

        if (odds != null) {
            val sportsBook = odds.sportsBooks.firstOrNull()
            val book = sportsBook?.odds

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("\uD83D\uDCC8 Betting Odds", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Team", fontSize = 18.sp)
                        Text("Moneyline", fontSize = 18.sp)
                        Text("Run Line", fontSize = 18.sp)
                        Text("Over/Under", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(game.away, fontSize = 16.sp)

                        Button(onClick = {
                            selectedTeam = game.away
                            betType = "ML"
                            line = "ML"
                            selectedOdds = "${book?.awayTeamMLOdds}"
                        }) {
                            Text("${book?.awayTeamMLOdds}")
                        }

                        Button(onClick = {
                            selectedTeam = game.away
                            betType = "RL"
                            line = "${book?.awayTeamRunLine}"
                            selectedOdds = "${book?.awayTeamRunLineOdds}"
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${book?.awayTeamRunLine}")
                                Text("${book?.awayTeamRunLineOdds}")
                            }
                        }

                        Button(onClick = {
                            selectedTeam = "Over"
                            betType = "TO"
                            line = "${book?.totalOver}"
                            selectedOdds = "${book?.totalOverOdds}"
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${book?.totalOver}")
                                Text("${book?.totalOverOdds}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(game.home, fontSize = 16.sp)

                        Button(onClick = {
                            selectedTeam = game.home
                            betType = "ML"
                            line = "ML"
                            selectedOdds = "${book?.homeTeamMLOdds}"
                        }) {
                            Text("${book?.homeTeamMLOdds}")
                        }

                        Button(onClick = {
                            selectedTeam = game.home
                            betType = "RL"
                            line = "${book?.homeTeamRunLine}"
                            selectedOdds = "${book?.homeTeamRunLineOdds}"
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${book?.homeTeamRunLine}")
                                Text("${book?.homeTeamRunLineOdds}")
                            }
                        }

                        Button(onClick = {
                            selectedTeam = "Under"
                            betType = "TO"
                            line = "${book?.totalUnder}"
                            selectedOdds = "${book?.totalUnderOdds}"
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${book?.totalUnder}")
                                Text("${book?.totalUnderOdds}")
                            }
                        }
                    }
                }
            }
        } else {
            Text("Loading betting odds...")
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

        if (game.gameStatus == "Completed") {
            Text(
                text = " You cannot bet on completed games.",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                val cleanWagerAmount = wagerAmount.replace(",", "").trim()
                val prediction = Prediction(
                    userOwnerId = GlobalVariables.currentUser,
                    gameId = game.gameId,
                    predictedWinner = selectedTeam,
                    betType = betType,
                    line = line,
                    bettingOdds = selectedOdds,
                    betAmount = cleanWagerAmount.toInt(),
                    concluded = false,
                    result = "Active"
                )
                viewModel.placeBet(prediction)
            },
            enabled = selectedTeam.isNotEmpty() &&
                    wagerAmount.isNotEmpty() &&
                    game.gameStatus != "Completed",
            colors = ButtonDefaults.buttonColors(
                containerColor = if (betPlaced) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Place Bet on $selectedTeam")
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
}