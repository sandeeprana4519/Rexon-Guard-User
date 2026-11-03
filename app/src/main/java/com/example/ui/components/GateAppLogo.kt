package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlusJakartaSans

/**
 * GateApp Logo: Exact stylized dual-gate monogram matching user screenshot.
 */
@Composable
fun GateAppLogo(
    modifier: Modifier = Modifier.size(72.dp),
    color: Color = Color(0xFF1E1E1E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.11f

        val pathLeft = Path().apply {
            // Left gate loop
            // Start from bottom inner hook
            moveTo(w * 0.42f, h * 0.85f)
            lineTo(w * 0.28f, h * 0.85f)
            lineTo(w * 0.28f, h * 0.65f)
            lineTo(w * 0.42f, h * 0.65f)
            lineTo(w * 0.42f, h * 0.35f)
            // Arc to outer left
            quadraticTo(w * 0.42f, h * 0.05f, w * 0.28f, h * 0.05f)
            quadraticTo(w * 0.08f, h * 0.05f, w * 0.08f, h * 0.35f)
            lineTo(w * 0.08f, h * 0.85f)
        }

        val pathRight = Path().apply {
            // Right gate loop (mirrored)
            moveTo(w * 0.58f, h * 0.85f)
            lineTo(w * 0.72f, h * 0.85f)
            lineTo(w * 0.72f, h * 0.65f)
            lineTo(w * 0.58f, h * 0.65f)
            lineTo(w * 0.58f, h * 0.35f)
            quadraticTo(w * 0.58f, h * 0.05f, w * 0.72f, h * 0.05f)
            quadraticTo(w * 0.92f, h * 0.05f, w * 0.92f, h * 0.35f)
            lineTo(w * 0.92f, h * 0.85f)
        }

        drawPath(
            path = pathLeft,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawPath(
            path = pathRight,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * GateApp Brand Header Component (matching Screenshot 1 & 8)
 */
@Composable
fun GateAppBrandEmblem(
    subtitle: String = "GUARD",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GateAppLogo(modifier = Modifier.size(68.dp))
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "GATEAPP",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans,
            letterSpacing = 4.sp,
            color = Color(0xFF1E1E1E)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PlusJakartaSans,
            letterSpacing = 3.sp,
            color = Color(0xFFA1A1AA)
        )
    }
}
