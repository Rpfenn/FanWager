package edu.quinnipiac.ser210.finalproject.navigation


import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.quinnipiac.ser210.finalproject.model.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.screens.DetailsScreen
import edu.quinnipiac.ser210.finalproject.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: String,
    navController: NavController,
    navigateUp: () -> Unit,
    context: Context,
    textToShare: String,
    onHelpClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canNavigateBack = navController.previousBackStackEntry != null
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("McDonald's Menu") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Share button
            if (textToShare.isNotBlank()) {
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Share Item")
                        putExtra(Intent.EXTRA_TEXT, textToShare)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share via"))
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                }
            }

            // Overflow menu
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        menuExpanded = false
                        onSettingsClick()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Help") },
                    onClick = {
                        menuExpanded = false
                        onHelpClick()
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MovieNavigation() {
    val navController = rememberNavController()

    val viewModel: McdViewModel = viewModel()
    viewModel.getMenuItems()


    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeScreen.name,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppScreens.HomeScreen.name) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = AppScreens.DetailScreen.name + "/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            DetailsScreen(
                navController = navController,
                viewModel = viewModel,
                itemName = name
            )
        }
    }
}
