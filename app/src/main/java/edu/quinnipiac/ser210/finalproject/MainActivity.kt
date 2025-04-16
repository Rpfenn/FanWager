package edu.quinnipiac.ser210.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import edu.quinnipiac.ser210.finalproject.navigation.FanWagerNavigation
import edu.quinnipiac.ser210.finalproject.ui.theme.FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalProjectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FanWagerNavigation()
                }
            }
        }
    }
}
