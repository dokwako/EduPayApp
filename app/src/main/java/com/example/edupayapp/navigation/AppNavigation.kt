package com.example.edupayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edupayapp.ui.login.LoginScreen
import com.example.edupayapp.ui.otp.OtpScreen
import com.example.edupayapp.ui.register.SignUpScreen
import com.example.edupayapp.ui.welcome.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    //nav host - container for all screens
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ){
        composable("welcome") {
            WelcomeScreen(
                onGetStartedClick = {
                navController.navigate("login")
            }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("pin")
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                }
            )

        }
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("otp_verification")
                },
                onSignInClick = {
                    navController.navigate("login")
                }
            )

        }
        composable("otp_verification") {
            OtpScreen(
                onVerificationSuccess = {
                    navController.navigate("home") {
                     popUpTo("Welcome")
                    }
                }
            )
        }
    }
}