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
import com.example.edupayapp.ui.history.ReceiptDetailScreen
// FIX 1: Exact spelling of your receipt folder
import com.example.edupayapp.ui.login.LoginScreen
import com.example.edupayapp.ui.otp.OtpScreen
import com.example.edupayapp.ui.payment.PaymentScreen
import com.example.edupayapp.ui.pin.PinScreen
import com.example.edupayapp.ui.profile.ProfileScreen
// FIX 2: Exact spelling of your profile folder
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

        composable("dashboard") {
            DashboardScreen(
                onLoggedOut = { navController.navigate("welcome") { popUpTo(0) } },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToReceipts = { navController.navigate("history") },
                onNavigateToHelp = { navController.navigate("help") },
                onChildClicked = { id, name, school, amount, status, itemName ->
                    val safeId = android.net.Uri.encode(id)
                    val safeName = android.net.Uri.encode(name)
                    val safeSchool = android.net.Uri.encode(school)
                    val safeAmount = android.net.Uri.encode(amount)
                    val safeStatus = android.net.Uri.encode(status)
                    val safeItemName = android.net.Uri.encode(itemName)

                    navController.navigate("payment/$safeId/$safeName/$safeSchool/$safeAmount/$safeStatus/$safeItemName")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateToChangePin = {
                    val userEmailPlaceholder = "logged_in_user@edupay.com"
                    navController.navigate("create_pin/$userEmailPlaceholder")
                },
                onLogoutConfirmed = { navController.navigate("welcome") { popUpTo(0) } },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- UPDATED HISTORY ROUTE ---
        // --- UPDATED HISTORY ROUTE ---
        composable("history") {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onViewReceiptClicked = { name, amt, item ->
                    val encodedName = android.net.Uri.encode(name)
                    val encodedAmt = android.net.Uri.encode(amt)
                    val encodedItem = android.net.Uri.encode(item)
                    navController.navigate("receipt_detail/$encodedName/$encodedAmt/$encodedItem")
                }
            )
        }

        // --- NEW RECEIPT DETAIL ROUTE (FIX 3: ADDED DECODE LOGIC)
        composable(
            route = "receipt_detail/{name}/{amount}/{item}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("item") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = android.net.Uri.decode(backStackEntry.arguments?.getString("name") ?: "")
            val amt = android.net.Uri.decode(backStackEntry.arguments?.getString("amount") ?: "")
            val item = android.net.Uri.decode(backStackEntry.arguments?.getString("item") ?: "")

            ReceiptDetailScreen(
                studentName = name,
                amount = amt,
                itemName = item,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "payment/{studentId}/{studentName}/{schoolName}/{amount}/{status}/{itemName}",
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("schoolName") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType },
                navArgument("itemName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sId = android.net.Uri.decode(backStackEntry.arguments?.getString("studentId") ?: "")
            val sName = android.net.Uri.decode(backStackEntry.arguments?.getString("studentName") ?: "")
            val scName = android.net.Uri.decode(backStackEntry.arguments?.getString("schoolName") ?: "")
            val amt = android.net.Uri.decode(backStackEntry.arguments?.getString("amount") ?: "")
            val stat = android.net.Uri.decode(backStackEntry.arguments?.getString("status") ?: "")
            val iName = android.net.Uri.decode(backStackEntry.arguments?.getString("itemName") ?: "")

            PaymentScreen(
                studentId = sId,
                studentName = sName,
                schoolName = scName,
                amount = amt,
                status = stat,
                itemName = iName,
                onNavigateBack = { navController.popBackStack() },
                onViewReceipt = { paymentId ->
                    navController.navigate("history") {
                        popUpTo("dashboard") { inclusive = false }
                    }
                }
            )
        }

        composable("help") {
            HelpScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}