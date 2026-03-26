package com.app.ecarepro.core.network.model.discipline

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ========== Infraction Models ==========

@Serializable
data class NetworkInfractionType(
    @SerialName("infrTypeID") val infrTypeID: Int? = null,
    @SerialName("infraction") val infraction: String? = null,
)

@Serializable
data class NetworkInfractionConsequence(
    @SerialName("consID") val consID: Int? = null,
    @SerialName("consequences") val consequences: String? = null,
)

@Serializable
data class NetworkInfractionRecord(
    @SerialName("infraction") val infraction: String? = null,
    @SerialName("consequences") val consequences: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("stffPhoto") val stffPhoto: String? = null,
    @SerialName("admissionNo") val admissionNo: String? = null,
    @SerialName("correctiveAction") val correctiveAction: String? = null,
    @SerialName("stID") val stID: Int? = null,
    @SerialName("studentName") val studentName: String? = null,
    @SerialName("point") val point: Int? = null,
    @SerialName("instance") val instance: String? = null,
    @SerialName("infractionOn") val infractionOn: String? = null,
    @SerialName("staffName") val staffName: String? = null,
    @SerialName("issueBy") val issueBy: String? = null,
    @SerialName("class") val recordClass: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("subInfraction") val subInfraction: String? = null,
    @SerialName("isResolved") val isResolved: Boolean? = null,
    @SerialName("canDelete") val canDelete: Boolean? = null,
    @SerialName("showResolvedButton") val showResolvedButton: Boolean? = null,
    @SerialName("isComplianceActive") val isComplianceActive: Boolean? = null,
    @SerialName("complianceAttachment") val complianceAttachment: String? = null,
    @SerialName("infractionID") val infractionID: String = "",
    @SerialName("contactMob") val contactMob: String? = null,
    @SerialName("remarks") val remarks: String? = null,
)

@Serializable
data class NetworkRecentInfraction(
    @SerialName("infraction") val infraction: String? = null,
    @SerialName("subInfraction") val subInfraction: String? = null,
    @SerialName("infractionOn") val infractionOn: String? = null,
)

@Serializable
data class NetworkStudentDTL(
    @SerialName("stID") val stID: Int? = null,
    @SerialName("studentName") val studentName: String? = null,
    @SerialName("admissionNo") val admissionNo: String? = null,
    @SerialName("class") val className: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("contactPerson") val contactPerson: String? = null,
    @SerialName("contactMob") val contactMob: String? = null,
)

@Serializable
data class NetworkStaffDTL(
    @SerialName("sID") val sID: Int? = null,
    @SerialName("staffName") val staffName: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("qualification") val qualification: String? = null,
    @SerialName("photo") val photo: String? = null,
)

// ========== Response Models ==========

@Serializable
data class NetworkInfractionDetailsResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("records") val records: List<NetworkInfractionRecord>? = null,
    @SerialName("showPoints") val showPoints: Boolean? = null,
    @SerialName("totalPoints") val totalPoints: Int? = null,
    @SerialName("studentDTL") val studentDTL: NetworkStudentDTL? = null,
    @SerialName("staffDTL") val staffDTL: NetworkStaffDTL? = null,
) : NetworkResponse

@Serializable
data class NetworkAddInfractionResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("studentDTL") val studentDTL: NetworkStudentDTL? = null,
    @SerialName("stafftDTL") val staffDTL: NetworkStaffDTL? = null, // Note: API typo
    @SerialName("infractionTypes") val infractionTypes: List<NetworkInfractionType>? = null,
    @SerialName("infractionSubCategories") val infractionSubCategories: List<NetworkInfractionType>? = null,
    @SerialName("instance") val instance: String? = null,
    @SerialName("recentInfractions") val recentInfractions: List<NetworkRecentInfraction>? = null,
    @SerialName("infractionConsequences") val infractionConsequences: List<NetworkInfractionConsequence>? = null,
) : NetworkResponse

@Serializable
data class NetworkSubInfractionTypesResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("infractionSubCategories") val infractionSubCategories: List<NetworkInfractionType>? = null,
) : NetworkResponse

@Serializable
data class NetworkInfractionInstanceResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("instance") val instance: String? = null,
) : NetworkResponse

// ========== Request Models ==========

@Serializable
data class NetworkBrowsedFile(
    @SerialName("Attachment") val attachment: String? = null,
    @SerialName("FileExt") val fileExt: String? = null,
    @SerialName("FileURL") val fileURL: String? = "",
)

@Serializable
data class NetworkSaveDisciplineLogRequest(
    @SerialName("stID") val stID: Int? = null,
    @SerialName("SID") val sID: Int? = null,
    @SerialName("uType") val uType: Int,
    @SerialName("action") val action: Int,
    @SerialName("infrSubTypeID") val infrSubTypeID: Int,
    @SerialName("consID") val consID: Int,
    @SerialName("instance") val instance: String,
    @SerialName("correctiveAction") val correctiveAction: String? = null,
    @SerialName("infractionOn") val infractionOn: String,
    @SerialName("isComplianceActive") val isComplianceActive: Boolean = false,
    @SerialName("BrowsedFile") val browsedFile: NetworkBrowsedFile? = null,
)

@Serializable
data class NetworkSaveComplianceRequest(
    @SerialName("utype") val uType: Int,
    @SerialName("ID") val id: String,
    @SerialName("Compliance") val compliance: String,
    @SerialName("BrowsedFile") val browsedFile: NetworkBrowsedFile? = null,
)

// ========== Appreciation Models ==========

@Serializable
data class NetworkAppreciationType(
    @SerialName("aprID") val aprID: Int? = null,
    @SerialName("appreciation") val appreciation: String? = null,
    @SerialName("appreciationSubCatID") val appreciationSubCatID: Int? = null,
)

@Serializable
data class NetworkAppreciationReward(
    @SerialName("rwdID") val rwdID: Int? = null,
    @SerialName("reward") val reward: String? = null,
)

@Serializable
data class NetworkAppreciationRecord(
    @SerialName("id") val id: String? = null,
    @SerialName("appreciation") val appreciation: String? = null,
    @SerialName("subAppreciation") val subAppreciation: String? = null,
    @SerialName("appreciationOn") val appreciationOn: String? = null,
    @SerialName("admissionNo") val admissionNo: String? = null,
    @SerialName("stID") val stID: Int? = null,
    @SerialName("studentName") val studentName: String? = null,
    @SerialName("reward") val reward: String? = null,
    @SerialName("remark") val remark: String? = null,
    @SerialName("point") val point: Int? = null,
    @SerialName("instance") val instance: Int? = null,
    @SerialName("staffName") val staffName: String? = null,
    @SerialName("stffPhoto") val stffPhoto: String? = null,
    @SerialName("class") val recordClass: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("canDelete") val canDelete: Boolean? = null,
)

@Serializable
data class NetworkRecentAppreciation(
    @SerialName("appreciation") val appreciation: String? = null,
    @SerialName("subAppreciation") val subAppreciation: String? = null,
    @SerialName("appreciationOn") val appreciationOn: String? = null,
)

@Serializable
data class NetworkAppreciationDetailsResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("records") val records: List<NetworkAppreciationRecord>? = null,
    @SerialName("showPoints") val showPoints: Boolean? = null,
    @SerialName("totalPoints") val totalPoints: Int? = null,
    @SerialName("studentDTL") val studentDTL: NetworkStudentDTL? = null,
) : NetworkResponse

@Serializable
data class NetworkAddAppreciationResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("studentDTL") val studentDTL: NetworkStudentDTL? = null,
    @SerialName("appreciationTypes") val appreciationTypes: List<NetworkAppreciationType>? = null,
    @SerialName("recentAppreciations") val recentAppreciations: List<NetworkRecentAppreciation>? = null,
    @SerialName("appreciationRewards") val appreciationRewards: List<NetworkAppreciationReward>? = null,
) : NetworkResponse

@Serializable
data class NetworkSubAppreciationTypesResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("appreciationSubCategories") val appreciationSubCategories: List<NetworkAppreciationType>? = null,
) : NetworkResponse

@Serializable
data class NetworkAppreciationInstanceResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("instance") val instance: String? = null,
) : NetworkResponse

@Serializable
data class NetworkSaveAppreciationRequest(
    @SerialName("action") val action: Int,
    @SerialName("stID") val stID: Int,
    @SerialName("aprSubID") val aprSubID: Int,
    @SerialName("rwdID") val rwdID: Int? = null,
    @SerialName("instance") val instance: String,
    @SerialName("appreciationOn") val appreciationOn: String,
    @SerialName("remark") val remark: String? = null,
)
