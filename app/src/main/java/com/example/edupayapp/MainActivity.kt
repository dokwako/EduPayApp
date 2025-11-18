package com.example.edupayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.edupayapp.navigation.AppNavigation
import com.example.edupayapp.ui.theme.EduPayAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            EduPayAppTheme {
                AppNavigation()
            }
        }
    }
}
