package com.example.edupayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

    // nav host - container for all screens
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
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
                    // Navigate to Dashboard after successful Login (PIN is already confirmed)
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { email ->
                    // PASS THE EMAIL to the OTP screen
                    navController.navigate("otp_verification/$email")
                },
                onSignInClick = {
                    navController.navigate("login")
                }
            )
        }

        composable(
            route = "otp_verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            OtpScreen(
                email = email,
                onVerificationSuccess = {
                    // After successful OTP, PASS THE EMAIL to the Create PIN screen
                    navController.navigate("create_pin/$email")
                }
            )
        }

        composable(
            route = "create_pin/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            CreatePinScreen(
                email = email, // Pass the email to the ViewModel for the final API call
                onPinCreated = {
                    // After the PIN is created, go to the dashboard and clear the back stack.
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        // NOTE: PinScreen route is used for re-entry PIN check after initial login
        composable("pin") {
            PinScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onForgotPinClicked = {
                    navController.navigate("forgot_pin")
                }
            )
        }

        composable("forgot_pin") {
            ForgotPinScreen(
                onNavigateToOtp = { email ->
                    // Reuses the OTP screen for identity verification by passing the email.
                    navController.navigate("otp_verification/$email")
                },
                // --- FIX: Add callback to navigate back to Login ---
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onNavigateToHistory = { navController.navigate("receipts") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToReceipts = { navController.navigate("receipts") },
                onNavigateToHelp = { navController.navigate("help") },
                onNavigateToChildDetails = { studentId ->
                    navController.navigate("child_details/$studentId")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateToChangePin = {
                    // This should pass the current user's email, which should be retrieved from tokenStorage in a real app.
                    val userEmailPlaceholder = "logged_in_user@edupay.com"
                    navController.navigate("create_pin/$userEmailPlaceholder")
                },
                onLogoutConfirmed = {
                    navController.navigate("welcome") {
                        popUpTo(0)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("help") {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            // TODO: Add HistoryScreen here
        }

        composable("receipts") {
            /* TODO: Add ReceiptsScreen here */
        }

        composable("child_details/{studentId}") { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")
            // TODO: Add ChildDetailsScreen here, passing the studentId
        }
    }
}