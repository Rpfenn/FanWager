package edu.quinnipiac.ser210.finalproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showInfoDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Settings Screen", fontFamily = FontFamily.Cursive, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("This app lets you track MLB games and place fun predictions!")
                }
            }) {
                Text("Help")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                showInfoDialog = true
            }) {
                Text("Info")
            }
        }
    }

    // Snackbar display
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    // Info dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Close")
                }
            },
            title = { Text("About FanWager") },
            text = {
                Text("FanWager v1.0\nCreated by Tannon Bryant, Ethan Kulawiak and Ryan Fennelly This app is for entertainment purposes only.")
            }
        )
    }
}