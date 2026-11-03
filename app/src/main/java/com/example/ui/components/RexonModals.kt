package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.*
import com.example.ui.theme.*

// Floating Purple Shield Button (Royal Violet Luxury Jewel)
@Composable
fun FloatingPurpleShieldButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = RoyalViolet,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
            .size(62.dp)
            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            .testTag("floating_purple_shield_button")
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Security Shield SOS",
            modifier = Modifier.size(28.dp)
        )
    }
}

// Purple Shield Action Modal (SOS & Message to Security)
@Composable
fun PurpleShieldActionModal(
    onDismiss: () -> Unit,
    onSendMessage: (String, String) -> Unit,
    onTriggerSos: (AlertType) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: SOS Alert, 1: Send Message
    var messageText by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("SECURITY") } // SECURITY or ADMIN

    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RoyalVioletLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Shield",
                                tint = RoyalViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency & Desk",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Instant Gate Security Dispatch",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "SOS", modifier = Modifier.size(16.dp), tint = CrimsonRed)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Emergency SOS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Chat, contentDescription = "Msg", modifier = Modifier.size(16.dp), tint = CharcoalDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send Message", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 0) {
                    // Security Alert (SOS) Options
                    Text(
                        text = "ONE-TAP INSTANT SECURITY DISPATCH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CrimsonRed,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val alerts = listOf(
                        AlertType.FIRE_ALERT to Icons.Default.LocalFireDepartment,
                        AlertType.STUCK_IN_LIFT to Icons.Default.Elevator,
                        AlertType.ANIMAL_THREAT to Icons.Default.Pets,
                        AlertType.VISITOR_THREAT to Icons.Default.PersonOff
                    )

                    alerts.forEach { (alert, icon) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .testTag("sos_trigger_${alert.name.lowercase()}")
                                .clickable {
                                    onTriggerSos(alert)
                                    Toast.makeText(context, "${alert.title} Dispatched to Gate Security!", Toast.LENGTH_LONG).show()
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CrimsonRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = alert.title,
                                        tint = CrimsonRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = CrimsonRed
                                    )
                                    Text(
                                        text = "Instant sirens to Gate A, Gate B & Security Staff",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Trigger",
                                    tint = CrimsonRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Send Message Compose Panel
                    Text(
                        text = "DIRECT INSTRUCTION TO DESK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = RoyalViolet,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Recipient Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("SECURITY" to "Gate Security", "ADMIN" to "Society Admin").forEach { (key, label) ->
                            val isSel = recipient == key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) CharcoalDark else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSel) CharcoalDark else BorderGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { recipient = key }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        label = { Text("Write gate note or instruction") },
                        placeholder = { Text("e.g. Courier arriving soon, please hold parcel or leave with neighbor...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("guard_message_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                onSendMessage(messageText, recipient)
                                Toast.makeText(context, "Instruction sent to $recipient board!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("send_guard_message_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                        enabled = messageText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Instruction", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Digital GatePass Ticket Dialog with Dynamic QR Code
@Composable
fun GatePassTicketDialog(
    preApproval: PreApproval,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = "Ticket",
                            tint = RoyalViolet,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REXON GATEPASS",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pass Type & Visitor info
                Surface(
                    color = RoyalVioletLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${preApproval.category.label.uppercase()} PASS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalViolet,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = preApproval.visitorName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Tower B • Flat ${preApproval.fullFlatId}",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic QR Code Canvas
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSize = 15
                        val stepX = size.width / gridSize
                        val stepY = size.height / gridSize

                        val seed = preApproval.passcode.hashCode()
                        for (r in 0 until gridSize) {
                            for (c in 0 until gridSize) {
                                val isCornerMarker =
                                    (r in 0..4 && c in 0..4) ||
                                    (r in 0..4 && c in (gridSize - 5) until gridSize) ||
                                    (r in (gridSize - 5) until gridSize && c in 0..4)

                                val shouldDraw = if (isCornerMarker) {
                                    val localR = if (r >= gridSize - 5) r - (gridSize - 5) else r
                                    val localC = if (c >= gridSize - 5) c - (gridSize - 5) else c
                                    localR == 0 || localR == 4 || localC == 0 || localC == 4 || (localR == 2 && localC == 2)
                                } else {
                                    ((r * 31 + c * 17 + seed) % 7) < 3
                                }

                                if (shouldDraw) {
                                    drawRect(
                                        color = CharcoalDark,
                                        topLeft = Offset(c * stepX, r * stepY),
                                        size = Size(stepX * 0.95f, stepY * 0.95f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6-Digit Passcode Display (Solid Black Box matching screenshot)
                Text(
                    text = "6-DIGIT ENTRY PASSCODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = Color(0xFF111111),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = preApproval.passcode,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = JetBrainsMono,
                        color = Color.White,
                        letterSpacing = 6.sp,
                        modifier = Modifier.padding(horizontal = 26.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Valid: ${preApproval.validDate} (${preApproval.validTimeSlot})",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Share and Download Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Passcode ${preApproval.passcode} shared via SMS / WhatsApp!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CharcoalDark)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp), tint = CharcoalDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = CharcoalDark, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "GatePass ticket saved to gallery!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Guard Visitor Registration Form with Camera Capture simulation
@Composable
fun VisitorRegistrationDialog(
    category: VisitorCategory,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, wing: String, flat: String, company: String, vehicle: String) -> Unit
) {
    var visitorName by remember { mutableStateOf("") }
    var visitorPhone by remember { mutableStateOf("") }
    var selectedWing by remember { mutableStateOf("B") }
    var selectedFlat by remember { mutableStateOf("204") }
    var companyName by remember {
        mutableStateOf(
            when (category) {
                VisitorCategory.DELIVERY -> "Zomato"
                VisitorCategory.CAB -> "Ola"
                VisitorCategory.SERVICE -> "Urban Company"
                else -> ""
            }
        )
    }
    var vehicleNumber by remember { mutableStateOf("") }
    var hasPhotoCaptured by remember { mutableStateOf(false) }

    val wings = listOf("A", "B", "C", "D")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CharcoalDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(category),
                                contentDescription = category.label,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "New ${category.label} Entry",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Camera Snapshot Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasPhotoCaptured) EmeraldGreenLight else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (hasPhotoCaptured) EmeraldGreen else BorderGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { hasPhotoCaptured = !hasPhotoCaptured }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (hasPhotoCaptured) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = if (hasPhotoCaptured) EmeraldGreen else CharcoalDark,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hasPhotoCaptured) "✓ Visitor Photo Captured" else "Tap to Capture Visitor Photo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasPhotoCaptured) EmeraldGreen else TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name Field
                OutlinedTextField(
                    value = visitorName,
                    onValueChange = { visitorName = it },
                    label = { Text("${category.label} Name") },
                    placeholder = { Text("e.g. Rahul Sharma") },
                    modifier = Modifier.fillMaxWidth().testTag("visitor_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Field
                OutlinedTextField(
                    value = visitorPhone,
                    onValueChange = { visitorPhone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+91 98765 43210") },
                    modifier = Modifier.fillMaxWidth().testTag("visitor_phone_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                if (category == VisitorCategory.DELIVERY || category == VisitorCategory.SERVICE) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company / Service Name") },
                        placeholder = { Text("e.g. Swiggy, Amazon, UNS & Co.") },
                        modifier = Modifier.fillMaxWidth().testTag("company_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                if (category == VisitorCategory.CAB) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Cab Company") },
                        placeholder = { Text("Ola / Uber / Rapido") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Last 4 Digits of Cab Number") },
                        placeholder = { Text("e.g. 4425") },
                        modifier = Modifier.fillMaxWidth().testTag("cab_number_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wing & Flat selector
                Text(
                    text = "Destination Residence",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Wing", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            wings.forEach { w ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedWing == w) CharcoalDark else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedWing = w }
                                ) {
                                    Text(
                                        text = w,
                                        color = if (selectedWing == w) Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Flat Number", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("101", "204", "301").forEach { f ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedFlat == f) RoyalViolet else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedFlat = f }
                                ) {
                                    Text(
                                        text = f,
                                        color = if (selectedFlat == f) Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (visitorName.isNotBlank()) {
                            onSubmit(visitorName, visitorPhone, selectedWing, selectedFlat, companyName, vehicleNumber)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_visitor_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                    enabled = visitorName.isNotBlank()
                ) {
                    Text(
                        text = "Notify Flat $selectedWing-$selectedFlat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Quick QR Scanner Simulation Dialog
@Composable
fun QrScannerDialog(
    onDismiss: () -> Unit,
    onScanned: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SlateHeader),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gate QR Scanner",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(2.dp, EmeraldGreen, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanning",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Align Visitor QR Ticket",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onScanned("541142")
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("simulate_qr_scan_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("Simulate Verified Scan (PIN 541142)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
