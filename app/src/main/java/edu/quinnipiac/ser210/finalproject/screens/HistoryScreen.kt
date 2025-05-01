package edu.quinnipiac.ser210.finalproject.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.data.Prediction

@Composable
fun HistoryScreen(
    viewModel: FanWagerViewModel
) {
    val predictions by viewModel.userPredictions.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadUserPredictions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Text("📜 Bet History", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (predictions.isEmpty()) {
            Text("You haven't placed any bets yet.")
        } else {
            if (predictions.any { it.result == "win" || it.result == "loss" }) {
                Button(
                    onClick = { viewModel.clearCompletedPredictions() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text("🗑 Clear Completed Bets")
                }
            }

            LazyColumn {
                items(predictions) { prediction ->
                    PredictionCard(prediction = prediction)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
fun PredictionCard(prediction: Prediction) {
    val teams = prediction.gameId.split("_").getOrNull(1)?.split("@")
    val gameText = if (teams?.size == 2) "${teams[0]} vs ${teams[1]}" else prediction.gameId

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏟 Game: $gameText", fontWeight = FontWeight.SemiBold)
            Text("Bet: ${prediction.predictedWinner} ${prediction.betType}")
            if (prediction.betType != "ML") {
                Text("Line: ${prediction.line}")
            }
            Text("Odds: ${prediction.bettingOdds}")
            Text("Amount: \$${prediction.betAmount}")
            Text(
                "Result: ${prediction.result.capitalize()}",
                color = when (prediction.result.lowercase()) {
                    "win" -> Color.Green
                    "loss" -> Color.Red
                    else -> Color.Gray
                }
            )

            if (prediction.result == "win") {
                val profit = calculateWinnings(prediction.betAmount, prediction.bettingOdds)
                Text("Winnings: \$${profit + prediction.betAmount}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun calculateWinnings(betAmount: Int, odds: String): Int {
    return try {
        val value = odds.replace(",", "").trim().toInt()
        if (value > 0) (betAmount * value / 100.0).toInt()
        else (betAmount * 100 / -value.toDouble()).toInt()
    } catch (e: Exception) {
        Log.e("BettingCalc", "Invalid odds format: $odds", e)
        0
    }
}