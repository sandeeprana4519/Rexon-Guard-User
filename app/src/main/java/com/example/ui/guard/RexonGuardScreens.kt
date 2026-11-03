package com.example.ui.guard

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RexonRepository
import com.example.data.models.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Rexon Guard App: 100% faithful to user reference screenshots (GATEAPP GUARD)
 */
@Composable
fun RexonGuardApp(
    activeAlerts: List<SecurityAlert>,
    onResolveAlert: (String) -> Unit
) {
    var guardNavTab by remember { mutableStateOf(0) } // 0: Home, 1: In-Out, 2: Message
    var showProfileScreen by remember { mutableStateOf(false) }
    var activeRegistrationCategory by remember { mutableStateOf<VisitorCategory?>(null) }
    var showQrScannerDialog by remember { mutableStateOf(false) }
    var showGateMenu by remember { mutableStateOf(false) }
    var isGuardLoggedIn by remember { mutableStateOf(true) }
    var authStep by remember { mutableStateOf(0) } // 0: Sign In, 1: Verification

    val gateLogs by RexonRepository.gateLogs.collectAsState()
    val guardMessages by RexonRepository.guardMessages.collectAsState()
    val guardProfile by RexonRepository.guardProfile.collectAsState()

    val context = LocalContext.current

    if (!isGuardLoggedIn) {
        if (authStep == 0) {
            GateAppGuardSignInScreen(
                onContinue = {
                    authStep = 1
                }
            )
        } else {
            GateAppGuardVerificationScreen(
                onBack = { authStep = 0 },
                onSubmit = {
                    isGuardLoggedIn = true
                    authStep = 0
                    Toast.makeText(context, "Welcome back, Marc Swanson!", Toast.LENGTH_SHORT).show()
                }
            )
        }
        return
    }

    if (showProfileScreen) {
        GuardProfileView(
            guardProfile = guardProfile,
            onBack = { showProfileScreen = false },
            onLogout = {
                showProfileScreen = false
                isGuardLoggedIn = false
                authStep = 0
                Toast.makeText(context, "Logged out of Gate Session", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    if (activeRegistrationCategory != null) {
        VisitorRegistrationScreen(
            category = activeRegistrationCategory!!,
            onBack = { activeRegistrationCategory = null },
            onSubmit = { name, phone, wing, flat, company, vehicle ->
                RexonRepository.registerVisitor(
                    name = name,
                    phone = phone,
                    category = activeRegistrationCategory!!,
                    wing = wing,
                    flatNumber = flat,
                    companyName = company,
                    vehicleNumber = vehicle
                )
                Toast.makeText(context, "Visitor entry recorded for $wing-$flat", Toast.LENGTH_SHORT).show()
                activeRegistrationCategory = null
                guardNavTab = 1 // Switch to In-Out screen to see waiting log
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Emergency Alert Bar if active
        EmergencyAlertBar(alerts = activeAlerts, onResolve = onResolveAlert)

        // Top Modern Obsidian Header Bar with Navigation Dock (High Contrast & Clear Typography)
        Surface(
            color = Color(0xFF0F172A),
            contentColor = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Gate title & 3 dots menu with High-Contrast Live Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Prominent Gate Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF93C5FD),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = guardProfile.assignedGate,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Box {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { showGateMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showGateMenu,
                            onDismissRequest = { showGateMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("My Profile", fontWeight = FontWeight.SemiBold, color = Color.Black) },
                                onClick = {
                                    showGateMenu = false
                                    showProfileScreen = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Gate Sign In / OTP Screen", color = Color.Black) },
                                onClick = {
                                    showGateMenu = false
                                    isGuardLoggedIn = false
                                    authStep = 0
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Gate A, West Corner", color = Color.Black) },
                                onClick = {
                                    showGateMenu = false
                                    RexonRepository.updateGuardGate("Gate A, West Corner")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Gate B, East Plaza", color = Color.Black) },
                                onClick = {
                                    showGateMenu = false
                                    RexonRepository.updateGuardGate("Gate B, East Plaza")
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Modern Navigation Dock: Home, In-Out, Message with pure white active pill & bright inactive tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        Triple(0, "Home", Icons.Default.Home),
                        Triple(1, "In-Out", Icons.Default.SwapVert),
                        Triple(2, "Message", Icons.Default.Mail)
                    )

                    tabs.forEach { (index, title, icon) ->
                        val isSelected = guardNavTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color.White else Color.Transparent
                                )
                                .clickable { guardNavTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                )
                                if (index == 1) {
                                    val waitingCount = gateLogs.count { it.status == GateLogStatus.WAITING }
                                    if (waitingCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFFEF4444) else Color(0xFFEF4444))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$waitingCount",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Screen Body matching exact tab selected
        when (guardNavTab) {
            0 -> GuardHomeScreen(
                onRegisterCategory = { activeRegistrationCategory = it },
                onScanQr = { showQrScannerDialog = true }
            )
            1 -> GuardInOutScreen(
                gateLogs = gateLogs,
                onPermitIn = { logId ->
                    RexonRepository.markGateLogStatus(logId, GateLogStatus.INSIDE)
                    Toast.makeText(context, "Visitor allowed IN", Toast.LENGTH_SHORT).show()
                },
                onMarkOut = { logId ->
                    RexonRepository.markGateLogStatus(logId, GateLogStatus.EXITED)
                    Toast.makeText(context, "Visitor checked OUT", Toast.LENGTH_SHORT).show()
                }
            )
            2 -> GuardMessagesScreen(messages = guardMessages)
        }
    }

    if (showQrScannerDialog) {
        QrScannerDialog(
            onDismiss = { showQrScannerDialog = false },
            onScanned = { pin ->
                val (success, message) = RexonRepository.verifyPasscode(pin)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (success) {
                    guardNavTab = 1
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 1. GUARD HOME SCREEN (DIALPAD & VISITOR CARDS)
// -------------------------------------------------------------
data class GuardKeypadKey(
    val main: String,
    val sub: String = ""
)

@Composable
fun GuardHomeScreen(
    onRegisterCategory: (VisitorCategory) -> Unit,
    onScanQr: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Passcode Label
        Text(
            text = "ENTER 6-DIGIT VISITOR PASSCODE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // PIN Display Container (Tactile 6-slot modern card)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val digit = enteredPin.getOrNull(i)?.toString()
                    val isFilled = digit != null
                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isFilled) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isFilled) 1.5.dp else 1.dp,
                                color = if (isFilled) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (digit != null) {
                            Text(
                                text = digit,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = PlusJakartaSans,
                                color = Color(0xFF0F172A)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(3.dp)
                                    .background(Color(0xFF94A3B8), CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3x4 Modern Dialpad with high contrast tactile buttons
        val dialpadRows = listOf(
            listOf(GuardKeypadKey("1", ""), GuardKeypadKey("2", "ABC"), GuardKeypadKey("3", "DEF")),
            listOf(GuardKeypadKey("4", "GHI"), GuardKeypadKey("5", "JKL"), GuardKeypadKey("6", "MNO")),
            listOf(GuardKeypadKey("7", "PQRS"), GuardKeypadKey("8", "TUV"), GuardKeypadKey("9", "WXYZ")),
            listOf(GuardKeypadKey("QR", "SCAN"), GuardKeypadKey("0", "+"), GuardKeypadKey("DEL", ""))
        )

        dialpadRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { keyInfo ->
                    val key = keyInfo.main
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 7.dp)
                            .size(width = 78.dp, height = 62.dp)
                            .clickable {
                                when (key) {
                                    "QR" -> onScanQr()
                                    "DEL" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                    else -> {
                                        if (enteredPin.length < 6) {
                                            val next = enteredPin + key
                                            enteredPin = next
                                            if (next.length == 6) {
                                                val (success, msg) = RexonRepository.verifyPasscode(next)
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                if (success) enteredPin = ""
                                            }
                                        }
                                    }
                                }
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (key) {
                                "QR" -> Color(0xFF6C5CE7)
                                "DEL" -> Color(0xFFFFF1F2)
                                else -> Color.White
                            }
                        ),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = when (key) {
                                "QR" -> Color(0xFF5843E6)
                                "DEL" -> Color(0xFFFECDD3)
                                else -> Color(0xFFE2E8F0)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (key) {
                                "QR" -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = "QR Scanner",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "SCAN",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                                "DEL" -> {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = Color(0xFFE11D48),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                else -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = key,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = PlusJakartaSans,
                                            color = Color(0xFF0F172A)
                                        )
                                        if (keyInfo.sub.isNotEmpty()) {
                                            Text(
                                                text = keyInfo.sub,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF64748B),
                                                letterSpacing = 1.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "ADD NEW VISITOR" section header with clear icon and subtitle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ADD NEW VISITOR",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = PlusJakartaSans,
                        color = Color(0xFF0F172A),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Select category to register entry",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "4 Categories",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Structured Category Cards (Guest & Delivery on top, Service & Cab below)
        // Perfectly fits screen width, NO horizontal scrolling or cut-offs!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Guest Card
            CategoryActionCard(
                category = VisitorCategory.GUEST,
                tagColor = Color(0xFF6C5CE7),
                tagBackground = Color(0xFFF3F0FF),
                onClick = { onRegisterCategory(VisitorCategory.GUEST) },
                modifier = Modifier.weight(1f)
            )

            // Delivery Card
            CategoryActionCard(
                category = VisitorCategory.DELIVERY,
                tagColor = Color(0xFFD97706),
                tagBackground = Color(0xFFFEF3C7),
                onClick = { onRegisterCategory(VisitorCategory.DELIVERY) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Service Card
            CategoryActionCard(
                category = VisitorCategory.SERVICE,
                tagColor = Color(0xFF059669),
                tagBackground = Color(0xFFD1FAE5),
                onClick = { onRegisterCategory(VisitorCategory.SERVICE) },
                modifier = Modifier.weight(1f)
            )

            // Cab Card
            CategoryActionCard(
                category = VisitorCategory.CAB,
                tagColor = Color(0xFF0284C7),
                tagBackground = Color(0xFFE0F2FE),
                onClick = { onRegisterCategory(VisitorCategory.CAB) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Modern High-Contrast 3D Category Card for 2x2 Grid
 */
@Composable
fun CategoryActionCard(
    category: VisitorCategory,
    tagColor: Color,
    tagBackground: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(138.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.label,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF0F172A)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(tagBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+ ADD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CategoryLogoBadge(
                    category = category,
                    modifier = Modifier.size(68.dp),
                    cornerRadius = 14.dp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. VISITOR REGISTRATION FULL-SCREEN FORM (Screenshots 9-14)
// -------------------------------------------------------------
@Composable
fun VisitorRegistrationScreen(
    category: VisitorCategory,
    onBack: () -> Unit,
    onSubmit: (name: String, phone: String, wing: String, flat: String, company: String, vehicle: String) -> Unit
) {
    var name by remember {
        mutableStateOf(
            when (category) {
                VisitorCategory.GUEST -> "George Smith"
                VisitorCategory.DELIVERY -> "Paul Williamson"
                VisitorCategory.CAB -> ""
                VisitorCategory.SERVICE -> "Jennop Anderson"
            }
        )
    }
    var companyName by remember {
        mutableStateOf(
            when (category) {
                VisitorCategory.DELIVERY -> "Arrozone"
                VisitorCategory.CAB -> "Olas Cab"
                VisitorCategory.SERVICE -> "UNS & Co."
                else -> ""
            }
        )
    }
    var cabLastDigits by remember { mutableStateOf("4425") }
    var building by remember { mutableStateOf("A Wing") }
    var flatNumber by remember { mutableStateOf("103") }

    var showBuildingMenu by remember { mutableStateOf(false) }
    var showFlatMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Top Back Arrow
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Avatar with camera overlay (Matching Screenshot 9, 10, 11, 12, 13, 14)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${category.label} Entry",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF1E293B)
            )

            // Circular avatar with camera icon overlay
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                VisitorIllustrationAvatar(
                    category = category,
                    modifier = Modifier.size(56.dp)
                )

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Form Fields (Clean Gray Label, Bold Black Text, Line Underneath - Exact Match)
        when (category) {
            VisitorCategory.GUEST -> {
                CleanUnderlinedInputField(
                    label = "Enter Guest Name",
                    value = name,
                    onValueChange = { name = it }
                )
            }
            VisitorCategory.DELIVERY -> {
                CleanUnderlinedInputField(
                    label = "Deliveryman Name",
                    value = name,
                    onValueChange = { name = it }
                )
                Spacer(modifier = Modifier.height(20.dp))
                CleanUnderlinedInputField(
                    label = "Company Name",
                    value = companyName,
                    onValueChange = { companyName = it }
                )
            }
            VisitorCategory.CAB -> {
                CleanUnderlinedInputField(
                    label = "Last 4 Digit of Cab",
                    value = cabLastDigits,
                    onValueChange = { cabLastDigits = it }
                )
                Spacer(modifier = Modifier.height(20.dp))
                CleanUnderlinedInputField(
                    label = "Company Name",
                    value = companyName,
                    onValueChange = { companyName = it }
                )
            }
            VisitorCategory.SERVICE -> {
                CleanUnderlinedInputField(
                    label = "Servicesman Name",
                    value = name,
                    onValueChange = { name = it }
                )
                Spacer(modifier = Modifier.height(20.dp))
                CleanUnderlinedInputField(
                    label = "Company Name",
                    value = companyName,
                    onValueChange = { companyName = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Building Dropdown
        CleanUnderlinedDropdownField(
            label = "Building",
            value = building,
            onClick = { showBuildingMenu = true }
        )
        DropdownMenu(
            expanded = showBuildingMenu,
            onDismissRequest = { showBuildingMenu = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOf("A Wing", "B Wing", "C Wing").forEach { w ->
                DropdownMenuItem(
                    text = { Text(w, color = Color.Black) },
                    onClick = {
                        building = w
                        showBuildingMenu = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Flat Number Dropdown
        CleanUnderlinedDropdownField(
            label = "Flat Number",
            value = flatNumber,
            onClick = { showFlatMenu = true }
        )
        DropdownMenu(
            expanded = showFlatMenu,
            onDismissRequest = { showFlatMenu = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOf("103", "202", "204", "302", "308").forEach { f ->
                DropdownMenuItem(
                    text = { Text(f, color = Color.Black) },
                    onClick = {
                        flatNumber = f
                        showFlatMenu = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue Button (Solid black, rounded, full-width)
        Button(
            onClick = {
                val finalName = if (category == VisitorCategory.CAB) "Cab $cabLastDigits" else name
                onSubmit(finalName, "+91 98765 00000", building.take(1), flatNumber, companyName, cabLastDigits)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// -------------------------------------------------------------
// 3. IN-OUT SCREEN (Screenshots 3 & 4)
// -------------------------------------------------------------
@Composable
fun GuardInOutScreen(
    gateLogs: List<GateLog>,
    onPermitIn: (String) -> Unit,
    onMarkOut: (String) -> Unit
) {
    var subTab by remember { mutableStateOf(0) } // 0: Watting, 1: Inside

    val waitingList = gateLogs.filter { it.status == GateLogStatus.WAITING }
    val insideList = gateLogs.filter { it.status == GateLogStatus.INSIDE }

    Column(modifier = Modifier.fillMaxSize()) {
        // Subtabs: Watting (Count) vs Inside (Count) (Exact match with screenshot 3 & 4)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC))
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Waiting Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { subTab = 0 }
            ) {
                Text(
                    text = "Waiting (${waitingList.size})",
                    fontSize = 16.sp,
                    fontWeight = if (subTab == 0) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (subTab == 0) Color(0xFF0F172A) else Color(0xFF64748B),
                    fontFamily = PlusJakartaSans
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(3.dp)
                        .background(if (subTab == 0) Color(0xFF0F172A) else Color.Transparent, CircleShape)
                )
            }

            // Inside Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { subTab = 1 }
            ) {
                Text(
                    text = "Inside (${insideList.size})",
                    fontSize = 16.sp,
                    fontWeight = if (subTab == 1) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (subTab == 1) Color(0xFF0F172A) else Color(0xFF64748B),
                    fontFamily = PlusJakartaSans
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(3.dp)
                        .background(if (subTab == 1) Color(0xFF0F172A) else Color.Transparent, CircleShape)
                )
            }
        }

        // List Content
        val currentList = if (subTab == 0) waitingList else insideList

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(currentList, key = { it.id }) { log ->
                InOutListItem(
                    log = log,
                    isWaiting = subTab == 0,
                    onPermitIn = { onPermitIn(log.id) },
                    onMarkOut = { onMarkOut(log.id) }
                )
                Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun InOutListItem(
    log: GateLog,
    isWaiting: Boolean,
    onPermitIn: () -> Unit,
    onMarkOut: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // 3D Avatar Illustration matching screenshot
            VisitorIllustrationAvatar(
                category = log.category,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = log.visitorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${log.category.label}  •  ${log.wing} ${log.flatNumber}",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Button matching screenshot: Green IN, Gray Clock, or Red OUT
        if (isWaiting) {
            if (log.id.endsWith("2") || log.category == VisitorCategory.DELIVERY) {
                // Gray clock pending icon button (Screenshot 4)
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFCBD5E1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Pending",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                // Green IN button (Screenshot 4)
                Button(
                    onClick = onPermitIn,
                    modifier = Modifier
                        .width(72.dp)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "IN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            // Red OUT button (Screenshot 3)
            Button(
                onClick = onMarkOut,
                modifier = Modifier
                    .width(72.dp)
                    .height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "OUT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. MESSAGE SCREEN (Screenshot 2)
// -------------------------------------------------------------
@Composable
fun GuardMessagesScreen(messages: List<GuardMessage>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(messages, key = { it.id }) { msg ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Resident Avatar circle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFBCFE8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = msg.senderName,
                            tint = Color(0xFF9D174D),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = msg.senderName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${msg.wing} ${msg.flatNumber}  •  ${msg.timeElapsed}",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = msg.message,
                    fontSize = 14.sp,
                    color = Color(0xFF334155),
                    lineHeight = 20.sp
                )
            }
            Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }
    }
}

// -------------------------------------------------------------
// 5. PROFILE SCREEN (Screenshot 7)
// -------------------------------------------------------------
@Composable
fun GuardProfileView(
    guardProfile: GuardProfile,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Top Back Arrow
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = "Everthing about you",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Guard photo with camera badge (Screenshot 7)
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Guard Photo",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(42.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        CleanUnderlinedInputField(
            label = "Full Name",
            value = "Marc Swanson",
            onValueChange = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        CleanUnderlinedInputField(
            label = "Phone Number",
            value = "987 654 3210",
            onValueChange = {}
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Logout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// Reusable clean underlined input field matching screenshots
@Composable
fun CleanUnderlinedInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.ifBlank { "Enter $label" },
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (value.isNotBlank()) Color(0xFF1E293B) else Color(0xFFCBD5E1)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
    }
}

@Composable
fun CleanUnderlinedDropdownField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
    }
}

// -------------------------------------------------------------
// 6. GATEAPP GUARD SIGN IN SCREEN (Screenshot 1)
// -------------------------------------------------------------
@Composable
fun GateAppGuardSignInScreen(
    onContinue: () -> Unit
) {
    var country by remember { mutableStateOf("India (+91)") }
    var phoneNumber by remember { mutableStateOf("987 654 3210") }
    var showCountryPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Center GateApp Monogram & Brand (Exact Screenshot 1)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            GateAppBrandEmblem(subtitle = "GUARD")
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Hey mate,",
            fontSize = 16.sp,
            color = Color(0xFF94A3B8),
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Sign in Now",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            fontFamily = PlusJakartaSans
        )

        Spacer(modifier = Modifier.height(34.dp))

        // Select Country
        CleanUnderlinedDropdownField(
            label = "Select Country",
            value = country,
            onClick = { showCountryPicker = true }
        )

        DropdownMenu(
            expanded = showCountryPicker,
            onDismissRequest = { showCountryPicker = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOf("India (+91)", "United States (+1)", "United Kingdom (+44)", "UAE (+971)").forEach { c ->
                DropdownMenuItem(
                    text = { Text(c, color = Color.Black) },
                    onClick = {
                        country = c
                        showCountryPicker = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Phone Number
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Phone Number",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium,
                fontFamily = PlusJakartaSans
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Enter Phone Number", color = Color(0xFFCBD5E1)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1E1E1E),
                    unfocusedTextColor = Color(0xFF1E1E1E),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = PlusJakartaSans
            )
        }
    }
}

// -------------------------------------------------------------
// 7. GATEAPP GUARD OTP VERIFICATION SCREEN (Screenshot 5)
// -------------------------------------------------------------
@Composable
fun GateAppGuardVerificationScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    var otpCode by remember { mutableStateOf("645770") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Back arrow (Screenshot 5)
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Enter verification code sent on given number",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            fontFamily = PlusJakartaSans
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Enter 6 digit OTP",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(6.dp))

        // OTP with 6 digits (Screenshot 5 shows "6 4 5 7 7 0")
        OutlinedTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6) otpCode = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                color = Color(0xFF1E1E1E),
                fontFamily = JetBrainsMono
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Submit",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = PlusJakartaSans
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "0:25 min left",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                fontFamily = PlusJakartaSans
            )

            Text(
                text = "RESEND",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.clickable {
                    otpCode = "645770"
                }
            )
        }
    }
}

