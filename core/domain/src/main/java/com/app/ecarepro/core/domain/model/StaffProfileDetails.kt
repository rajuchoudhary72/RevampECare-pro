package com.app.ecarepro.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffProfileDetailsResponse(
    val errorCode: Int,
    val status: String?,
    val message: String?,
    val assignments: Int?,
    val sentMessage: Int?,
    val workLoad: String?,
    val attendancePer: String?,
    val leaveRecord: String?,
    val currentSalary: String?,
    val advanceGiven: String?,
    val sectionControl: StaffSectionControl?,
    val academicYears: List<AcademicYear>?,
    val attendanceEnabled: Boolean?,
    val timetableEnabled: Boolean?,
    val salaryEnabled: Boolean?,
    val details: StaffProfileDetail?,
    val attendanceDTL: StaffAttendanceDetail?,
    val timetableSummary: TimetableSummary?,
    val salaryStructure: SalaryStructure?
)

@Serializable
data class StaffSectionControl(
    val sections: List<StaffSectionItem>
)

@Serializable
data class StaffSectionItem(
    val name: String,
    val isShow: Boolean
)

@Serializable
data class StaffProfileDetail(
    // Basic Info
    val roleName: String?,
    val username: String?,
    val title: String?,
    val fName: String?,
    val mName: String?,
    val lName: String?,
    val name: String,
    val gender: String?,
    val bloodGroup: String?,
    val maritalStatus: String?,
    val mobile: String?,
    val alternateMobile: String?,
    val isSpouseName: Boolean?,
    val fatherHusbandName: String?,
    val fatherHusbandMob: String?,
    val emergencyContactNo: String?,
    val emailID: String?,
    val alternateEmailID: String?,
    val designation: String?,
    val dob: String?,
    val doj: String?,
    val dojEPF: String?,
    val doAnniversary: String?,
    val nationality: String?,
    val qualification: String?,
    val aadharCardNo: String?,
    @SerialName("paN_Number") val panNumber: String?,
    val cbseid: String?,
    @SerialName("uaN_Number") val uanNumber: String?,
    val nationalCode: String?,
    val stateCode: String?,
    val religion: String?,
    val address: String?,
    @SerialName("p_Address") val permanentAddress: String?,
    val photo: String?,
    val coverImg: String?,
    val canChangeProfileImg: Boolean?,
    val canChangeCoverImg: Boolean?,
    val userImgReq: UserImageRequest?,
    val titleID: Int?,
    val maritialStatusID: Int?,
    val relationshipWithMemberId: Int?,
    val bloodGroupID: Int?,
    val relegionID: Int?,
    val nationalityID: Int?,
    val titles: List<String>?,
    val bloodGroupLST: List<String>?,
    val nationalityLST: List<String>?,
    val relegionLST: List<String>?,
    val profileUpdationRecord: String?
)

@Serializable
data class UserImageRequest(
    val coverImg: Int?,
    val profileImg: Int?,
    val childProfileImg: Int?,
    val showProfilePopup: Boolean?,
    val showCoverPopup: Boolean?,
    val profilePopup: String?,
    val coverImgPopup: String?
)

@Serializable
data class StaffAttendanceDetail(
    val fromDate: String?,
    val totalPresent: Int?,
    val totalAbsent: Int?,
    val latInCount: Int?,
    val earlyOutsCount: Int?
)

@Serializable
data class TimetableSummary(
    val totalLecture: Int?,
    val periodTaken: Int?,
    val freePeriod: Int?
)

@Serializable
data class SalaryStructure(
    val salryOf: String?,
    val basic: Double?,
    val allowances: Double?,
    val deduction: Double?,
    val netSalary: Double?,
    val salaryHeads: List<SalaryHead>?
)

@Serializable
data class SalaryHead(
    val headName: String?,
    val shortName: String?,
    val headType: String?,
    val headTypeID: String?,
    val amount: String?
)

enum class StaffProfileSection(
    val apiName: String,
    val displayName: String,
    val tabOrder: Int
) {
    PERSONAL_DETAILS("PersonalDetails", "Personal details", 0),
    ATTENDANCE("Attendance", "Attendance", 1),
    SALARY("Salary", "Salary", 2),
    TIMETABLE("Timetable", "Timetable", 3),
    LEAVE("Leave", "Leave", 4),
    SESSION_LOG("Session Log", "Session Log", 5);

    companion object {
        fun fromApiName(name: String): StaffProfileSection? {
            return values().find { it.apiName == name }
        }
    }
}

enum class StaffPersonalDetailSection(
    val displayName: String,
    val iconName: String,
    val iconColor: Long,
    val iconBackgroundColor: Long
) {
    PERSONAL_INFO(
        "Personal info",
        "graduationcap.fill",
        0xFF9C27B0,
        0x269C27B0  // 15% opacity
    ),
    CONTACT_DETAILS(
        "Contact details",
        "phone.circle",
        0xFF2196F3,
        0x262196F3
    ),
    FAMILY_DETAILS(
        "Family details",
        "person.2.fill",
        0xFFE91E63,
        0x26E91E63
    ),
    OTHER_DETAILS(
        "Other details",
        "exclamationmark.circle",
        0xFFFF9800,
        0x26FF9800
    )
}
