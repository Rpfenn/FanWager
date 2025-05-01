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
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.model.FanWagerViewModelFactory
import edu.quinnipiac.ser210.finalproject.ui.theme.ColorSchemeType
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: FanWagerViewModel) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val theme by viewModel.theme.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Settings", fontFamily = FontFamily.Cursive, fontSize = 28.sp)

            // Theme Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Theme", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    ColorSchemeType.values().forEach { option ->
                        Button(
                            onClick = { viewModel.setTheme(option) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }

            //Help / Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Support", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "FanWager lets you track MLB games and place predictions!"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Help")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Info")
                    }
                }
            }
        }

        //Info Dialog
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
                    Text("FanWager v1.0\nCreated by Tannon Bryant, Ethan Kulawiak, and Ryan Fennelly.\nFor entertainment only.")
                }
            )
        }
    }
}