package com.codelegger.golfperformancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.codelegger.golfperformancetracker.theme.GolfTheme
import com.codelegger.golfperformancetracker.ui.navigation.GolfNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host for the Compose UI.
 *
 * [AndroidEntryPoint] lets Hilt inject into this activity and any `@HiltViewModel`
 * obtained from Compose destinations below it.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GolfTheme {
                GolfNavHost()
            }
        }
    }
}
