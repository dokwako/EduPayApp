package com.example.edupayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edupayapp.ui.register.SignUpScreen

object AppRoutes{
    const val SIGN_UP = "signup"
    const val SIGN_IN = "signin"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    //nav host - container for all screens
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SIGN_UP
    ) {
        composable(AppRoutes.SIGN_UP) {
            SignUpScreen(
                onSignInClick = {
                    navController.navigate(AppRoutes.SIGN_IN)
                },
                onSignUpSuccess = {
                    //TODO : navigate to otp
                }
            )
        }


    }
}