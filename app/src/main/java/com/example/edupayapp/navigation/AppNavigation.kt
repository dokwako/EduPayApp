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
import com.example.edupayapp.ui.history.HistoryScreen
import com.example.edupayapp.ui.login.LoginScreen
import com.example.edupayapp.ui.otp.OtpScreen
import com.example.edupayapp.ui.payment.PaymentScreen
import com.example.edupayapp.ui.pin.PinScreen
import com.example.edupayapp.ui.profile.ProfileScreen
import com.example.edupayapp.ui.register.SignUpScreen
import com.example.edupayapp.ui.welcome.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(onGetStartedClick = { navController.navigate("login") })
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { email -> navController.navigate("otp_verification/$email") },
                onSignInClick = { navController.navigate("login") }
            )
        }

        composable(
            route = "otp_verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpScreen(
                email = email,
                onVerificationSuccess = { navController.navigate("create_pin/$email") }
            )
        }

        composable(
            route = "create_pin/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CreatePinScreen(
                email = email,
                onPinCreated = {
                    navController.navigate("dashboard") {
                        popUpTo("welcome") { inclusive = true }
                    }
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
                onForgotPinClicked = { navController.navigate("forgot_pin") }
            )
        }

        composable("forgot_pin") {
            ForgotPinScreen(
                onNavigateToOtp = { email -> navController.navigate("otp_verification/$email") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // --- DASHBOARD AND MAIN APP FLOW ---

        composable("dashboard") {
            DashboardScreen(
                onLoggedOut = {
                    navController.navigate("welcome") { popUpTo(0) }
                },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToReceipts = { navController.navigate("history") },
                onNavigateToHelp = { navController.navigate("help") },
                // Receiving 5 items from Dashboard (ID, Name, School, Amount, Status)
                onChildClicked = { id, name, school, amount, status ->
                    val safeId = android.net.Uri.encode(id)
                    val safeName = android.net.Uri.encode(name)
                    val safeSchool = android.net.Uri.encode(school)
                    val safeAmount = android.net.Uri.encode(amount)
                    val safeStatus = android.net.Uri.encode(status)

                    // FIXED: Corrected the path and removed the extra quotation mark
                    navController.navigate("payment/$safeId/$safeName/$safeSchool/$safeAmount/$safeStatus")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateToChangePin = {
                    val userEmailPlaceholder = "logged_in_user@edupay.com"
                    navController.navigate("create_pin/$userEmailPlaceholder")
                },
                onLogoutConfirmed = {
                    navController.navigate("welcome") { popUpTo(0) }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("history") {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onViewReceiptClicked = { }
            )
        }

        // UPGRADED: Route now expects 5 arguments
        composable(
            route = "payment/{studentId}/{studentName}/{schoolName}/{amount}/{status}",
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("schoolName") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = android.net.Uri.decode(backStackEntry.arguments?.getString("studentId") ?: "")
            val studentName = android.net.Uri.decode(backStackEntry.arguments?.getString("studentName") ?: "")
            val schoolName = android.net.Uri.decode(backStackEntry.arguments?.getString("schoolName") ?: "")
            val amount = android.net.Uri.decode(backStackEntry.arguments?.getString("amount") ?: "")
            val status = android.net.Uri.decode(backStackEntry.arguments?.getString("status") ?: "")

            PaymentScreen(
                studentId = studentId,
                studentName = studentName,
                schoolName = schoolName,
                amount = amount,
                status = status,
                onNavigateBack = { navController.popBackStack() },
                onViewReceipt = { }
            )
        }

        composable("help") {
            HelpScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}