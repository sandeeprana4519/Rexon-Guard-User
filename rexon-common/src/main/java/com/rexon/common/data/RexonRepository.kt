package com.rexon.common.data
import com.rexon.common.models.*

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object RexonRepository {

    // Current Guard Profile
    private val _guardProfile = MutableStateFlow(GuardProfile())
    val guardProfile: StateFlow<GuardProfile> = _guardProfile.asStateFlow()

    // Current Resident Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Incoming Real-time Gate Call Alert for Resident (VoIP / High-priority Alert)
    private val _incomingGateCall = MutableStateFlow<GateLog?>(null)
    val incomingGateCall: StateFlow<GateLog?> = _incomingGateCall.asStateFlow()

    // Real-time Active Gate Logs
    private val _gateLogs = MutableStateFlow<List<GateLog>>(
        listOf(
            GateLog(
                id = "GL-1001",
                visitorName = "Aman Verma",
                visitorPhone = "+91 98200 12345",
                category = VisitorCategory.GUEST,
                wing = "B",
                flatNumber = "204",
                status = GateLogStatus.WAITING,
                entryTime = System.currentTimeMillis() - (3 * 60 * 1000),
                passcode = "541142"
            ),
            GateLog(
                id = "GL-1002",
                visitorName = "Ramesh Kumar (Zomato)",
                visitorPhone = "+91 97111 88990",
                category = VisitorCategory.DELIVERY,
                companyName = "Zomato",
                wing = "B",
                flatNumber = "204",
                status = GateLogStatus.INSIDE,
                entryTime = System.currentTimeMillis() - (18 * 60 * 1000)
            ),
            GateLog(
                id = "GL-1003",
                visitorName = "Suresh Yadav (Ola)",
                visitorPhone = "+91 98333 44556",
                category = VisitorCategory.CAB,
                companyName = "Ola Cabs",
                vehicleNumber = "MH 02 CZ 4425",
                wing = "A",
                flatNumber = "102",
                status = GateLogStatus.INSIDE,
                entryTime = System.currentTimeMillis() - (35 * 60 * 1000)
            ),
            GateLog(
                id = "GL-1004",
                visitorName = "Sunil (Urban Company)",
                visitorPhone = "+91 98777 22334",
                category = VisitorCategory.SERVICE,
                companyName = "Urban Company",
                wing = "C",
                flatNumber = "301",
                status = GateLogStatus.EXITED,
                entryTime = System.currentTimeMillis() - (2 * 3600 * 1000),
                exitTime = System.currentTimeMillis() - (45 * 60 * 1000)
            )
        )
    )
    val gateLogs: StateFlow<List<GateLog>> = _gateLogs.asStateFlow()

    // Pre-Approvals & Digital GatePasses
    private val _preApprovals = MutableStateFlow<List<PreApproval>>(
        listOf(
            PreApproval(
                id = "PA-501",
                visitorName = "Kunal Shah",
                category = VisitorCategory.GUEST,
                passcode = "541142",
                qrToken = "RXN-541142-B204",
                wing = "B",
                flatNumber = "204",
                validDate = "Today",
                validTimeSlot = "06:00 PM - 11:00 PM"
            ),
            PreApproval(
                id = "PA-502",
                visitorName = "Amazon Express Delivery",
                category = VisitorCategory.DELIVERY,
                passcode = "882109",
                qrToken = "RXN-882109-B204",
                companyName = "Amazon",
                wing = "B",
                flatNumber = "204",
                validDate = "Today",
                validTimeSlot = "Anytime Today"
            )
        )
    )
    val preApprovals: StateFlow<List<PreApproval>> = _preApprovals.asStateFlow()

    // Emergency Security Alerts (SOS)
    private val _securityAlerts = MutableStateFlow<List<SecurityAlert>>(emptyList())
    val securityAlerts: StateFlow<List<SecurityAlert>> = _securityAlerts.asStateFlow()

    // Guard Messages & Instruction Board
    private val _guardMessages = MutableStateFlow<List<GuardMessage>>(
        listOf(
            GuardMessage(
                id = "GM-201",
                senderName = "Vikram Malhotra",
                wing = "B",
                flatNumber = "204",
                message = "Amazon parcel expected around 3 PM, please keep at the security desk if flat locked.",
                timestamp = System.currentTimeMillis() - (15 * 60 * 1000)
            ),
            GuardMessage(
                id = "GM-202",
                senderName = "Sneha Kapur",
                wing = "A",
                flatNumber = "402",
                message = "Keep kids away from cars in underground parking area P2.",
                timestamp = System.currentTimeMillis() - (45 * 60 * 1000)
            ),
            GuardMessage(
                id = "GM-203",
                senderName = "Dr. Mehta",
                wing = "C",
                flatNumber = "101",
                message = "Switch off main overhead water booster pump by 11:00 PM sharp.",
                timestamp = System.currentTimeMillis() - (90 * 60 * 1000)
            )
        )
    )
    val guardMessages: StateFlow<List<GuardMessage>> = _guardMessages.asStateFlow()

    // Services Orders
    private val _serviceOrders = MutableStateFlow<List<ServiceOrder>>(
        listOf(
            ServiceOrder(
                id = "SO-301",
                category = "Plumbing Service",
                serviceIcon = "🚰",
                problemDescription = "Master bathroom tap leakage inspection and washer replacement.",
                scheduledDate = "Tomorrow, 10:30 AM",
                scheduledTime = "10:30 AM",
                status = "Assigned",
                technicianName = "Rakesh Sharma"
            ),
            ServiceOrder(
                id = "SO-302",
                category = "Home Cleaning",
                serviceIcon = "🧹",
                problemDescription = "Deep sofa shampoo and balcony high pressure wash.",
                scheduledDate = "Saturday, 02:00 PM",
                scheduledTime = "02:00 PM",
                status = "Booked"
            )
        )
    )
    val serviceOrders: StateFlow<List<ServiceOrder>> = _serviceOrders.asStateFlow()

    // HelpDesk Tickets
    private val _helpDeskTickets = MutableStateFlow<List<HelpDeskTicket>>(
        listOf(
            HelpDeskTicket(
                id = "HD-401",
                title = "Elevator 2 jerking between 4th and 5th floor",
                description = "Lift B-2 makes a loud grinding squeak and halts momentarily before reaching 5th floor.",
                category = "Community",
                status = "NEW",
                wing = "B",
                flatNumber = "204",
                date = "2 hours ago"
            ),
            HelpDeskTicket(
                id = "HD-402",
                title = "Intercom static noise resolved",
                description = "Intercom unit wiring checked and handset receiver replaced.",
                category = "Personal",
                status = "RESOLVED",
                wing = "B",
                flatNumber = "204",
                date = "Yesterday"
            )
        )
    )
    val helpDeskTickets: StateFlow<List<HelpDeskTicket>> = _helpDeskTickets.asStateFlow()

    // Notice Board Announcements
    private val _notices = MutableStateFlow<List<NoticeItem>>(
        listOf(
            NoticeItem(
                id = "N-1",
                title = "Increasing Society maintenance to $20 from Next month due to security increments.",
                content = "Increasing Society maintenance to $20 from Next month due to security increments.",
                category = "Announcement",
                date = "19 Aug, 05:30 pm",
                author = "Managing Committee",
                likes = 78,
                dislikes = 12
            ),
            NoticeItem(
                id = "N-2",
                title = "Society meeting about water management in our society at Community hall tomorrow.",
                content = "Society meeting about water management in our society at Community hall tomorrow.",
                category = "Announcement",
                date = "17 Aug, 02:40 pm",
                author = "Facility Team",
                likes = 45,
                dislikes = 5
            ),
            NoticeItem(
                id = "N-3",
                title = "Increasing Society maintenance to $20 from Next month due to security increments.",
                content = "Increasing Society maintenance to $20 from Next month due to security increments.",
                category = "Announcement",
                date = "19 Aug, 05:30 pm",
                author = "Managing Committee",
                likes = 78,
                dislikes = 12
            )
        )
    )
    val notices: StateFlow<List<NoticeItem>> = _notices.asStateFlow()

    // Society Payment Invoices
    private val _payments = MutableStateFlow<List<PaymentInvoice>>(
        listOf(
            PaymentInvoice(
                id = "INV-2026-11",
                title = "Society Maintenance - November 2026",
                type = "Monthly Maintenance",
                amount = 4500.0,
                dueDate = "10 Nov 2026",
                status = "PENDING"
            ),
            PaymentInvoice(
                id = "INV-2026-10",
                title = "Society Maintenance - October 2026",
                type = "Monthly Maintenance",
                amount = 4500.0,
                dueDate = "10 Oct 2026",
                status = "PAID",
                transactionId = "TXN-9988224501",
                paidDate = "05 Oct 2026"
            ),
            PaymentInvoice(
                id = "INV-2026-WF",
                title = "Clubhouse Renovation Welfare Fund",
                type = "Welfare Fund",
                amount = 2000.0,
                dueDate = "15 Oct 2026",
                status = "PAID",
                transactionId = "TXN-7766110023",
                paidDate = "12 Oct 2026"
            )
        )
    )
    val payments: StateFlow<List<PaymentInvoice>> = _payments.asStateFlow()

    // Amenities Bookings
    private val _amenities = MutableStateFlow<List<AmenityBooking>>(
        listOf(
            AmenityBooking(
                id = "AM-1",
                amenityName = "Clubhouse Banquet Hall",
                amenityIcon = "🏛️",
                date = "15 Nov 2026",
                timeSlot = "06:00 PM - 11:00 PM",
                status = "CONFIRMED",
                guestsCount = 45
            ),
            AmenityBooking(
                id = "AM-2",
                amenityName = "Tennis Court 1",
                amenityIcon = "🎾",
                date = "Tomorrow",
                timeSlot = "07:00 AM - 08:00 AM",
                status = "CONFIRMED",
                guestsCount = 2
            )
        )
    )
    val amenities: StateFlow<List<AmenityBooking>> = _amenities.asStateFlow()

    // Household Management
    private val _familyMembers = MutableStateFlow<List<FamilyMember>>(
        listOf(
            FamilyMember("FM-1", "Pooja Malhotra", "Spouse", "+91 99887 76654", "PM"),
            FamilyMember("FM-2", "Aarav Malhotra", "Son", "+91 99887 76653", "AM")
        )
    )
    val familyMembers: StateFlow<List<FamilyMember>> = _familyMembers.asStateFlow()

    private val _dailyHelps = MutableStateFlow<List<DailyHelp>>(
        listOf(
            DailyHelp("DH-1", "Shanta Bai", "Maid / Cleaning", isInside = true, phone = "+91 91234 56789", rating = 4.9),
            DailyHelp("DH-2", "Kavita Devi", "Cook", isInside = false, phone = "+91 91234 56790", rating = 4.7),
            DailyHelp("DH-3", "Mukesh", "Car Driver", isInside = false, phone = "+91 91234 56791", rating = 4.8)
        )
    )
    val dailyHelps: StateFlow<List<DailyHelp>> = _dailyHelps.asStateFlow()

    private val _vehicles = MutableStateFlow<List<VehicleItem>>(
        listOf(
            VehicleItem("V-1", "Car", "MH 02 ER 8821", "Slot B2-44"),
            VehicleItem("V-2", "Bike", "MH 02 BW 1902", "Slot B2-45")
        )
    )
    val vehicles: StateFlow<List<VehicleItem>> = _vehicles.asStateFlow()

    // Community Feed & Directory (Matching Screenshot with Emili Williamson, Peter Johnson, Jessica Albania, Rahul Patel)
    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(
        listOf(
            CommunityPost(
                id = "CP-1",
                authorName = "Emili Williamson",
                flatId = "A 202",
                content = "Keep kids away form Cars in Parking at A Wing.",
                timestamp = "2m ago",
                likes = 12,
                commentsCount = 3
            ),
            CommunityPost(
                id = "CP-2",
                authorName = "Peter Johnson",
                flatId = "B 108",
                content = "Keep reminder to switch off water tank switch on time.",
                timestamp = "28m ago",
                likes = 18,
                commentsCount = 5
            ),
            CommunityPost(
                id = "CP-3",
                authorName = "Jessica Albania",
                flatId = "A 306",
                content = "Keep Street dog away from the main gate.",
                timestamp = "1h ago",
                likes = 24,
                commentsCount = 8
            ),
            CommunityPost(
                id = "CP-4",
                authorName = "Rahul Patel",
                flatId = "A 202",
                content = "Keep reminder to switch off water tank switch on time.",
                timestamp = "2h ago",
                likes = 15,
                commentsCount = 4
            )
        )
    )
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    val residentDirectory: List<ResidentDirectoryEntry> = listOf(
        ResidentDirectoryEntry("RD-1", "B", "204", "Vikram Malhotra", "Owner", "+91 99887 76655"),
        ResidentDirectoryEntry("RD-2", "B", "205", "Arun Singhal", "Owner", "+91 98210 11223"),
        ResidentDirectoryEntry("RD-3", "B", "206", "Priya Nambiar", "Tenant", "+91 98330 44556"),
        ResidentDirectoryEntry("RD-4", "A", "101", "Dr. K. Mehta", "Committee Member", "+91 98110 99887"),
        ResidentDirectoryEntry("RD-5", "A", "402", "Sneha Kapur", "Owner", "+91 98720 33445"),
        ResidentDirectoryEntry("RD-6", "C", "301", "Gaurav Joshi", "Tenant", "+91 99220 55667")
    )

    // ==========================================
    // REPOSITORY ACTIONS (SYNCHRONIZED REAL-TIME)
    // ==========================================

    fun updateGuardLanguage(lang: String) {
        _guardProfile.value = _guardProfile.value.copy(language = lang)
    }

    fun updateGuardGate(gate: String) {
        _guardProfile.value = _guardProfile.value.copy(assignedGate = gate)
    }

    fun registerVisitor(
        name: String,
        phone: String,
        category: VisitorCategory,
        wing: String,
        flatNumber: String,
        companyName: String = "",
        vehicleNumber: String = "",
        photoUrl: String = ""
    ): GateLog {
        val newLog = GateLog(
            id = "GL-" + UUID.randomUUID().toString().take(6).uppercase(),
            visitorName = name,
            visitorPhone = phone.ifBlank { "+91 98000 00000" },
            category = category,
            wing = wing,
            flatNumber = flatNumber,
            companyName = companyName,
            vehicleNumber = vehicleNumber,
            photoUrl = photoUrl,
            status = GateLogStatus.WAITING,
            entryTime = System.currentTimeMillis(),
            guardName = _guardProfile.value.name,
            gateName = _guardProfile.value.assignedGate
        )

        _gateLogs.value = listOf(newLog) + _gateLogs.value

        // If the visitor is visiting the active Resident's flat (Wing B, Flat 204),
        // instantly trigger the high-priority Incoming Call alert!
        if (wing == _userProfile.value.wing && flatNumber == _userProfile.value.flatNumber) {
            _incomingGateCall.value = newLog
        }

        return newLog
    }

    fun markGateLogStatus(logId: String, newStatus: GateLogStatus) {
        _gateLogs.value = _gateLogs.value.map { log ->
            if (log.id == logId) {
                if (newStatus == GateLogStatus.EXITED) {
                    log.copy(status = newStatus, exitTime = System.currentTimeMillis())
                } else {
                    log.copy(status = newStatus)
                }
            } else log
        }

        // Clear incoming call if matching
        if (_incomingGateCall.value?.id == logId) {
            _incomingGateCall.value = null
        }
    }

    fun acceptIncomingCall(logId: String) {
        markGateLogStatus(logId, GateLogStatus.INSIDE)
    }

    fun denyIncomingCall(logId: String) {
        markGateLogStatus(logId, GateLogStatus.DENIED)
    }

    fun dismissIncomingCall() {
        _incomingGateCall.value = null
    }

    // Dialpad Passcode verification
    fun verifyPasscode(enteredPin: String): Pair<Boolean, String> {
        val cleanPin = enteredPin.trim()
        val matchingPreApproval = _preApprovals.value.find { it.passcode == cleanPin && !it.isUsed }

        if (matchingPreApproval != null) {
            // Mark pre-approval as used and create INSIDE log
            _preApprovals.value = _preApprovals.value.map {
                if (it.id == matchingPreApproval.id) it.copy(isUsed = true) else it
            }

            val autoLog = GateLog(
                id = "GL-" + UUID.randomUUID().toString().take(6).uppercase(),
                visitorName = matchingPreApproval.visitorName,
                visitorPhone = "+91 Verified Pre-Pass",
                category = matchingPreApproval.category,
                wing = matchingPreApproval.wing,
                flatNumber = matchingPreApproval.flatNumber,
                companyName = matchingPreApproval.companyName,
                vehicleNumber = matchingPreApproval.vehicleNumber,
                status = GateLogStatus.INSIDE,
                passcode = cleanPin,
                entryTime = System.currentTimeMillis()
            )
            _gateLogs.value = listOf(autoLog) + _gateLogs.value
            return Pair(true, "Passcode Verified! Access Granted for ${matchingPreApproval.visitorName} (${matchingPreApproval.fullFlatId})")
        }

        // Also check if any waiting gate log has this passcode
        val waitingMatch = _gateLogs.value.find { it.passcode == cleanPin && it.status == GateLogStatus.WAITING }
        if (waitingMatch != null) {
            markGateLogStatus(waitingMatch.id, GateLogStatus.INSIDE)
            return Pair(true, "Passcode Matched! Entry Permitted for ${waitingMatch.visitorName}")
        }

        return Pair(false, "Invalid or expired 6-digit passcode. Please register visitor or verify with resident.")
    }

    fun createPreApproval(
        visitorName: String,
        category: VisitorCategory,
        companyName: String = "",
        vehicleNumber: String = "",
        validDate: String = "Today",
        validTimeSlot: String = "Full Day"
    ): PreApproval {
        val random6Digit = (100000..999999).random().toString()
        val preApproval = PreApproval(
            id = "PA-" + UUID.randomUUID().toString().take(6).uppercase(),
            visitorName = visitorName,
            category = category,
            passcode = random6Digit,
            qrToken = "RXN-$random6Digit-B204",
            wing = _userProfile.value.wing,
            flatNumber = _userProfile.value.flatNumber,
            validDate = validDate,
            validTimeSlot = validTimeSlot,
            companyName = companyName,
            vehicleNumber = vehicleNumber
        )
        _preApprovals.value = listOf(preApproval) + _preApprovals.value
        return preApproval
    }

    fun triggerSecurityAlert(type: AlertType, description: String = ""): SecurityAlert {
        val alert = SecurityAlert(
            id = "ALT-" + UUID.randomUUID().toString().take(6).uppercase(),
            type = type,
            senderName = _userProfile.value.name,
            wing = _userProfile.value.wing,
            flatNumber = _userProfile.value.flatNumber,
            timestamp = System.currentTimeMillis(),
            status = "ACTIVE",
            description = description.ifBlank { "Immediate security intervention requested!" }
        )
        _securityAlerts.value = listOf(alert) + _securityAlerts.value

        // Also automatically dispatch an urgent message to the guard instruction board
        sendInstructionToGuard(
            message = "EMERGENCY SOS: ${type.title} triggered at ${alert.fullFlatId}! Send emergency support immediately.",
            recipient = "SECURITY"
        )
        return alert
    }

    fun resolveSecurityAlert(alertId: String) {
        _securityAlerts.value = _securityAlerts.value.filter { it.id != alertId }
    }

    fun sendInstructionToGuard(message: String, recipient: String = "SECURITY"): GuardMessage {
        val msg = GuardMessage(
            id = "GM-" + UUID.randomUUID().toString().take(6).uppercase(),
            senderName = _userProfile.value.name,
            wing = _userProfile.value.wing,
            flatNumber = _userProfile.value.flatNumber,
            message = message,
            recipient = recipient,
            timestamp = System.currentTimeMillis()
        )
        _guardMessages.value = listOf(msg) + _guardMessages.value
        return msg
    }

    fun acknowledgeMessage(messageId: String) {
        _guardMessages.value = _guardMessages.value.map {
            if (it.id == messageId) it.copy(isAcknowledged = true) else it
        }
    }

    fun bookService(category: String, serviceIcon: String, description: String, date: String, time: String): ServiceOrder {
        val order = ServiceOrder(
            id = "SO-" + UUID.randomUUID().toString().take(6).uppercase(),
            category = category,
            serviceIcon = serviceIcon,
            problemDescription = description,
            scheduledDate = date,
            scheduledTime = time,
            status = "Booked",
            technicianName = "Assigned Soon",
            wing = _userProfile.value.wing,
            flatNumber = _userProfile.value.flatNumber
        )
        _serviceOrders.value = listOf(order) + _serviceOrders.value
        return order
    }

    fun raiseComplaint(title: String, description: String, category: String): HelpDeskTicket {
        val ticket = HelpDeskTicket(
            id = "HD-" + UUID.randomUUID().toString().take(6).uppercase(),
            title = title,
            description = description,
            category = category,
            status = "NEW",
            wing = _userProfile.value.wing,
            flatNumber = _userProfile.value.flatNumber,
            date = "Just now"
        )
        _helpDeskTickets.value = listOf(ticket) + _helpDeskTickets.value
        return ticket
    }

    fun resolveComplaint(ticketId: String) {
        _helpDeskTickets.value = _helpDeskTickets.value.map {
            if (it.id == ticketId) it.copy(status = "RESOLVED") else it
        }
    }

    fun payMaintenance(invoiceId: String): String {
        val txnId = "TXN-" + (1000000000L..9999999999L).random()
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        _payments.value = _payments.value.map {
            if (it.id == invoiceId) {
                it.copy(status = "PAID", transactionId = txnId, paidDate = dateStr)
            } else it
        }
        return txnId
    }

    fun bookAmenity(amenityName: String, amenityIcon: String, date: String, timeSlot: String, guests: Int = 1): AmenityBooking {
        val booking = AmenityBooking(
            id = "AM-" + UUID.randomUUID().toString().take(6).uppercase(),
            amenityName = amenityName,
            amenityIcon = amenityIcon,
            date = date,
            timeSlot = timeSlot,
            status = "CONFIRMED",
            guestsCount = guests
        )
        _amenities.value = listOf(booking) + _amenities.value
        return booking
    }

    fun addFamilyMember(name: String, relation: String, phone: String) {
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        val member = FamilyMember(
            id = "FM-" + UUID.randomUUID().toString().take(4),
            name = name,
            relation = relation,
            phone = phone,
            avatarInitials = initials.ifBlank { "FM" }
        )
        _familyMembers.value = _familyMembers.value + member
    }

    fun toggleDailyHelp(helpId: String) {
        _dailyHelps.value = _dailyHelps.value.map {
            if (it.id == helpId) it.copy(isInside = !it.isInside) else it
        }
    }

    fun addDailyHelp(name: String, role: String, phone: String) {
        val help = DailyHelp(
            id = "DH-" + UUID.randomUUID().toString().take(4),
            name = name,
            role = role,
            phone = phone,
            isInside = false
        )
        _dailyHelps.value = _dailyHelps.value + help
    }

    fun addVehicle(type: String, number: String, slot: String) {
        val v = VehicleItem(
            id = "V-" + UUID.randomUUID().toString().take(4),
            vehicleType = type,
            vehicleNumber = number.uppercase(),
            parkingSlot = slot
        )
        _vehicles.value = _vehicles.value + v
    }

    fun voteNotice(noticeId: String, isLike: Boolean) {
        _notices.value = _notices.value.map { notice ->
            if (notice.id == noticeId) {
                if (isLike) {
                    if (notice.userVoted == 1) {
                        notice.copy(likes = notice.likes - 1, userVoted = 0)
                    } else {
                        val newDislikes = if (notice.userVoted == -1) notice.dislikes - 1 else notice.dislikes
                        notice.copy(likes = notice.likes + 1, dislikes = newDislikes, userVoted = 1)
                    }
                } else {
                    if (notice.userVoted == -1) {
                        notice.copy(dislikes = notice.dislikes - 1, userVoted = 0)
                    } else {
                        val newLikes = if (notice.userVoted == 1) notice.likes - 1 else notice.likes
                        notice.copy(dislikes = notice.dislikes + 1, likes = newLikes, userVoted = -1)
                    }
                }
            } else notice
        }
    }

    fun likeCommunityPost(postId: String) {
        _communityPosts.value = _communityPosts.value.map { post ->
            if (post.id == postId) {
                if (post.hasLiked) {
                    post.copy(likes = post.likes - 1, hasLiked = false)
                } else {
                    post.copy(likes = post.likes + 1, hasLiked = true)
                }
            } else post
        }
    }

    fun addCommunityPost(content: String) {
        val post = CommunityPost(
            id = "CP-" + UUID.randomUUID().toString().take(6).uppercase(),
            authorName = _userProfile.value.name,
            flatId = _userProfile.value.fullFlatId,
            content = content,
            timestamp = "Just now",
            likes = 0,
            commentsCount = 0
        )
        _communityPosts.value = listOf(post) + _communityPosts.value
    }
}
