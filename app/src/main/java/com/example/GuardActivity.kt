package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.RexonRepository
import com.example.ui.guard.RexonGuardApp
import com.example.ui.theme.RexonTheme

class GuardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RexonTheme {
                StandaloneGuardScreen()
            }
        }
    }
}

@Composable
fun StandaloneGuardScreen() {
    val activeAlerts by RexonRepository.securityAlerts.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        RexonGuardApp(
            activeAlerts = activeAlerts,
            onResolveAlert = { alertId ->
                RexonRepository.resolveSecurityAlert(alertId)
                Toast.makeText(context, "Alert resolved.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
