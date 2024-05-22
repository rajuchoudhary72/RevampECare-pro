package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ClassSummary

data class NetworkAttedanceSummary(
    val classSummary: List<ClassSummary>,
    val errorCode: Int,
    val isLateEnabled: Boolean,
    val message: String,
    val status: String,
    val totalAbsent: Int,
    val totalLate: Int,
    val totalLeave: Int,
    val totalPresent: Int
)