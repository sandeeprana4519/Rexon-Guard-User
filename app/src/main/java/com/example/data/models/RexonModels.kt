package com.example.data.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VisitorCategory(val label: String, val icon: String) {
    GUEST("Guest", "👤"),
    DELIVERY("Delivery", "📦"),
    SERVICE("Service", "🔧"),
    CAB("Cab", "🚖")
}

enum class GateLogStatus(val label: String) {
    WAITING("Waiting"),
    INSIDE("Inside"),
    EXITED("Exited"),
    DENIED("Denied")
}

data class GateLog(
    val id: String,
    val visitorName: String,
    val visitorPhone: String,
    val category: VisitorCategory,
    val wing: String,
    val flatNumber: String,
    val companyName: String = "",
    val vehicleNumber: String = "",
    val photoUrl: String = "",
    val status: GateLogStatus = GateLogStatus.WAITING,
    val entryTime: Long = System.currentTimeMillis(),
    val exitTime: Long? = null,
    val passcode: String = "",
    val guardName: String = "Rajesh Kumar",
    val gateName: String = "Gate A, West Corner"
) {
    val formattedEntryTime: String
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(entryTime))

    val formattedExitTime: String?
        get() = exitTime?.let { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it)) }

    val fullFlatId: String
        get() = "$wing-$flatNumber"
}

data class PreApproval(
    val id: String,
    val visitorName: String,
    val category: VisitorCategory,
    val passcode: String, // 6-digit PIN e.g. "541142"
    val qrToken: String,
    val wing: String,
    val flatNumber: String,
    val validDate: String,
    val validTimeSlot: String,
    val companyName: String = "",
    val vehicleNumber: String = "",
    val isUsed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullFlatId: String
        get() = "$wing-$flatNumber"
}

enum class AlertType(val title: String, val icon: String, val severityColor: Long) {
    FIRE_ALERT("Fire Alert", "🔥", 0xFFEF4444),
    STUCK_IN_LIFT("Stuck in Lift", "🛗", 0xFFF59E0B),
    ANIMAL_THREAT("Animal Threat", "🐕", 0xFFEF4444),
    VISITOR_THREAT("Visitor Threat", "🚨", 0xFFDC2626)
}

data class SecurityAlert(
    val id: String,
    val type: AlertType,
    val senderName: String,
    val wing: String,
    val flatNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE",
    val description: String = ""
) {
    val formattedTime: String
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))

    val fullFlatId: String
        get() = "$wing-$flatNumber"
}

data class GuardMessage(
    val id: String,
    val senderName: String,
    val wing: String,
    val flatNumber: String,
    val message: String,
    val recipient: String = "SECURITY", // SECURITY or ADMIN
    val timestamp: Long = System.currentTimeMillis(),
    val isAcknowledged: Boolean = false
) {
    val timeElapsed: String
        get() {
            val diffSec = (System.currentTimeMillis() - timestamp) / 1000
            return when {
                diffSec < 60 -> "Just now"
                diffSec < 3600 -> "${diffSec / 60}m ago"
                else -> "${diffSec / 3600}h ago"
            }
        }

    val fullFlatId: String
        get() = "$wing-$flatNumber"
}

data class ServiceOrder(
    val id: String,
    val category: String,
    val serviceIcon: String,
    val problemDescription: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val status: String = "Booked", // Booked, Assigned, Completed
    val technicianName: String = "Deepak Sharma",
    val technicianPhone: String = "+91 98765 43210",
    val wing: String = "B",
    val flatNumber: String = "204"
)

data class HelpDeskTicket(
    val id: String,
    val title: String,
    val description: String,
    val category: String = "Personal", // Community or Personal
    val status: String = "NEW", // NEW, RESOLVED
    val wing: String = "B",
    val flatNumber: String = "204",
    val date: String = "Today",
    val repliesCount: Int = 1
)

data class NoticeItem(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val date: String,
    val author: String,
    var likes: Int = 12,
    var dislikes: Int = 1,
    var userVoted: Int = 0 // 1 for like, -1 for dislike
)

data class PaymentInvoice(
    val id: String,
    val title: String,
    val type: String, // Monthly maintenance, Security deposit, Welfare fund
    val amount: Double,
    val dueDate: String,
    var status: String, // PAID, PENDING, FAILED
    val transactionId: String = "",
    val paidDate: String = ""
)

data class AmenityBooking(
    val id: String,
    val amenityName: String,
    val amenityIcon: String,
    val date: String,
    val timeSlot: String,
    val status: String, // CONFIRMED, PENDING, CANCELED
    val guestsCount: Int = 4
)

data class FamilyMember(
    val id: String,
    val name: String,
    val relation: String,
    val phone: String,
    val avatarInitials: String
)

data class DailyHelp(
    val id: String,
    val name: String,
    val role: String, // Maid, Cook, Driver
    var isInside: Boolean = false,
    val phone: String,
    val rating: Double = 4.8
)

data class VehicleItem(
    val id: String,
    val vehicleType: String, // Car, Bike
    val vehicleNumber: String,
    val parkingSlot: String
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val flatId: String,
    val content: String,
    val timestamp: String,
    var likes: Int = 8,
    val commentsCount: Int = 3,
    var hasLiked: Boolean = false
)

data class ResidentDirectoryEntry(
    val id: String,
    val wing: String,
    val flatNumber: String,
    val name: String,
    val role: String, // Owner, Tenant, Committee Member
    val phone: String
) {
    val fullFlatId: String get() = "$wing-$flatNumber"
}

data class GuardProfile(
    val id: String = "G-101",
    val name: String = "Rajesh Kumar",
    val phone: String = "+91 98765 00112",
    val assignedGate: String = "Gate A, West Corner",
    val shift: String = "Morning (07:00 AM - 03:00 PM)",
    val language: String = "English" // English or Hindi
)

data class UserProfile(
    val id: String = "U-204",
    val name: String = "Vikram Malhotra",
    val phone: String = "+91 99887 76655",
    val email: String = "vikram.malhotra@rexonresidency.com",
    val societyName: String = "Rexon Grandeur Residency",
    val wing: String = "B",
    val flatNumber: String = "204",
    val role: String = "Owner"
) {
    val fullFlatId: String get() = "$wing-$flatNumber"
}
