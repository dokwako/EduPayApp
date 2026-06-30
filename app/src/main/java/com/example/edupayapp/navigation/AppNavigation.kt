package com.example.edupayapp.navigation

import android.widget.Toast // ADDED for download alerts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope // ADDED to launch network calls
import androidx.compose.ui.platform.LocalContext // ADDED for Toasts
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch // ADDED for background work

import com.example.edupayapp.data.AuthService // ADDED to fetch the receipt

import com.example.edupayapp.ui.createpin.CreatePinScreen
import com.example.edupayapp.ui.dashboard.DashboardScreen
import com.example.edupayapp.ui.forgotpin.ForgotPinScreen
import com.example.edupayapp.ui.help.HelpScreen
import com.example.edupayapp.ui.history.HistoryScreen
import com.example.edupayapp.ui.reciept.ReceiptDetailScreen
import com.example.edupayapp.ui.login.LoginScreen
import com.example.edupayapp.ui.otp.OtpScreen
import com.example.edupayapp.ui.payment.PaymentScreen
import com.example.edupayapp.ui.pin.PinScreen
import com.example.edupayapp.ui.profile.ProfileScreen
import com.example.edupayapp.ui.register.SignUpScreen
import com.example.edupayapp.ui.welcome.WelcomeScreen
import com.example.edupayapp.ui.notifications.NotificationScreen
import com.example.edupayapp.ui.shared.SharedEduPayViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // We keep this here for your PaymentScreen until we upgrade it later
    val sharedViewModel: SharedEduPayViewModel = viewModel()

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
                onNavigateToNotifications = { navController.navigate("notifications") },
                onChildClicked = { id, name, school, amount, status, itemName ->
                    val safeId = android.net.Uri.encode(id)
                    val safeName = android.net.Uri.encode(name)
                    val safeSchool = android.net.Uri.encode(school)
                    val safeAmount = android.net.Uri.encode(amount)
                    val safeStatus = android.net.Uri.encode(status)
                    val safeItemName = android.net.Uri.encode(itemName)

                    // CHANGED THIS LINE
                    navController.navigate("payment/$safeId/$safeName/$safeSchool/$safeAmount/$safeStatus/$safeItemName")
                }
            )
        }

        composable("notifications") {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() }
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

        // --- UPDATED: History Screen ---
        composable("history") {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                // 1. We now expect a single paymentId instead of the dummy strings
                onViewReceiptClicked = { paymentId ->
                    // 2. Launch the background task to hit the Ktor endpoint
                    scope.launch {
                        try {
                            val authService = AuthService()
                            val pdfBytes = authService.downloadReceiptBytes(paymentId)

                            // Success! The bytes are ready to be saved to the phone.
                            Toast.makeText(context, "Receipt Downloaded: ${pdfBytes.size} bytes", Toast.LENGTH_SHORT).show()

                            // Note: Later, we can add the Android File I/O code here to actually
                            // save these bytes into a .pdf file in the user's Downloads folder.
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to download receipt.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // We leave this here just in case you still need the dummy receipt screen somewhere else
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

        // CHANGED THIS WHOLE BLOCK
        composable(
            route = "payment/{obligationId}/{studentName}/{schoolName}/{amount}/{status}/{itemName}",
            arguments = listOf(
                navArgument("obligationId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("schoolName") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType },
                navArgument("itemName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val oId = android.net.Uri.decode(backStackEntry.arguments?.getString("obligationId") ?: "")
            val sName = android.net.Uri.decode(backStackEntry.arguments?.getString("studentName") ?: "")
            val scName = android.net.Uri.decode(backStackEntry.arguments?.getString("schoolName") ?: "")
            val amt = android.net.Uri.decode(backStackEntry.arguments?.getString("amount") ?: "")
            val stat = android.net.Uri.decode(backStackEntry.arguments?.getString("status") ?: "")
            val iName = android.net.Uri.decode(backStackEntry.arguments?.getString("itemName") ?: "")

            PaymentScreen(
                sharedViewModel = sharedViewModel,
                obligationId = oId, // NOW PASSES oId
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