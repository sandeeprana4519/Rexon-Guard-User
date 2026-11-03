package com.example.ui.user

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.RexonRepository
import com.example.data.models.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Rexon Resident / User Application Root Composable.
 * Matches 100% of the user-provided reference screenshots:
 * - Clean pure white top header with notification badge "2", GeteApp logo, profile avatar
 * - 4-Tab Bottom Dock: Home, Activity, Social, Service in solid obsidian black (#111111)
 * - Floating Purple Shield button for instant SOS & quick actions
 * - 4 Pre-approval cards: Add Delivery, Add Service, Add Cab, Add Guest with 3D characters
 * - 4 Community cards: HelpDesk, Notice Board, Do society payment, Book Amenities
 * - Authentic GatePass ticket with perforated line & solid black 6-digit PIN box
 * - Complete Social screen (Post feed, Direct Chats, Residents directory by Wings)
 * - Notice Board matching exact screenshot (Announcement, green megaphone, reaction counters)
 */
@Composable
fun RexonUserApp(
    activeAlerts: List<SecurityAlert>,
    onResolveAlert: (String) -> Unit
) {
    var userMainTab by remember { mutableStateOf(0) } // 0: Home, 1: Activity, 2: Social, 3: Service
    var activeSubScreen by remember { mutableStateOf<String?>(null) } // "notices", "helpdesk", "payments", "amenities", "notifications", "profile"
    var showPreApprovalModalFor by remember { mutableStateOf<VisitorCategory?>(null) }
    var selectedGatePassToView by remember { mutableStateOf<PreApproval?>(null) }
    var showPurpleShieldModal by remember { mutableStateOf(false) }
    var testGateCallActive by remember { mutableStateOf(false) }

    val userProfile by RexonRepository.userProfile.collectAsState()
    val gateLogs by RexonRepository.gateLogs.collectAsState()
    val preApprovals by RexonRepository.preApprovals.collectAsState()
    val dailyHelps by RexonRepository.dailyHelps.collectAsState()
    val incomingGateCall by RexonRepository.incomingGateCall.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            if (activeSubScreen != null) {
                Surface(
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { activeSubScreen = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF0F172A)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (activeSubScreen) {
                                "notices" -> "Notices Board"
                                "helpdesk" -> "HelpDesk Complaints"
                                "payments" -> "Society Payments"
                                "amenities" -> "Book Amenities"
                                "notifications" -> "Notifications"
                                "profile" -> "My Profile & Household"
                                else -> "GeteApp"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            } else {
                DedicatedResidentHeader(
                    userProfile = userProfile,
                    unreadCount = 2,
                    onOpenNotifications = { activeSubScreen = "notifications" },
                    onOpenProfile = { activeSubScreen = "profile" }
                )
            }
        },
        floatingActionButton = {
            if (activeSubScreen == null) {
                FloatingPurpleShieldButton(onClick = { showPurpleShieldModal = true })
            }
        },
        bottomBar = {
            if (activeSubScreen == null) {
                UserBottomNavigationBar(
                    selectedTab = userMainTab,
                    onTabSelect = { userMainTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Emergency Alert Bar if active
                EmergencyAlertBar(alerts = activeAlerts, onResolve = onResolveAlert)

                if (activeSubScreen != null) {
                    when (activeSubScreen) {
                        "notices" -> UserNoticeBoardScreen(onBack = { activeSubScreen = null })
                        "helpdesk" -> UserHelpDeskScreen(onBack = { activeSubScreen = null })
                        "payments" -> UserPaymentsScreen(onBack = { activeSubScreen = null })
                        "amenities" -> UserAmenitiesScreen(onBack = { activeSubScreen = null })
                        "notifications" -> UserNotificationsScreen(onBack = { activeSubScreen = null })
                        "profile" -> UserHouseholdScreen(onBack = { activeSubScreen = null })
                    }
                } else {
                    when (userMainTab) {
                        0 -> UserDashboardHome(
                            userProfile = userProfile,
                            preApprovals = preApprovals,
                            gateLogs = gateLogs,
                            onPreApprove = { cat -> showPreApprovalModalFor = cat },
                            onViewGatePass = { pass -> selectedGatePassToView = pass },
                            onNavigateToSubScreen = { sub -> activeSubScreen = sub },
                            onTriggerDemoCall = { testGateCallActive = true }
                        )
                        1 -> UserActivityLogsScreen(
                            gateLogs = gateLogs.filter { it.wing == userProfile.wing && it.flatNumber == userProfile.flatNumber },
                            preApprovals = preApprovals,
                            dailyHelps = dailyHelps,
                            onViewGatePass = { pass -> selectedGatePassToView = pass }
                        )
                        2 -> UserSocialScreen()
                        3 -> UserServicesHubScreen()
                    }
                }
            }

            // Purple Shield Bottom Sheet Modal
            if (showPurpleShieldModal) {
                PurpleShieldActionModal(
                    onDismiss = { showPurpleShieldModal = false },
                    onSendMessage = { message, recipient ->
                        RexonRepository.sendInstructionToGuard(message, recipient)
                    },
                    onTriggerSos = { alertType ->
                        RexonRepository.triggerSecurityAlert(alertType)
                    }
                )
            }

            // Pre-Approval Creation Modal (Clean dialog with top-right 3D character badge)
            showPreApprovalModalFor?.let { category ->
                UserPreApproveModal(
                    category = category,
                    onDismiss = { showPreApprovalModalFor = null },
                    onCreated = { preApproval ->
                        showPreApprovalModalFor = null
                        selectedGatePassToView = preApproval
                        Toast.makeText(context, "GatePass created with PIN ${preApproval.passcode}!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // GatePass Ticket Viewer
            selectedGatePassToView?.let { pass ->
                GatePassTicketDialog(
                    preApproval = pass,
                    onDismiss = { selectedGatePassToView = null }
                )
            }

            // Full-screen incoming video call alert (real or simulated)
            if (testGateCallActive) {
                IncomingGateCallDialog(
                    callLog = GateLog(
                        id = "DEMO-CALL",
                        visitorName = "Bruno Stevenson",
                        visitorPhone = "+91 98765 44332",
                        category = VisitorCategory.GUEST,
                        wing = "B",
                        flatNumber = "254",
                        entryTime = System.currentTimeMillis()
                    ),
                    onAccept = {
                        testGateCallActive = false
                        Toast.makeText(context, "Gate call accepted! Entry allowed for Bruno Stevenson.", Toast.LENGTH_SHORT).show()
                    },
                    onDeny = {
                        testGateCallActive = false
                        Toast.makeText(context, "Entry denied.", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            incomingGateCall?.let { callLog ->
                IncomingGateCallDialog(
                    callLog = callLog,
                    onAccept = {
                        RexonRepository.acceptIncomingCall(callLog.id)
                        Toast.makeText(context, "Gate opened for ${callLog.visitorName}!", Toast.LENGTH_SHORT).show()
                    },
                    onDeny = {
                        RexonRepository.denyIncomingCall(callLog.id)
                        Toast.makeText(context, "Entry denied.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// USER DASHBOARD HOME SCREEN
// -------------------------------------------------------------
@Composable
fun UserDashboardHome(
    userProfile: UserProfile,
    preApprovals: List<PreApproval>,
    gateLogs: List<GateLog>,
    onPreApprove: (VisitorCategory) -> Unit,
    onViewGatePass: (PreApproval) -> Unit,
    onNavigateToSubScreen: (String) -> Unit,
    onTriggerDemoCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Resident Welcome & Society Header Card
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hey ${userProfile.name.split(" ").firstOrNull() ?: "Alex"},",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${userProfile.societyName} • Wing ${userProfile.wing}, ${userProfile.flatNumber}",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gate A Active",
                                color = Color(0xFF0F172A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Demo Banner: Tap to test Gate Call matching screenshot
                Surface(
                    color = Color(0xFF111111),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTriggerDemoCall() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneInTalk,
                                    contentDescription = "Test Call",
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Gate Intercom Live",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontFamily = PlusJakartaSans
                                )
                                Text(
                                    text = "Tap to test incoming visitor call",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = PlusJakartaSans
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFF22C55E),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TEST CALL",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontFamily = PlusJakartaSans
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pre-Approve Visitor Section (4 Cards with 3D character pictures on pale cyan cards)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRE-APPROVE VISITOR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFF64748B),
                    fontFamily = PlusJakartaSans
                )
                Text(
                    text = "Instant 6-Digit PIN",
                    fontSize = 12.sp,
                    color = RoyalViolet,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Distinct Cards: Add Delivery, Add Service, Add Cab, Add Guest
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple(VisitorCategory.DELIVERY, "Add Delivery", R.drawable.ic_category_delivery),
                    Triple(VisitorCategory.SERVICE, "Add Service", R.drawable.ic_category_service),
                    Triple(VisitorCategory.CAB, "Add Cab", R.drawable.ic_category_cab),
                    Triple(VisitorCategory.GUEST, "Add Guest", R.drawable.ic_category_guest)
                ).forEach { (category, title, drawableRes) ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .width(100.dp)
                            .testTag("pre_approve_${category.name.lowercase()}")
                            .clickable { onPreApprove(category) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = drawableRes),
                                contentDescription = title,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center,
                                fontFamily = PlusJakartaSans
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Community Operations Grid Cards (4 pale cyan cards: HelpDesk, Notice Board, Do society payment, Book Amenities)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "COMMUNITY",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = Color(0xFF64748B),
                fontFamily = PlusJakartaSans
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResidentCommunityCard(
                    title = "HelpDesk",
                    subtitle = "Raise a ticket & complaint",
                    icon = Icons.Default.SupportAgent,
                    badge = "1 Open",
                    badgeColor = AmberPending,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSubScreen("helpdesk") }
                )
                ResidentCommunityCard(
                    title = "Notice Board",
                    subtitle = "Important society notices",
                    icon = Icons.Default.Campaign,
                    badge = "2 New",
                    badgeColor = RoyalViolet,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSubScreen("notices") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResidentCommunityCard(
                    title = "Do society payment",
                    subtitle = "Pay maintenance & dues",
                    icon = Icons.Default.AccountBalanceWallet,
                    badge = "₹4,500 Due",
                    badgeColor = CrimsonRed,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSubScreen("payments") }
                )
                ResidentCommunityCard(
                    title = "Book Amenities",
                    subtitle = "Clubhouse, Pool & Sports",
                    icon = Icons.Default.FitnessCenter,
                    badge = "4 Available",
                    badgeColor = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSubScreen("amenities") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Active Digital GatePasses
        if (preApprovals.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE GATEPASS PASSES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontFamily = PlusJakartaSans
                    )
                    Text(
                        text = "${preApprovals.size} Generated",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PlusJakartaSans
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                preApprovals.forEach { pass ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onViewGatePass(pass) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = getCategoryDrawableRes(pass.category)),
                                    contentDescription = pass.category.label,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = pass.visitorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0F172A),
                                        fontFamily = PlusJakartaSans
                                    )
                                    Text(
                                        text = "PIN: ${pass.passcode} • ${pass.validDate}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = PlusJakartaSans
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF111111),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "View Ticket",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PlusJakartaSans,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// Pale Cyan Community Card Component matching screenshots
@Composable
private fun ResidentCommunityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                fontFamily = PlusJakartaSans
            )
        }
    }
}

// -------------------------------------------------------------
// USER BOTTOM NAVIGATION BAR (Solid Obsidian Black Bar)
// -------------------------------------------------------------
@Composable
fun UserBottomNavigationBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit
) {
    Surface(
        color = Color(0xFF111111),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                Triple(0, "Home", Icons.Default.Home),
                Triple(1, "Activity", Icons.Default.History),
                Triple(2, "Social", Icons.Default.ChatBubbleOutline),
                Triple(3, "Service", Icons.Default.GridView)
            )

            items.forEach { (index, label, icon) ->
                val isSelected = selectedTab == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelect(index) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp),
                        tint = if (isSelected) Color.White else Color(0xFF71717A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF71717A),
                        fontFamily = PlusJakartaSans
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// USER ACTIVITY LOGS SCREEN (Today & Upcoming Tabs)
// -------------------------------------------------------------
@Composable
fun UserActivityLogsScreen(
    gateLogs: List<GateLog>,
    preApprovals: List<PreApproval>,
    dailyHelps: List<DailyHelp>,
    onViewGatePass: (PreApproval) -> Unit
) {
    var activityTab by remember { mutableStateOf(0) } // 0: Today, 1: Upcoming
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = activityTab,
            containerColor = Color.White,
            contentColor = Color(0xFF0F172A),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activityTab]),
                    color = Color.Black,
                    height = 2.5.dp
                )
            }
        ) {
            Tab(
                selected = activityTab == 0,
                onClick = { activityTab = 0 },
                text = { Text("Today's Activity", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) }
            )
            Tab(
                selected = activityTab == 1,
                onClick = { activityTab = 1 },
                text = { Text("Upcoming Pre-Approvals", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) }
            )
        }

        if (activityTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "DAILY DOMESTIC STAFF TRACKING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontFamily = PlusJakartaSans
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(dailyHelps, key = { it.id }) { help ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (help.isInside) EmeraldGreenLight else Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (help.role.contains("Maid")) Icons.Default.CleaningServices else if (help.role.contains("Cook")) Icons.Default.SoupKitchen else Icons.Default.DirectionsCar,
                                        contentDescription = help.role,
                                        tint = if (help.isInside) EmeraldGreen else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = help.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                    Text(text = "${help.role} • ★ ${help.rating}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (help.isInside) EmeraldGreenLight else Color.White,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (help.isInside) "INSIDE" else "OUTSIDE",
                                        color = if (help.isInside) EmeraldGreen else Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = PlusJakartaSans,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { Toast.makeText(context, "Calling ${help.name} (${help.phone})", Toast.LENGTH_SHORT).show() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0F172A))
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "GATE VISITORS TODAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontFamily = PlusJakartaSans
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (gateLogs.isEmpty()) {
                    item {
                        Text("No visitor activity recorded today yet.", fontSize = 13.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                    }
                } else {
                    items(gateLogs, key = { it.id }) { log ->
                        VisitorLogCard(
                            log = log,
                            onPermitIn = { RexonRepository.markGateLogStatus(log.id, GateLogStatus.INSIDE) },
                            onMarkOut = { RexonRepository.markGateLogStatus(log.id, GateLogStatus.EXITED) }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (preApprovals.isEmpty()) {
                    item {
                        Text("No upcoming pre-approvals created yet.", fontSize = 13.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                    }
                } else {
                    items(preApprovals, key = { it.id }) { pass ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = getCategoryDrawableRes(pass.category)),
                                            contentDescription = pass.category.label,
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(pass.visitorName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                            Text(
                                                text = "${pass.category.label} • ${pass.validDate} (${pass.validTimeSlot})",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B),
                                                fontFamily = PlusJakartaSans
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFF111111),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = pass.passcode,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = JetBrainsMono,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { onViewGatePass(pass) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                                        modifier = Modifier.height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
                                    ) {
                                        Text("Open GatePass QR", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// USER SOCIAL SCREEN (Post, Chat, Residents matching Screenshots)
// -------------------------------------------------------------
@Composable
fun UserSocialScreen() {
    var socialSubTab by remember { mutableStateOf(0) } // 0: Post, 1: Chat, 2: Residents
    val communityPosts by RexonRepository.communityPosts.collectAsState()
    val residentDirectory = RexonRepository.residentDirectory
    val context = LocalContext.current
    var showCreatePostDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = socialSubTab,
            containerColor = Color.White,
            contentColor = Color(0xFF0F172A),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[socialSubTab]),
                    color = Color.Black,
                    height = 2.5.dp
                )
            }
        ) {
            Tab(
                selected = socialSubTab == 0,
                onClick = { socialSubTab = 0 },
                text = { Text("Post", fontWeight = if (socialSubTab == 0) FontWeight.Bold else FontWeight.Medium, fontFamily = PlusJakartaSans) }
            )
            Tab(
                selected = socialSubTab == 1,
                onClick = { socialSubTab = 1 },
                text = { Text("Chat", fontWeight = if (socialSubTab == 1) FontWeight.Bold else FontWeight.Medium, fontFamily = PlusJakartaSans) }
            )
            Tab(
                selected = socialSubTab == 2,
                onClick = { socialSubTab = 2 },
                text = { Text("Residents", fontWeight = if (socialSubTab == 2) FontWeight.Bold else FontWeight.Medium, fontFamily = PlusJakartaSans) }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (socialSubTab) {
                0 -> {
                    // Feed of posts matching screenshots (Emili Williamson, Peter Johnson, etc.)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(communityPosts, key = { it.id }) { post ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE0E7FF)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = post.authorName.take(1),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF4338CA)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = post.authorName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF0F172A),
                                                    fontFamily = PlusJakartaSans
                                                )
                                                Text(
                                                    text = "${post.flatId} • ${post.timestamp}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B),
                                                    fontFamily = PlusJakartaSans
                                                )
                                            }
                                        }

                                        IconButton(onClick = {}) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color(0xFF94A3B8))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = post.content,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1E293B),
                                        lineHeight = 20.sp,
                                        fontFamily = PlusJakartaSans
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { RexonRepository.likeCommunityPost(post.id) }
                                        ) {
                                            Icon(
                                                imageVector = if (post.hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Like",
                                                tint = if (post.hasLiked) Color(0xFFEF4444) else Color(0xFF64748B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${post.likes}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                Toast.makeText(context, "Opening comments...", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChatBubbleOutline,
                                                contentDescription = "Comments",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${post.commentsCount}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                Toast.makeText(context, "Post shared!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Floating purple (+) FAB
                    FloatingActionButton(
                        onClick = { showCreatePostDialog = true },
                        containerColor = RoyalViolet,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 20.dp, end = 20.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Post")
                    }
                }
                1 -> {
                    // Direct Chats list
                    UserChatListScreen()
                }
                2 -> {
                    // Resident directory by Wings
                    UserResidentDirectoryScreen(residentDirectory = residentDirectory)
                }
            }
        }
    }

    if (showCreatePostDialog) {
        CreateCommunityPostDialog(
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { text ->
                RexonRepository.addCommunityPost(text)
                showCreatePostDialog = false
                Toast.makeText(context, "Post published to community!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun UserChatListScreen() {
    val context = LocalContext.current
    val conversations = listOf(
        Triple("Admin Office", "Maintenance receipt generated for Nov", "10m ago"),
        Triple("Security Gate A", "Parcel left at reception by Dunzo", "25m ago"),
        Triple("Peter Johnson (B 108)", "Thanks for the reminder about water tank!", "1h ago"),
        Triple("Rahul Patel (A 202)", "Are you attending society meeting tomorrow?", "3h ago"),
        Triple("Emili Williamson (A 202)", "Sure, let's coordinate parking slots.", "Yesterday")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(conversations) { (name, lastMsg, time) ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, "Chat with $name opened", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = name, tint = Color(0xFF0F172A))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                            Text(time, fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(lastMsg, fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1, fontFamily = PlusJakartaSans)
                    }
                }
            }
        }
    }
}

@Composable
fun UserResidentDirectoryScreen(residentDirectory: List<ResidentDirectoryEntry>) {
    val context = LocalContext.current
    val wings = residentDirectory.groupBy { it.wing }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        wings.forEach { (wing, residents) ->
            item {
                Text(
                    text = "WING $wing RESIDENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFF64748B),
                    fontFamily = PlusJakartaSans
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(residents, key = { it.id }) { resident ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = resident.name.take(1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(resident.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                Text("${resident.fullFlatId} • ${resident.role}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Calling ${resident.name} (${resident.phone})", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Message to ${resident.name} opened", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Chat", tint = RoyalViolet, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// USER SERVICES SCREEN (Your Bookings & 8 Categories Grid)
// -------------------------------------------------------------
@Composable
fun UserServicesHubScreen() {
    var selectedCategoryForBooking by remember { mutableStateOf<String?>(null) }
    val serviceOrders by RexonRepository.serviceOrders.collectAsState()
    val context = LocalContext.current

    val serviceCategories = listOf(
        "Home Cleaning" to Icons.Default.CleaningServices,
        "Appliances Repair" to Icons.Default.Kitchen,
        "Carpenter" to Icons.Default.Construction,
        "Painting" to Icons.Default.FormatPaint,
        "Plumbing" to Icons.Default.Plumbing,
        "Packers & Movers" to Icons.Default.LocalShipping,
        "Sanitize & Pest" to Icons.Default.Sanitizer,
        "Hair & Beauty" to Icons.Default.Face
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "NEARBY SERVICE PROVIDERS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF64748B),
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Grid of 8 Services
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            serviceCategories.chunked(2).forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { (catName, icon) ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCategoryForBooking = catName }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = catName,
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = catName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontFamily = PlusJakartaSans
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "YOUR ACTIVE BOOKINGS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF64748B),
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(10.dp))

        serviceOrders.forEach { order ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Build, contentDescription = order.category, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = order.category, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                Text(text = order.scheduledDate, fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                            }
                        }

                        Surface(
                            color = when (order.status) {
                                "Completed" -> EmeraldGreenLight
                                "Assigned" -> BlueInfoLight
                                else -> AmberPendingLight
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = order.status.uppercase(),
                                color = when (order.status) {
                                    "Completed" -> EmeraldGreen
                                    "Assigned" -> BlueInfo
                                    else -> AmberPending
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = order.problemDescription, fontSize = 12.sp, color = Color(0xFF475569), fontFamily = PlusJakartaSans)
                }
            }
        }
    }

    selectedCategoryForBooking?.let { catName ->
        ServiceBookingDialog(
            category = catName,
            onDismiss = { selectedCategoryForBooking = null },
            onSubmit = { description, date, time ->
                RexonRepository.bookService(
                    category = catName,
                    serviceIcon = "🔧",
                    description = description,
                    date = date,
                    time = time
                )
                Toast.makeText(context, "Booking confirmed for $catName!", Toast.LENGTH_SHORT).show()
                selectedCategoryForBooking = null
            }
        )
    }
}

// -------------------------------------------------------------
// DEDICATED SUB-SCREENS (Notice Board, HelpDesk, Payments, Amenities, Notifications, Profile)
// -------------------------------------------------------------

// Notice Board Screen matching exact user screenshot
@Composable
fun UserNoticeBoardScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val notices by RexonRepository.notices.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(notices, key = { it.id }) { notice ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcement",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Announcement",
                            color = Color(0xFF16A34A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = notice.content,
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Normal,
                        lineHeight = 22.sp,
                        fontFamily = PlusJakartaSans
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notice.date,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = PlusJakartaSans
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { RexonRepository.voteNotice(notice.id, true) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (notice.userVoted == 1) Color(0xFF16A34A) else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${notice.likes}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = PlusJakartaSans
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { RexonRepository.voteNotice(notice.id, false) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "Dislike",
                                    tint = if (notice.userVoted == -1) Color(0xFFEF4444) else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${notice.dislikes}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = PlusJakartaSans
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// HelpDesk Screen
@Composable
fun UserHelpDeskScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val helpDeskTickets by RexonRepository.helpDeskTickets.collectAsState()
    var showRaiseComplaintModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("HelpDesk Complaints", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
            Button(
                onClick = { showRaiseComplaintModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("+ Raise Complaint", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(helpDeskTickets, key = { it.id }) { ticket ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (ticket.status == "NEW") AmberPendingLight else EmeraldGreenLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ticket.status,
                                    color = if (ticket.status == "NEW") AmberPending else EmeraldGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PlusJakartaSans,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(text = "${ticket.category} • ${ticket.date}", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = ticket.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = ticket.description, fontSize = 12.sp, color = Color(0xFF475569), fontFamily = PlusJakartaSans)
                    }
                }
            }
        }
    }

    if (showRaiseComplaintModal) {
        RaiseComplaintDialog(
            onDismiss = { showRaiseComplaintModal = false },
            onSubmit = { title, desc, cat ->
                RexonRepository.raiseComplaint(title, desc, cat)
                showRaiseComplaintModal = false
            }
        )
    }
}

// Society Payments Screen
@Composable
fun UserPaymentsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val payments by RexonRepository.payments.collectAsState()
    val context = LocalContext.current
    var selectedInvoiceForReceipt by remember { mutableStateOf<PaymentInvoice?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("CURRENT MAINTENANCE DUES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp, fontFamily = PlusJakartaSans)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("₹4,500.00", fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, color = Color.White)
                    Text("Due Date: 10 Nov 2026 • Flat B-254", fontSize = 12.sp, color = Color(0xFFCBD5E1), fontFamily = PlusJakartaSans)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            RexonRepository.payMaintenance("INV-2026-11")
                            Toast.makeText(context, "Payment successful! Receipt generated.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("Pay Now via UPI / NetBanking", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("PAYMENT HISTORY & INVOICES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp, fontFamily = PlusJakartaSans)
        }

        items(payments, key = { it.id }) { invoice ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(invoice.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                        Surface(
                            color = if (invoice.status == "PAID") EmeraldGreenLight else AmberPendingLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = invoice.status,
                                color = if (invoice.status == "PAID") EmeraldGreen else AmberPending,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Amount: ₹${"%,.0f".format(invoice.amount)} • Due: ${invoice.dueDate}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)

                    if (invoice.status == "PAID") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { selectedInvoiceForReceipt = invoice },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Text("Download Receipt", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                            }
                        }
                    }
                }
            }
        }
    }

    selectedInvoiceForReceipt?.let { invoice ->
        ReceiptDialog(invoice = invoice, onDismiss = { selectedInvoiceForReceipt = null })
    }
}

// Amenities Screen
@Composable
fun UserAmenitiesScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val amenities by RexonRepository.amenities.collectAsState()
    var showBookAmenityModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bookable Society Amenities", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
            Button(
                onClick = { showBookAmenityModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("+ Book Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(amenities, key = { it.id }) { booking ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(booking.amenityIcon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(booking.amenityName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                Text("${booking.date} • ${booking.timeSlot}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                            }
                        }

                        Surface(color = EmeraldGreenLight, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = booking.status,
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBookAmenityModal) {
        BookAmenityDialog(
            onDismiss = { showBookAmenityModal = false },
            onSubmit = { name, icon, date, slot ->
                RexonRepository.bookAmenity(name, icon, date, slot, 1)
                showBookAmenityModal = false
            }
        )
    }
}

// Notifications Screen
@Composable
fun UserNotificationsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "RECENT GATE ALERTS & NOTIFICATIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFF64748B),
                fontFamily = PlusJakartaSans
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VISITOR AT GATE A",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A),
                                letterSpacing = 1.sp,
                                fontFamily = PlusJakartaSans
                            )
                        }
                        Text("2m ago", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bruno Stevenson • Guest for Flat B-254",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A),
                        fontFamily = PlusJakartaSans
                    )
                    Text(
                        text = "Waiting at Main Gate intercom for resident confirmation.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569),
                        fontFamily = PlusJakartaSans
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Bruno Stevenson Allowed Entry!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("Allow Entry", fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = PlusJakartaSans)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Entry Denied.", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("Deny", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = PlusJakartaSans)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = "Delivery", tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Amazon Package at Reception", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                        Text("Security has kept your parcel securely.", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                    }
                    Text("1h ago", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// User Household Screen
@Composable
fun UserHouseholdScreen(onBack: () -> Unit = {}) {
    BackHandler(onBack = onBack)
    var householdTab by remember { mutableStateOf(0) } // 0: Family, 1: Daily Helps, 2: Vehicles, 3: Settings
    var showAddDialog by remember { mutableStateOf(false) }

    val userProfile by RexonRepository.userProfile.collectAsState()
    val familyMembers by RexonRepository.familyMembers.collectAsState()
    val dailyHelps by RexonRepository.dailyHelps.collectAsState()
    val vehicles by RexonRepository.vehicles.collectAsState()

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Resident Identity Profile Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFE2E8F0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getCategoryDrawableRes(VisitorCategory.GUEST)),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = userProfile.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                    Text(text = "Flat ${userProfile.fullFlatId} • ${userProfile.role}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                    Text(text = userProfile.phone, fontSize = 12.sp, color = Color(0xFF94A3B8), fontFamily = PlusJakartaSans)
                }
            }
        }

        TabRow(
            selectedTabIndex = householdTab,
            containerColor = Color.White,
            contentColor = Color(0xFF0F172A),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[householdTab]),
                    color = Color.Black,
                    height = 2.5.dp
                )
            }
        ) {
            Tab(selected = householdTab == 0, onClick = { householdTab = 0 }, text = { Text("Family", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) })
            Tab(selected = householdTab == 1, onClick = { householdTab = 1 }, text = { Text("Daily Helps", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) })
            Tab(selected = householdTab == 2, onClick = { householdTab = 2 }, text = { Text("Vehicles", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) })
            Tab(selected = householdTab == 3, onClick = { householdTab = 3 }, text = { Text("Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) })
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (householdTab) {
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Registered Family Members", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Text("+ Add Member", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(familyMembers) { fm ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(fm.avatarInitials, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(fm.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                                Text("${fm.relation} • ${fm.phone}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                                            }
                                        }
                                        Icon(Icons.Default.Verified, contentDescription = "Active", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Domestic Help Staff", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Text("+ Add Staff", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(dailyHelps) { help ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(help.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                            Text("${help.role} • ★ ${help.rating}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                                        }

                                        Switch(
                                            checked = help.isInside,
                                            onCheckedChange = { RexonRepository.toggleDailyHelp(help.id) },
                                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldGreen)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Registered Vehicles", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Text("+ Add Vehicle", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(vehicles) { v ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (v.vehicleType.contains("Car")) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                                                contentDescription = v.vehicleType,
                                                tint = Color(0xFF0F172A)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(v.vehicleNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = JetBrainsMono, color = Color(0xFF0F172A))
                                                Text("${v.vehicleType} • Slot: ${v.parkingSlot}", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                                            }
                                        }

                                        Surface(color = Color.White, shape = RoundedCornerShape(6.dp)) {
                                            Text("RFID TAGGED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen, modifier = Modifier.padding(6.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Settings Tab
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FB)),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("APP PREFERENCES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF64748B), letterSpacing = 1.sp, fontFamily = PlusJakartaSans)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Gate Bell Push Alerts", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                    Switch(checked = true, onCheckedChange = {})
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Auto-Approve Regular Staff", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                                    Switch(checked = true, onCheckedChange = {})
                                }
                            }
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Log Out of Account", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddHouseholdItemDialog(
            tab = householdTab,
            onDismiss = { showAddDialog = false },
            onAddFamily = { name, rel, phone ->
                RexonRepository.addFamilyMember(name, rel, phone)
                showAddDialog = false
            },
            onAddHelp = { name, role, phone ->
                RexonRepository.addDailyHelp(name, role, phone)
                showAddDialog = false
            },
            onAddVehicle = { type, num, slot ->
                RexonRepository.addVehicle(type, num, slot)
                showAddDialog = false
            }
        )
    }
}

// Pre-Approval Modal for Resident (Clean White dialog with top-right 3D character badge matching screenshots)
@Composable
fun UserPreApproveModal(
    category: VisitorCategory,
    onDismiss: () -> Unit,
    onCreated: (PreApproval) -> Unit
) {
    var visitorName by remember { mutableStateOf("") }
    var companyName by remember {
        mutableStateOf(
            when (category) {
                VisitorCategory.DELIVERY -> "Amazon"
                VisitorCategory.CAB -> "Uber"
                VisitorCategory.SERVICE -> "Urban Company"
                else -> ""
            }
        )
    }
    var vehicleNumber by remember { mutableStateOf("") }
    var selectedValidity by remember { mutableStateOf("Today, Anytime") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (category) {
                                VisitorCategory.GUEST -> "Allow my Guest"
                                VisitorCategory.DELIVERY -> "Allow Deliveryman"
                                VisitorCategory.SERVICE -> "Allow Serviceman"
                                VisitorCategory.CAB -> "Allow my Cab"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = PlusJakartaSans,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Pre-approved GatePass PIN",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Image(
                        painter = painterResource(id = getCategoryDrawableRes(category)),
                        contentDescription = category.label,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = visitorName,
                    onValueChange = { visitorName = it },
                    label = { Text("${category.label} Name / Description", fontFamily = PlusJakartaSans) },
                    placeholder = { Text(if (category == VisitorCategory.GUEST) "e.g. Bruno Stevenson" else "e.g. Swiggy Delivery Partner", fontFamily = PlusJakartaSans) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (category == VisitorCategory.CAB) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Cab Number (e.g. MH 02 4425)", fontFamily = PlusJakartaSans) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Pass Validity Window:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Next 2 Hours", "Today, Anytime", "Tomorrow").forEach { v ->
                        val isSel = selectedValidity == v
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF111111) else Color(0xFFF0F7FB),
                            modifier = Modifier.weight(1f).clickable { selectedValidity = v }
                        ) {
                            Text(
                                text = v,
                                color = if (isSel) Color.White else Color(0xFF0F172A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (visitorName.isNotBlank()) {
                            val newPass = RexonRepository.createPreApproval(
                                visitorName = visitorName,
                                category = category,
                                companyName = companyName,
                                vehicleNumber = vehicleNumber,
                                validTimeSlot = selectedValidity
                            )
                            onCreated(newPass)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                    enabled = visitorName.isNotBlank()
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// Dialog: Service Booking
@Composable
fun ServiceBookingDialog(
    category: String,
    onDismiss: () -> Unit,
    onSubmit: (description: String, date: String, time: String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Tomorrow") }
    var selectedTime by remember { mutableStateOf("11:00 AM - 01:00 PM") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Book $category", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Problem Description", fontFamily = PlusJakartaSans) },
                    placeholder = { Text("e.g. AC cooling low, tap leaking...", fontFamily = PlusJakartaSans) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Select Date:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Today", "Tomorrow", "Weekend").forEach { d ->
                        val isSel = selectedDate == d
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF111111) else Color(0xFFF0F7FB),
                            modifier = Modifier.weight(1f).clickable { selectedDate = d }
                        ) {
                            Text(
                                text = d,
                                color = if (isSel) Color.White else Color(0xFF0F172A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (description.isNotBlank()) {
                            onSubmit(description, selectedDate, selectedTime)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                    enabled = description.isNotBlank()
                ) {
                    Text("Submit Booking", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// Dialog: Raise Complaint
@Composable
fun RaiseComplaintDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Common Area") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Raise HelpDesk Ticket", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title", fontFamily = PlusJakartaSans) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description", fontFamily = PlusJakartaSans) },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { if (title.isNotBlank() && desc.isNotBlank()) onSubmit(title, desc, cat) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                    enabled = title.isNotBlank() && desc.isNotBlank()
                ) {
                    Text("Submit Ticket", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// Dialog: Book Amenity
@Composable
fun BookAmenityDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, icon: String, date: String, slot: String) -> Unit
) {
    var selectedAmenity by remember { mutableStateOf("Swimming Pool" to "🏊") }
    var selectedDate by remember { mutableStateOf("Tomorrow") }
    var selectedSlot by remember { mutableStateOf("06:00 PM - 07:00 PM") }

    val options = listOf(
        "Swimming Pool" to "🏊",
        "Clubhouse" to "🏛️",
        "Tennis Court" to "🎾",
        "Gymnasium" to "🏋️"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Book Society Amenity", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.height(14.dp))

                options.forEach { (name, icon) ->
                    val isSel = selectedAmenity.first == name
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) Color(0xFF111111) else Color(0xFFF0F7FB),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedAmenity = name to icon }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(name, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSubmit(selectedAmenity.first, selectedAmenity.second, selectedDate, selectedSlot) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111))
                ) {
                    Text("Confirm Slot Booking", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// Dialog: Receipt
@Composable
fun ReceiptDialog(invoice: PaymentInvoice, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Paid", tint = EmeraldGreen, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("Official Society Receipt", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                Text(invoice.title, fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.height(14.dp))
                Text("₹${"%,.0f".format(invoice.amount)}", fontSize = 26.sp, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, color = Color(0xFF0F172A))
                Text("Paid on ${invoice.paidDate.ifBlank { "05 Oct 2026" }}", fontSize = 12.sp, color = EmeraldGreen, fontFamily = PlusJakartaSans)

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111))
                ) {
                    Text("Done", fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// Dialog: Add Household Item
@Composable
fun AddHouseholdItemDialog(
    tab: Int,
    onDismiss: () -> Unit,
    onAddFamily: (name: String, relation: String, phone: String) -> Unit,
    onAddHelp: (name: String, role: String, phone: String) -> Unit,
    onAddVehicle: (type: String, number: String, slot: String) -> Unit
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = when (tab) {
                        0 -> "Add Family Member"
                        1 -> "Add Domestic Staff"
                        else -> "Register Vehicle"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A),
                    fontFamily = PlusJakartaSans
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = field1,
                    onValueChange = { field1 = it },
                    label = {
                        Text(when (tab) {
                            0 -> "Full Name"
                            1 -> "Staff Name"
                            else -> "Vehicle Type (Car / Bike)"
                        }, fontFamily = PlusJakartaSans)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = field2,
                    onValueChange = { field2 = it },
                    label = {
                        Text(when (tab) {
                            0 -> "Relation (Spouse, Child, Parent)"
                            1 -> "Role (Maid, Cook, Driver)"
                            else -> "Vehicle Number (e.g. MH 02 AB 1234)"
                        }, fontFamily = PlusJakartaSans)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = field3,
                    onValueChange = { field3 = it },
                    label = {
                        Text(when (tab) {
                            0 -> "Phone Number"
                            1 -> "Phone Number"
                            else -> "Parking Slot (e.g. Slot B2-44)"
                        }, fontFamily = PlusJakartaSans)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        when (tab) {
                            0 -> onAddFamily(field1, field2, field3)
                            1 -> onAddHelp(field1, field2, field3)
                            else -> onAddVehicle(field1, field2, field3)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                    enabled = field1.isNotBlank() && field2.isNotBlank()
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}

// Dialog: Create Community Post
@Composable
fun CreateCommunityPostDialog(
    onDismiss: () -> Unit,
    onSubmit: (content: String) -> Unit
) {
    var postContent by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Share with Residents", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A), fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    label = { Text("What's on your mind?", fontFamily = PlusJakartaSans) },
                    placeholder = { Text("Share updates, reminders, notices...", fontFamily = PlusJakartaSans) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { if (postContent.isNotBlank()) onSubmit(postContent) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                    enabled = postContent.isNotBlank()
                ) {
                    Text("Publish Post", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans)
                }
            }
        }
    }
}
