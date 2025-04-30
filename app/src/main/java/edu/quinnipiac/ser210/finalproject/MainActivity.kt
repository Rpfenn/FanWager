package edu.quinnipiac.ser210.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.model.FanWagerViewModelFactory
import edu.quinnipiac.ser210.finalproject.ui.theme.Purple40
import edu.quinnipiac.ser210.finalproject.navigation.FanWagerNavigation
import edu.quinnipiac.ser210.finalproject.navigation.listOfNavItems
import edu.quinnipiac.ser210.finalproject.ui.theme.FinalProjectTheme
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
    private fun SetupNavigation(viewModel: FanWagerViewModel){
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var selectedItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.loadUserCurrency()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent =
                {
                    ModalDrawerSheet{
                        Column {
                            Box(modifier = Modifier.height(100.dp).fillMaxWidth(0.8f).background(color = Purple40)) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rounded app icon
//                                    Image(
//                                        painter = painterResource(id = R.drawable.ic_app_icon), // Replace with your app icon resource
//                                        contentDescription = null,
//                                        modifier = Modifier
//                                            .size(60.dp)
//                                            .clip(CircleShape)
//                                            .background(MaterialTheme.colorScheme.secondary)
//                                            .padding(8.dp)
//                                    )
                                    // Spacing between icon and app name
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "FanWager",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Normal,
                                            color = Color.White
                                        )
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
                                            imageVector = if (index == selectedItemIndex) {
                                                navigationItem.selectedIcon
                                            } else {
                                                navigationItem.unselectedIcon
                                            }, contentDescription = navigationItem.title
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
        ){
            val currency by viewModel.currency.collectAsState()
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "FanWager - MLB")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
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
                    // Add NavHost here
                    FanWagerNavigation(navController = navController,
                        viewModel = viewModel)
                }

                )
        }
    }
}






