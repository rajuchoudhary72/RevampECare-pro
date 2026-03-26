package com.app.ecarepro.feature.leave.appliedleaves.data

import androidx.compose.ui.graphics.Color

enum class LeaveStatus(val value: String, val statusCode: Int) {
    PENDING("Pending", 0),
    APPROVED("Approved", 1),
    REJECTED("Rejected", 2),
    CANCELLED("Cancelled", 3),
    UNKNOWN("Unknown", -1);

    val displayText: String
        get() = when (this) {
            PENDING -> "Pending"
            APPROVED -> "Approved"
            REJECTED -> "Rejected"
            CANCELLED -> "Cancelled"
            UNKNOWN -> value
        }

    val color: Color
        get() = when (this) {
            PENDING -> Color(0xFF2196F3)  // Blue
            APPROVED -> Color(0xFF4CAF50) // Green
            REJECTED -> Color(0xFFF44336) // Red
            CANCELLED -> Color(0xFF9E9E9E) // Gray
            UNKNOWN -> Color.Gray.copy(alpha = 0.5f)
        }

    val backgroundColor: Color
        get() = color.copy(alpha = 0.1f)

    companion object {
        fun fromString(value: String): LeaveStatus {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) } ?: UNKNOWN
        }

        fun fromStatusCode(code: Int): LeaveStatus {
            return values().firstOrNull { it.statusCode == code } ?: UNKNOWN
        }
    }
}
