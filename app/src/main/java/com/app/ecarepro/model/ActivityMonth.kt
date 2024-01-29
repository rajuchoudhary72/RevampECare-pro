package com.app.ecarepro.model

data class ActivityMonth(
    val activity: List<Activity>,
    val isCurrent: Boolean,
    val monthName: String,
    val monthNo: Int,
    val year: Int
)