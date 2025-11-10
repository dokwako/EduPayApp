package com.example.edupayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edupayapp.ui.createpin.CreatePinScreen
import com.example.edupayapp.ui.dashboard.DashboardScreen
import com.example.edupayapp.ui.forgotpin.ForgotPinScreen
import com.example.edupayapp.ui.help.HelpScreen
import com.example.edupayapp.ui.login.LoginScreen
import com.example.edupayapp.ui.otp.OtpScreen
import com.example.edupayapp.ui.pin.PinScreen
import com.example.edupayapp.ui.profile.ProfileScreen
import com.example.edupayapp.ui.register.SignUpScreen
import com.example.edupayapp.ui.welcome.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    //nav host - container for all screens
    //
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
                    // After successful OTP, navigate to our new "Create PIN" screen.
                    navController.navigate("create_pin")
                }
            )
        }

        composable("pin") {
            PinScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onForgotPinClicked = {
                    // Connects the "Forgot PIN?" link to the recovery flow.
                    navController.navigate("forgot_pin")
                }
            )
        }
        composable("create_pin") {
            CreatePinScreen(
                onPinCreated = {
                    // After the PIN is created, go to the dashboard and clear the back stack.
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("forgot_pin") {
            ForgotPinScreen(
                onNavigateToOtp = {
                    // Reuses the OTP screen for identity verification.
                    navController.navigate("otp_verification")
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                // Update callbacks to match the new bottom navigation
                onNavigateToHistory = { navController.navigate("receipts") }, // Changed
                onNavigateToProfile = { navController.navigate("profile") }, // Changed
                onNavigateToReceipts = { navController.navigate("receipts") },
                onNavigateToHelp = { navController.navigate("help") },
                onNavigateToChildDetails = { studentId ->
                    // Navigate to a details screen, passing the ID
                    navController.navigate("child_details/$studentId")
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateToChangePin = {
                    // Navigate to the "Change PIN" flow, which starts with the CreatePinScreen.
                    navController.navigate("create_pin")
                },
                onLogoutConfirmed = {
                    // Navigate back to welcome and clear the stack
                    navController.navigate("welcome") {
                        popUpTo(0)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack() // Standard back navigation
                }
            )
        }
        composable("help") {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack() // Go back to the previous screen (Dashboard)
                }
            )
        }
        composable("history") {
            // TODO: Add HistoryScreen here
        }

        composable("receipts") {
        /* TODO: Add ReceiptsScreen here */ }

        composable("child_details/{studentId}") { backStackEntry ->
            // Get the studentId argument from the route
            val studentId = backStackEntry.arguments?.getString("studentId")
            // TODO: Add ChildDetailsScreen here, passing the studentId
        }
    }
}