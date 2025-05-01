package edu.quinnipiac.ser210.finalproject.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.quinnipiac.ser210.finalproject.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(0.8f) }

    LaunchedEffect(Unit) {
        visible = true
        scale = 1f
        delay(5000)
        visible = false
        delay(500)
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A3D62), // dark blue
                        Color(0xFF8CF2E3)  // teal
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1200)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Image(
                    painter = painterResource(id = context.resources.getIdentifier("logo", "drawable", context.packageName)),
                    contentDescription = "FanWager Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(visible = visible) {
                Text(
                    text = "Major League Predictions",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Cursive,
                        color = Color.White
                    )
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(visible = visible) {
                Text(
                    text = "Created by Tannon Bryant, Ethan Kulawiak and Ryan Fennelly",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive,
                        color = Color.White
                    )
                )
            }
        }
    }
}
