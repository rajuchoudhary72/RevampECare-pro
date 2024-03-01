package com.app.ecarepro.model

data class LeaveTerms(
    val backwardDays: Int,
    val daysLimit: Int,
    val forwardDays: Int,
    val isPrevDatesAllow: Boolean,
    val weekOff: List<String>
)