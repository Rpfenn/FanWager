package edu.quinnipiac.ser210.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LeaderboardEntry(val name: String, val currency: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoardScreen() {
    val fakeLeaderboard = listOf(
        LeaderboardEntry("You", 4200),
        LeaderboardEntry("Alex", 3800),
        LeaderboardEntry("Jordan", 3500),
        LeaderboardEntry("Taylor", 3000),
        LeaderboardEntry("Casey", 2700),
        LeaderboardEntry("Drew", 2500),
        LeaderboardEntry("Jack", 1300),
        LeaderboardEntry("Zach", 1200),
        LeaderboardEntry("Aidan", 1000),
        LeaderboardEntry("Conner", 800)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "🏆 Leaderboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEDE7F6),
                            Color(0xFFB39DDB)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(fakeLeaderboard) { index, player ->
                    val bonus = when (index) {
                        0 -> 100
                        1 -> 75
                        2 -> 50
                        3 -> 25
                        else -> 0
                    }
                    LeaderboardRow(rank = index + 1, name = player.name, score = player.currency, bonus = bonus)
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, score: Int, bonus: Int) {
    val cardColor = when (rank) {
        1 -> Color(0xFFFFF176) // Gold
        2 -> Color(0xFFB0BEC5) // Silver
        3 -> Color(0xFFFFAB91) // Bronze
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "#$rank",
                modifier = Modifier.width(40.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$$score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "+$bonus bonus",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
