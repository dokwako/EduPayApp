package com.example.edupayapp.ui.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    viewModel: HelpViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                Tab(
                    selected = uiState.selectedTab == HelpTab.FAQs,
                    onClick = { viewModel.onTabSelected(HelpTab.FAQs) },
                    text = { Text("FAQs") }
                )
                Tab(
                    selected = uiState.selectedTab == HelpTab.ContactUs,
                    onClick = { viewModel.onTabSelected(HelpTab.ContactUs) },
                    text = { Text("Contact Us") }
                )
            }

            // Content based on selected tab
            when (uiState.selectedTab) {
                HelpTab.FAQs -> FaqContent()
                HelpTab.ContactUs -> ContactUsContent()
            }
        }
    }
}

@Composable
private fun FaqContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        FaqItem(
            question = "I forgot my PIN. How can I reset it?",
            answer = "You can reset your PIN from the PIN login screen by tapping 'Forgot PIN?'. You will be asked to verify your phone number via OTP, after which you can set a new PIN."
        )
        Divider()
        FaqItem(
            question = "I made a payment, but I haven't received a receipt. What should I do?",
            answer = "All successful payments generate a digital receipt in the 'History' tab. If you don't see it after 5 minutes, please check your transaction status or contact support."
        )
        Divider()
        FaqItem(
            question = "Money was deducted but payment failed?",
            answer = "In the rare case that money is deducted but the payment fails, the transaction will be automatically reconciled. Please check the app after 30 minutes. If the status is still 'Failed', contact support with your transaction details."
        )
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
        if (expanded) {
            Text(
                text = answer,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ContactUsContent() {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.padding(16.dp)) {
        ContactItem(
            icon = Icons.Default.Language,
            text = "Website",
            onClick = {
                // TODO: Replace with your actual website URL
                uriHandler.openUri("https://spaceyatech.com")
            }
        )
        Divider()
        ContactItem(
            icon = Icons.Default.Phone, // Using a generic icon
            text = "Facebook",
            onClick = {
                // TODO: Replace with your Facebook URL
                uriHandler.openUri("https://facebook.com")
            }
        )
        Divider()
        ContactItem(
            icon = Icons.Default.Phone, // Using a generic icon
            text = "Instagram",
            onClick = {
                // TODO: Replace with your Instagram URL
                uriHandler.openUri("https://instagram.com")
            }
        )
        Divider()
        ContactItem(
            icon = Icons.Default.Phone, // Using a generic icon
            text = "WhatsApp",
            onClick = {
                // TODO: Replace with your WhatsApp number/link
                uriHandler.openUri("httpsM://wa.me/254712345678")
            }
        )
    }
}

@Composable
private fun ContactItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = Color.Gray)
    }
}