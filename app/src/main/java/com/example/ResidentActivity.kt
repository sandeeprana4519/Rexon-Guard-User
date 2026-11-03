package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.RexonRepository
import com.example.ui.components.DedicatedResidentHeader
import com.example.ui.components.IncomingGateCallDialog
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.RexonTheme
import com.example.ui.user.RexonUserApp

class ResidentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RexonTheme {
                StandaloneResidentScreen()
            }
        }
    }
}

@Composable
fun StandaloneResidentScreen() {
    val userProfile by RexonRepository.userProfile.collectAsState()
    val activeAlerts by RexonRepository.securityAlerts.collectAsState()
    val incomingGateCall by RexonRepository.incomingGateCall.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            DedicatedResidentHeader(
                userProfile = userProfile,
                unreadCount = activeAlerts.size,
                onOpenNotifications = {
                    Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show()
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            RexonUserApp(
                activeAlerts = activeAlerts,
                onResolveAlert = { alertId ->
                    RexonRepository.resolveSecurityAlert(alertId)
                    Toast.makeText(context, "Emergency SOS marked Resolved.", Toast.LENGTH_SHORT).show()
                }
            )

            // Real-Time Incoming Gate Call Alert (VoIP style)
            incomingGateCall?.let { callLog ->
                IncomingGateCallDialog(
                    callLog = callLog,
                    onAccept = {
                        RexonRepository.acceptIncomingCall(callLog.id)
                        Toast.makeText(context, "Entry Permitted! Guard notified to let ${callLog.visitorName} IN.", Toast.LENGTH_LONG).show()
                    },
                    onDeny = {
                        RexonRepository.denyIncomingCall(callLog.id)
                        Toast.makeText(context, "Entry Denied. Guard notified.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
