package edu.quinnipiac.ser210.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.model.FanWagerViewModelFactory
import edu.quinnipiac.ser210.finalproject.navigation.FanWagerNavigation
import edu.quinnipiac.ser210.finalproject.navigation.listOfNavItems
import edu.quinnipiac.ser210.finalproject.ui.theme.FinalProjectTheme
import edu.quinnipiac.ser210.finalproject.ui.theme.Purple40
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val viewModel: FanWagerViewModel = viewModel(factory = FanWagerViewModelFactory(db))
            val theme by viewModel.theme.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.renameDefaultUserToYou()
            }

            FinalProjectTheme(colorSchemeType = theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SetupNavigation(viewModel)
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SetupNavigation(viewModel: FanWagerViewModel) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val currency by viewModel.currency.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadUserCurrency()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column {
                        Box(
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth(0.8f)
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "FanWager Logo",
                                    modifier = Modifier
                                        .size(512.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        listOfNavItems.forEachIndexed { index, navigationItem ->
                            NavigationDrawerItem(
                                label = { Text(text = navigationItem.title) },
                                selected = index == selectedItemIndex,
                                onClick = {
                                    selectedItemIndex = index
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(navigationItem.route)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex)
                                            navigationItem.selectedIcon
                                        else
                                            navigationItem.unselectedIcon,
                                        contentDescription = navigationItem.title
                                    )
                                },
                                modifier = Modifier
                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                                    .fillMaxWidth(0.7f)
                            )
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "FanWager - MLB") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        actions = {
                            Text(
                                text = "💰 $currency",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    )
                },
                content = {
                    FanWagerNavigation(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            )
        }
    }
}
