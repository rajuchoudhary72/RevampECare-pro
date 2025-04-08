package com.app.ecarepro.model

data class RecentInfraction(
    val admissionNo: Any,
    val `class`: Any,
    val consequences: String,
    val correctiveAction: String,
    val designation: String,
    val compCreatedOn: String,
    val infraction: String,
    val infractionOn: String,
    val instance: String,
    val photo: Any,
    val point: Int,
    val stID: Int,
    val staffName: String,
    val complianceAttachment: String,
    val stffPhoto: String,
    val studentName: Any,
    val canDelete: Boolean,
    val isResolved: Boolean,
    val showResolvedButton: Boolean,
    val isComplianceActive: Boolean,
    val id: String,
    val subInfraction: String
)