package com.example.passman


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.passman.navigation.AppNavigation
import com.example.passman.screens.FirstLaunchScreen
import com.example.passman.ui.theme.PassmanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassmanTheme {
                AppNavigation()
            }
        }
    }
}




