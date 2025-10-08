package com.example.edupayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.edupayapp.ui.theme.EduPayAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            EduPayAppTheme {
                // Your NavHost will go here later
            }
        }
    }
}
