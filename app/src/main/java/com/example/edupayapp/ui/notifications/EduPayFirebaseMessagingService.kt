package com.example.edupayapp.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.edupayapp.data.AuthService // ADDED

class EduPayFirebaseMessagingService : FirebaseMessagingService() {

    // This fires automatically when the app is installed or the token refreshes
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FCM Token Generated: $token")

        // Silently send the new token to the Django backend in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authService = AuthService()
                authService.updateFcmToken(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // This fires the millisecond Django sends a push notification
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "EduPay Update"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "edupay_payments"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0+ requires a Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Payment Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify((0..1000).random(), notification)
    }
}