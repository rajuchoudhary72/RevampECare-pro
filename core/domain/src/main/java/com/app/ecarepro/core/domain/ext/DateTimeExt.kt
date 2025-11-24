package com.app.ecarepro.core.domain.ext

import java.util.Calendar

/**
 * Get today's day number (1-6 for Monday-Saturday)
 * Returns 1 for Monday, 2 for Tuesday, ..., 6 for Saturday
 * Returns 1 for Sunday (defaults to Monday)
 */
fun getTodayDayNumber(): Int {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 1 // Default to Day 1 if Sunday
        else -> 1
    }
}

/**
 * Extension function for Calendar to get day number
 */
fun Calendar.getDayNumber(): Int {
    return when (this.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 1
        else -> 1
    }
}
