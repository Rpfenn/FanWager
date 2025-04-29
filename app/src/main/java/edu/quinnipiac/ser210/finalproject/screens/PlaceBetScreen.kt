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
    gameId: String
    //viewModel: FanWagerViewModel = viewModel()
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val viewModel: FanWagerViewModel = viewModel(
        factory = FanWagerViewModelFactory(db)
    )
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
    var betPlaced by remember { mutableStateOf(false) }
    var line by remember { mutableStateOf("") }
    val context = LocalContext.current
    val awayLogo = getLogoResId(game.away.lowercase())
    val homeLogo = getLogoResId(game.home.lowercase())

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
            val sportsBook = odds?.sportsBooks?.firstOrNull()
            val book = sportsBook?.odds

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📈 Betting Odds", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("💵 Moneyline")
                    Text("${game.away}: ${book?.awayTeamMLOdds ?: "N/A"}")
                    Text("${game.home}: ${book?.homeTeamMLOdds ?: "N/A"}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("📊 Run Line")
                    Text("${game.away}: ${book?.awayTeamRunLine ?: "-"} (${book?.awayTeamRunLineOdds ?: "-"})")
                    Text("${game.home}: ${book?.homeTeamRunLine ?: "-"} (${book?.homeTeamRunLineOdds ?: "-"})")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("🔢 Over/Under")
                    Text("Total Over: ${book?.totalOver ?: "-"} (${book?.totalOverOdds ?: "-"})")
                    Text("Total Under: ${book?.totalUnder ?: "-"} (${book?.totalUnderOdds ?: "-"})")
                }
            }
            //Text("Select Pick:")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally))
            {
                Row(horizontalArrangement = Arrangement.spacedBy(63.dp)){
                    Text("Team")
                    Text("ML")
                    Text("RL")
                    Text("Total")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = game.away,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    //Away Moneyline button
                    Button(
                        onClick = {
                            selectedTeam = game.away
                                  betType = "ML"
                                  line = "ML"},
                        modifier = Modifier.width(80.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTeam == game.away && betType == "ML") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )

                    ) {
                        Text(""+book?.awayTeamMLOdds)
                    }
                    //Away Run Line
                    Button(
                        onClick = { selectedTeam = game.away
                                  betType = "RL"
                                  line = ""+book?.awayTeamRunLine
                                  selectedOdds = ""+book?.awayTeamRunLineOdds},
                        modifier = Modifier.width(80.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTeam == game.away && betType == "RL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {

                        Column {
                            Text("" + book?.awayTeamRunLine)
                            Text("" + book?.awayTeamRunLineOdds)
                        }

                    }

                    //Over Button
                    Button(
                        onClick = { selectedTeam = "Over"
                                  betType = "TO"
                                  line = ""+book?.totalOver
                                  selectedOdds = ""+book?.totalOverOdds},
                        modifier = Modifier.width(80.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTeam == "Over") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Column{
                            Text(""+ book?.totalOver)
                            Text("" + book?.totalOverOdds)
                        }

                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        game.home,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    //Home Moneyline
                    Button(
                        onClick = { selectedTeam = game.home
                                  betType = "ML"
                                  line = "ML"
                                  selectedOdds = ""+ book?.homeTeamMLOdds},
                        modifier = Modifier.width(80.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTeam == game.home && betType == "ML") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(""+ book?.homeTeamMLOdds)
                    }
                    //Home Run line
                    Button(
                        onClick = { selectedTeam = game.home
                            betType = "RL"
                            line = "" + book?.homeTeamRunLine
                            selectedOdds = ""+ book?.homeTeamRunLineOdds},
                        modifier = Modifier.width(80.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTeam == game.home && betType == "RL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Column {
                            Text("" + book?.homeTeamRunLine)
                            Text("" + book?.homeTeamRunLineOdds)
                        }
                    }
                    //Under Button
                    Button(
                        onClick = { selectedTeam = "Under"
                            betType = "TO"
                            line = "" + book?.totalUnder
                            selectedOdds = ""+ book?.totalUnderOdds},
                        modifier = Modifier.width(80.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTeam == "Under") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Column{
                            Text(""+ book?.totalUnder)
                            Text("" + book?.totalUnderOdds)
                        }

                    }
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

            //Spacer(modifier = Modifier.height(4.dp))


            Button(
                onClick = {

                    //Not fully functional to add prediction
                    val prediction = Prediction(
                        userOwnerId = GlobalVariables.currentUser,
                        gameId = game.gameId,
                        predictedWinner = selectedTeam!!,
                        betType = betType,
                        line = line,
                        bettingOdds = selectedOdds,
                        betAmount = wagerAmount.toInt(),
                        concluded = false,
                        result = "Active"
                    )
                    viewModel.placeBet(prediction)
                    betPlaced = true
                    Toast.makeText(
                        context,
                        "Bet placed on $selectedTeam for $$wagerAmount",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                },
                enabled = selectedTeam.isNotEmpty() && wagerAmount.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (betPlaced) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Place Bet on $selectedTeam")
            }
        } else {
            Text("Loading betting odds...")
        }

//        Text("Select Team to Win:")
//        Column {
//            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                Button(
//                    onClick = { selectedTeam = game.away },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedTeam == game.away) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
//                    )
//                ) {
//                    Text(game.away)
//                }
//
//                Button(
//                    onClick = { selectedTeam = game.home },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedTeam == game.home) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
//                    )
//                ) {
//                    Text(game.home)
//                }
//
//                Button(
//                    onClick = { selectedTeam = "" },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedTeam == game.home) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
//                    )
//                ) {
//                    Text("Over "+ book?.totalOver)
//                }
//            }
//        }
//
//        Text("Enter Wager Amount:")
//        TextField(
//            value = wagerAmount,
//            onValueChange = { wagerAmount = it },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            singleLine = true,
//            placeholder = { Text("e.g., 50") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//
//        Button(
//            onClick = {
//
//                //Not fully functional to add prediction
//                val prediction = Prediction(
//                    userOwnerId = GlobalVariables.currentUser,
//                    gameId = game.gameId,
//                    predictedWinner = selectedTeam!!,
//                    betType = "ML",
//                    line = "ML",
//                    bettingOdds = "-150",
//                    betAmount = wagerAmount.toInt()
//                )
//                viewModel.placeBet(prediction)
//                betPlaced = true
//                Toast.makeText(
//                    context,
//                    "Bet placed on $selectedTeam for $$wagerAmount",
//                    Toast.LENGTH_SHORT
//                ).show()
//                navController.popBackStack()
//            },
//            enabled = selectedTeam.isNotEmpty() && wagerAmount.isNotEmpty(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (betPlaced) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
//            ),
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            Text("Place Bet on $selectedTeam")
//        }
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