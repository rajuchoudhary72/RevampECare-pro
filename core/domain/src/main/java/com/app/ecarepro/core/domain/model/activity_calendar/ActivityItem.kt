package com.app.ecarepro.core.domain.model.activity_calendar

data class ActivityItem(
    val title: String,
    val isWorking: Boolean,
    val duration: Int,
    val displayDate: String,           // "04 March" (pre-parsed from API)
    val displayDayOfWeek: String,      // "Wednesday"
    val displayTillDate: String?,      // "14 March" (null if single-day)
    val displayTillDayOfWeek: String?, // "Saturday" (null if single-day)
    val isMultiDay: Boolean,           // true when tillDate differs from fromDate
)
