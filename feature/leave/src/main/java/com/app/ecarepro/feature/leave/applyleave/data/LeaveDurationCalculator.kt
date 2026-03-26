package com.app.ecarepro.feature.leave.applyleave.data

import com.app.ecarepro.core.domain.model.Holiday
import com.app.ecarepro.core.domain.model.LeaveDetail
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object LeaveDurationCalculator {
    fun calculateDuration(
        fromDate: LocalDate,
        tillDate: LocalDate,
        startSession: SessionType,
        endSession: SessionType,
        weekOffs: Set<String>,
        holidays: List<Holiday>,
        sandwichEnabled: Boolean,
        isStaff: Boolean
    ): Double {
        var totalDays = 0.0
        var currentDate = fromDate

        while (currentDate <= tillDate) {
            val dayName = currentDate.dayOfWeek.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )

            val isWeekOff = weekOffs.contains(dayName)
            val isHoliday = holidays.any { it.containsDate(currentDate) }

            // Exclude week offs always
            // Exclude holidays only if sandwich is disabled
            if (isWeekOff) {
                // Skip
            } else if (isHoliday && !sandwichEnabled) {
                // Skip
            } else {
                // Count this day
                if (isStaff) {
                    when {
                        currentDate == fromDate && currentDate == tillDate -> {
                            // Same day leave
                            totalDays += when {
                                startSession == SessionType.FIRST_HALF &&
                                        endSession == SessionType.FIRST_HALF -> 0.5
                                startSession == SessionType.SECOND_HALF &&
                                        endSession == SessionType.SECOND_HALF -> 0.5
                                else -> 1.0
                            }
                        }
                        currentDate == fromDate -> {
                            totalDays += if (startSession == SessionType.SECOND_HALF) 0.5 else 1.0
                        }
                        currentDate == tillDate -> {
                            totalDays += if (endSession == SessionType.FIRST_HALF) 0.5 else 1.0
                        }
                        else -> totalDays += 1.0
                    }
                } else {
                    // Students always full day
                    totalDays += 1.0
                }
            }

            currentDate = currentDate.plusDays(1)
        }

        return totalDays
    }
}

data class LeaveBalancePresentation(
    val total: Double,
    val taken: Double,
    val available: Double,
    val percentage: Double
) {
    val displayText: String = "${taken.toInt()} of ${total.toInt()} leaves left"
    val progressValue: Float = if (total > 0) (taken / total).toFloat() else 0f

    companion object {
        fun from(leaveDetail: LeaveDetail): LeaveBalancePresentation {
            return LeaveBalancePresentation(
                total = leaveDetail.total ?: 0.0,
                taken = leaveDetail.taken ?: 0.0,
                available = leaveDetail.availableBalance,
                percentage = leaveDetail.balancePercentage
            )
        }
    }
}
