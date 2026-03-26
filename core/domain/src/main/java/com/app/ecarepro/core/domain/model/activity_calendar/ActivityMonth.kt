package com.app.ecarepro.core.domain.model.activity_calendar

data class ActivityMonth(
    val monthNo: Int,
    val monthName: String,
    val year: Int,
    val isCurrent: Boolean,
    val activities: List<ActivityItem>,
)
