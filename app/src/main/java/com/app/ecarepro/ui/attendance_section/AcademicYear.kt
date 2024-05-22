package com.app.ecarepro.ui.attendance_section

data class AcademicYear(
    val endDate: String?,
    val isCur: Boolean?,
    val session: String,
    val startDate: String?,
    val yrID: Int?
){
    override fun toString(): String {
        return session
    }
}