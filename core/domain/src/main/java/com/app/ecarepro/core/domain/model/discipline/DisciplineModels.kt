package com.app.ecarepro.core.domain.model.discipline

import kotlinx.serialization.Serializable

// ========== Infraction Domain Models ==========

data class InfractionType(
    val id: Int,
    val name: String,
)

data class InfractionConsequence(
    val id: Int,
    val name: String,
)

data class InfractionRecord(
    val infractionID: String,
    val infraction: String?,
    val consequences: String?,
    val designation: String?,
    val stffPhoto: String?,
    val admissionNo: String?,
    val correctiveAction: String?,
    val stID: Int?,
    val studentName: String?,
    val point: Int?,
    val instance: String?,
    val infractionOn: String?,
    val staffName: String?,
    val issueBy: String?,
    val recordClass: String?,
    val photo: String?,
    val subInfraction: String?,
    val isResolved: Boolean?,
    val canDelete: Boolean?,
    val showResolvedButton: Boolean?,
    val isComplianceActive: Boolean?,
    val complianceAttachment: String?,
    val contactMob: String?,
    val remarks: String?,
)

data class DisciplineUserInfo(
    val id: Int?,
    val name: String?,
    val className: String?,
    val admissionNo: String?,
    val photo: String?,
    val contactPerson: String?,
    val contactMob: String?,
    val designation: String?,
    val mobile: String?,
    val qualification: String?,
)

data class AddInfractionFormData(
    val studentInfo: DisciplineUserInfo?,
    val staffInfo: DisciplineUserInfo?,
    val infractionTypes: List<InfractionType>,
    val infractionConsequences: List<InfractionConsequence>,
)

data class InfractionDetails(
    val records: List<InfractionRecord>,
    val showPoints: Boolean,
    val totalPoints: Int,
    val userInfo: DisciplineUserInfo?,
)

// ========== Appreciation Domain Models ==========

data class AppreciationType(
    val id: Int,
    val name: String,
    val subCatID: Int?,
)

data class AppreciationReward(
    val id: Int,
    val name: String,
)

data class AppreciationRecord(
    val id: String?,
    val appreciation: String?,
    val subAppreciation: String?,
    val appreciationOn: String?,
    val admissionNo: String?,
    val stID: Int?,
    val studentName: String?,
    val reward: String?,
    val remark: String?,
    val point: Int?,
    val instance: Int?,
    val staffName: String?,
    val stffPhoto: String?,
    val recordClass: String?,
    val photo: String?,
    val designation: String?,
    val canDelete: Boolean?,
)

data class AddAppreciationFormData(
    val studentInfo: DisciplineUserInfo?,
    val appreciationTypes: List<AppreciationType>,
    val appreciationRewards: List<AppreciationReward>,
)

data class AppreciationDetails(
    val records: List<AppreciationRecord>,
    val showPoints: Boolean,
    val totalPoints: Int,
    val userInfo: DisciplineUserInfo?,
)
