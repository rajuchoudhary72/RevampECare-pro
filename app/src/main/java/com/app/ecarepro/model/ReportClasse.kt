package com.app.ecarepro.model

data class ReportClasse(
    val academicYear: String,
    val classID: Int,
    val className: String,
    val isCur: Int,
    val reportCards: List<ReportCard>,
    val yrID: Int
)