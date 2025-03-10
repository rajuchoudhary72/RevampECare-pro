package com.app.ecarepro.model

data class RecentInfraction(
    val admissionNo: Any,
    val `class`: Any,
    val consequences: String,
    val correctiveAction: String,
    val designation: String,
    val infraction: String,
    val infractionOn: String,
    val instance: Int,
    val photo: Any,
    val point: Int,
    val stID: Int,
    val staffName: String,
    val stffPhoto: String,
    val studentName: Any,
    val canDelete: Boolean,
    val id: String,
    val subInfraction: String
)