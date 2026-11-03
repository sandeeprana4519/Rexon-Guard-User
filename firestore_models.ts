/**
 * Rexon Unified Firebase Backend - TypeScript Models & Firestore Collection Schemas
 * Synchronized across Rexon Guard App (Gatekeeper) & Rexon User App (Resident)
 */

export type UserRole = 'owner' | 'tenant' | 'guard' | 'admin';
export type VisitorCategory = 'GUEST' | 'DELIVERY' | 'SERVICE' | 'CAB';
export type GateLogStatus = 'waiting' | 'inside' | 'exited' | 'denied';
export type AlertType = 'FIRE_ALERT' | 'STUCK_IN_LIFT' | 'ANIMAL_THREAT' | 'VISITOR_THREAT';
export type ServiceOrderStatus = 'Booked' | 'Assigned' | 'Completed';
export type TicketStatus = 'NEW' | 'IN_PROGRESS' | 'RESOLVED';
export type PaymentStatus = 'PAID' | 'PENDING' | 'FAILED';

// Collection: users
export interface UserProfileDocument {
  id: string; // Document ID (UID)
  name: string;
  phone: string;
  email: string;
  avatarUrl?: string;
  societyId: string;
  societyName: string;
  wing: string; // e.g. "B"
  flatNumber: string; // e.g. "204"
  role: UserRole;
  fcmTokens: string[]; // For high-priority gate call notifications
  createdAt: number;
  updatedAt: number;
}

// Collection: guards
export interface GuardProfileDocument {
  id: string; // Document ID (UID)
  name: string;
  phone: string;
  avatarUrl?: string;
  assignedGate: string; // e.g. "Gate A, West Corner"
  shift: string; // e.g. "Morning (07:00 AM - 03:00 PM)"
  language: 'English' | 'Hindi';
  societyId: string;
  isActive: boolean;
  fcmTokens: string[];
}

// Collection: gate_logs
export interface GateLogDocument {
  id: string; // Document ID
  visitorName: string;
  visitorPhone: string;
  category: VisitorCategory;
  wing: string;
  flatNumber: string;
  companyName?: string; // e.g. "Zomato", "Amazon"
  vehicleNumber?: string; // e.g. "4425"
  photoUrl?: string;
  status: GateLogStatus;
  entryTime: number; // Unix timestamp ms
  exitTime?: number | null;
  passcode?: string;
  guardId: string;
  guardName: string;
  gateName: string;
  societyId: string;
  fcmCallDispatched?: boolean;
}

// Collection: pre_approvals
export interface PreApprovalDocument {
  id: string;
  visitorName: string;
  category: VisitorCategory;
  passcode: string; // 6-digit numeric PIN e.g. "541142"
  qrToken: string; // e.g. "RXN-541142-B204"
  wing: string;
  flatNumber: string;
  validDate: string;
  validTimeSlot: string;
  companyName?: string;
  vehicleNumber?: string;
  isUsed: boolean;
  usedAt?: number;
  createdAt: number;
  createdByUserId: string;
}

// Collection: security_alerts (Instant SOS Triggers)
export interface SecurityAlertDocument {
  id: string;
  type: AlertType;
  senderId: string;
  senderName: string;
  wing: string;
  flatNumber: string;
  timestamp: number;
  status: 'ACTIVE' | 'RESOLVED';
  description?: string;
  broadcastPayload: {
    title: string;
    body: string;
    sound: 'emergency_siren.wav';
    priority: 'high';
  };
}

// Collection: guard_messages
export interface GuardMessageDocument {
  id: string;
  senderId: string;
  senderName: string;
  wing: string;
  flatNumber: string;
  message: string;
  recipient: 'SECURITY' | 'ADMIN';
  timestamp: number;
  isAcknowledged: boolean;
  acknowledgedByGuardId?: string;
}

// Collection: service_orders
export interface ServiceOrderDocument {
  id: string;
  userId: string;
  wing: string;
  flatNumber: string;
  category: string;
  problemDescription: string;
  scheduledDate: string;
  scheduledTime: string;
  status: ServiceOrderStatus;
  technicianName?: string;
  technicianPhone?: string;
  estimatedCost?: number;
  createdAt: number;
}

// Collection: helpdesk_tickets
export interface HelpDeskTicketDocument {
  id: string;
  userId: string;
  wing: string;
  flatNumber: string;
  title: string;
  description: string;
  category: 'Community' | 'Personal';
  status: TicketStatus;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  createdAt: number;
  resolvedAt?: number;
}

// Collection: payments
export interface PaymentInvoiceDocument {
  id: string;
  flatNumber: string;
  wing: string;
  title: string;
  type: 'Monthly Maintenance' | 'Security Deposit' | 'Welfare Fund';
  amount: number;
  dueDate: string;
  status: PaymentStatus;
  transactionId?: string;
  receiptUrl?: string;
  paidDate?: string;
}
