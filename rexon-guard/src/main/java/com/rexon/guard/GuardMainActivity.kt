package com.rexon.guard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.rexon.common.data.RexonRepository
import com.rexon.common.theme.RexonTheme

class GuardMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RexonTheme {
                GuardRootScreen()
            }
        }
    }
}

@Composable
fun GuardRootScreen() {
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
