package com.app.ecarepro.core.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class StudentProfileDetailsResponse(
    val errorCode: Int,
    val status: String?,
    val message: String?,
    val id: String?,
    val sectionControl: SectionControl?,
    val attendancePer: String?,
    val leaveRecord: String?,
    val lastExamResult: String?,
    val feeDues: String?,
    val bookIssued: Int?,
    val assignments: Int?,
    val infirmaryVisits: Int?,
    val infractions: Int?,
    val appreciation: Int?,
    val academicYears: List<AcademicYear>?,
    val profile: StudentProfileDetail?,
    val attendanceDTL: AttendanceDetail?,
    val academicPerformance: AcademicPerformance?,
    val feeSummery: FeeSummary?,
    val library: LibraryDetail?,
    val transDetails: TransportDetails?,
    val medicineIssued: List<MedicineIssued>?,
    val recentInfractions: List<Infraction>?,
    val recentAppreciations: List<Appreciation>?,
    val medicalCard: MedicalCard?,
    val siblingDetails: List<SiblingDetail>?,
    val reportCardDTLs: List<ReportCardDetail>?
)

@Serializable
@InternalSerializationApi
data class SectionControl(
    val sections: List<SectionItem>
)

@Serializable
@InternalSerializationApi
data class SectionItem(
    val name: String,
    val isShow: Boolean
)

@Serializable
@InternalSerializationApi
data class StudentProfileDetail(
    // Basic Info
    val username: String?,
    val name: String,
    val gender: String?,
    val admissionNo: String?,
    val rollNo: String?,
    val billNumber: String?,
    val dob: String?,
    val admissionDate: String?,
    val joiningDate: String?,
    val className: String?,

    // Photos
    val photo: String?,
    val coverImg: String?,
    val escortPhoto: String?,

    // Personal Details
    val bloodGroup: String?,
    val religion: String?,
    val nationality: String?,
    val transport: String?,
    val birthPlace: String?,
    val house: String?,
    val isBoarding: Boolean?,
    val classification: String?,
    val caste: String?,
    val category: String?,
    val club: String?,

    // Address Details
    val address: String?,
    val city: String?,
    val state: String?,
    val permanentAddress: String?,
    val permanentCity: String?,
    val permanentState: String?,

    // ID Numbers
    val diseNo: String?,
    val aadhaarNumber: String?,
    @SerialName("peN_Number") val penNumber: String?,
    @SerialName("apaaR_ID") val apaarId: String?,
    @SerialName("srN_UMRN_SATSNumber") val srnUmrnSatsNumber: String?,

    // Father's Details
    val fatherName: String?,
    val fatherProfession: String?,
    val fatherDesignationID: Int?,
    val fatherDesignation: String?,
    val fatherDOB: String?,
    val fatherResidentialAddress: String?,
    val fatherOfficeAddress: String?,
    @SerialName("fatherEmail_1") val fatherEmail1: String?,
    @SerialName("fatherEmail_2") val fatherEmail2: String?,
    @SerialName("fatherMob_1") val fatherMob1: String?,
    @SerialName("fatherMob_2") val fatherMob2: String?,
    val fatherAadhaarNumber: String?,
    val fatherPAN: String?,
    val fatherAnnualIncome: Double?,
    val fatherPhoto: String?,
    val fatherProfessionDetail: String?,
    val fatherDesignationDetail: String?,

    // Mother's Details
    val motherName: String?,
    val motherProfession: String?,
    val motherDesignation: String?,
    val motherDOB: String?,
    val motherResidentialAddress: String?,
    val motherOfficeAddress: String?,
    @SerialName("motherEmail_1") val motherEmail1: String?,
    @SerialName("motherEmail_2") val motherEmail2: String?,
    @SerialName("motherMob_1") val motherMob1: String?,
    @SerialName("motherMob_2") val motherMob2: String?,
    val motherAadhaarNumber: String?,
    val motherPAN: String?,
    val motherAnnualIncome: Double?,
    val motherPhoto: String?,
    val motherProfessionDetail: String?,
    val motherDesignationDetail: String?,

    // Contact Details
    val contactPerson: String?,
    val parentStaus: String?,
    val contactMobile: String?,
    val contactEmailID: String?,
    val studentEmail: String?,
    val parentAnniversaryDate: String?,
    val additionalMobile: String?,

    // Misc
    val canChangeCoverImg: Boolean?,
    val coverImgReq: Int?,
    val previousSchoolDTL: String?
)

@Serializable
@InternalSerializationApi
data class SiblingDetail(
    val stID: Int,
    val name: String?,
    val gender: String?,
    @SerialName("class") val siblingClass: String?,
    val rollNumber: String?,
    val admissionNumber: String?,
    val dob: String?,
    val fatherName: String?,
    val contactPerson: String?,
    val contactMob: String?,
    val photo: String?,
    val houseID: Int?,
    val houseName: String?,
    val clubID: Int?,
    val clubName: String?,
    val fatherPhoto: String?,
    val motherPhoto: String?,
    val escortPhoto: String?,
    val isSelected: Boolean?
)

@Serializable
@InternalSerializationApi
data class AcademicYear(
    val yrID: Int,
    val session: String,
    val isCur: Boolean,
    val startDate: String,
    val endDate: String
)

@Serializable
@InternalSerializationApi
data class AttendanceDetail(
    val isLateEnabled: Boolean?,
    val isHalfdayEnabled: Boolean?,
    val working: Int?,
    val present: Int?,
    @SerialName("present_HD") val presentHD: Double?,
    val wh: Int?,
    val totalPresent: Int?,
    @SerialName("totalPresent_HD") val totalPresentHD: Double?,
    val absent: Int?,
    val leave: Int?,
    val late: Int?,
    val summaryAttendance: List<MonthlyAttendance>?
)

@Serializable
@InternalSerializationApi
data class MonthlyAttendance(
    val monthID: Int,
    val month: String,
    val year: Int,
    val startDate: String,
    val endDate: String,
    val working: Int,
    val present: Int,
    @SerialName("present_HD") val presentHD: Double,
    val wh: Int,
    val totalPresent: Int,
    @SerialName("totalPresent_HD") val totalPresentHD: Double,
    val absent: Int,
    val leave: Int,
    val late: Int
)

@Serializable
@InternalSerializationApi
data class AcademicPerformance(
    val overallGrade: String?,
    val examResults: List<ExamResult>?
)

@Serializable
@InternalSerializationApi
data class ExamResult(
    val examName: String?,
    val totalMarks: Double?,
    val obtainedMarks: Double?,
    val grade: String?,
    val rank: Int?
)

@Serializable
@InternalSerializationApi
data class FeeSummary(
    val errorCode: Int?,
    val status: String?,
    val message: String?,
    val feeInstallment: List<FeeInstallment>?,
        val advanceAmount: Double?,
    val totalActualFee: Double?,
    val totalConcession: Double?,
    val totalReceived: Double?,
    val totalOutstanding: Double?
)

@Serializable
@InternalSerializationApi
data class FeeInstallment(
    val installment: String?,
    val actualFee: Double?,
    val concession: Double?,
    val received: Double?,
    val outstanding: Double?
)


@Serializable
@InternalSerializationApi
data class LibraryDetail(
    val issued: Int?,
    val returned: Int?,
    val pending: Int?,
    val duesAMT: Double?,
    val waiveAMT: Double?,
    val paidAMT: Double?,
    val pendingAMT: Double?,
    val libraryTransaction: List<LibraryTransaction>?,
    val libraryFineDTL: List<LibraryFine>?
)

@Serializable
@InternalSerializationApi
data class LibraryTransaction(
    val bookName: String?,
    val fineAmount: Int?,
    val status: String?
)

@Serializable
@InternalSerializationApi
data class LibraryFine(
    val bookName: String?,
    val fineAmount: Double?,
    val status: String?
)

@Serializable
@InternalSerializationApi
data class TransportDetails(
    val transportType: String?,
    val transportTypeID: Int?,
    val vehicleTypeID: Int?,
    val vehicleNumber: String?,
    val vehicleType: String?,
    val driverName: String?,
    val driverMob: String?,
    val driverAdd: String?,
    val driverAadharNumber: String?,
    val driverVoterIDNo: String?,
    val driverDrivingLNo: String?,
    val driverClearanceNo: String?,
    val isLadyGuardAvailabile: Boolean?,
    val transporterName: String?,
    val transporterMob: String?,
    val transporterAdd: String?,
    val transporterAadharNumber: String?,
    val transporterVoterIDNo: String?,
    val transporterDrivingLNo: String?,
    val routeNumber: String?,
    val stopName: String?,
    val vehicleUsingFrom: String?,
    val schoolTransport: SchoolTransport?
)

@Serializable
@InternalSerializationApi
data class SchoolTransport(
    val vehicleType: String?,
    val vehicleName: String?,
    val vehicleNumber: String?,
    val driverName: String?,
    val driverMob: String?,
    val routeNo: String?,
    val stopName: String?,
    val routeInchargeName: String?,
    val routeInchargeMobile: String?
)

@Serializable
@InternalSerializationApi
data class MedicineIssued(
    val medicine: String?,
    val qty: Int?,
    val receiptDate: String?,
    val inTime: String?,
    val outTime: String?,
    val reasontoVisitInfirmary: String?,
    val diagnosis: String?,
    val remark: String?,
    val attendedBy: String?,
    val informedParent: String?
)

@Serializable
@InternalSerializationApi
data class Infraction(
    val stID: Int?,
    val studentName: String?,
    @SerialName("class") val studentClass: String?,
    val admissionNo: String?,
    val rollNumber: String?,
    val photo: String?,
    val infraction: String?,
    val subInfraction: String?,
    val instance: String?,
    val consequences: String?,
    val consequencesAttachment: String?,
    val point: Int?,
    val infractionOn: String?,
    val correctiveAction: String?,
    val staffName: String?,
    val designation: String?,
    val stffPhoto: String?,
    val canDelete: Boolean?,
    val isComplianceActive: Boolean?,
    val compliance: String?,
    val complianceAttachment: String?,
    val isCompModified: Boolean?,
    val compCreatedBy: String?,
    val compCreatedOn: String?,
    val showResolvedButton: Boolean?,
    val isResolved: Boolean?,
    val resolvedOn: String?
)

@Serializable
@InternalSerializationApi
data class Appreciation(
    val stID: Int?,
    val studentName: String?,
    @SerialName("class") val studentClass: String?,
    val admissionNo: String?,
    val rollNumber: String?,
    val photo: String?,
    val appreciation: String?,
    val subAppreciation: String?,
    val instance: Int?,
    val reward: String?,
    val point: Int?,
    val appreciationOn: String?,
    val remark: String?,
    val staffName: String?,
    val designation: String?,
    val stffPhoto: String?,
    val canDelete: Boolean?
)

@Serializable
@InternalSerializationApi
data class MedicalCard(
    // Vaccinations
    val bcg: String?,
    val diphtheria: String?,
    val dptBooster: String?,
    val whoopingCough: String?,
    val tetanus: String?,
    val measles: String?,
    val mmr: String?,
    @SerialName("chicken_pox") val chickenPox: String?,
    val hepatitisA: String?,
    val hepatitisB: String?,
    val typhoid: String?,

    // COVID Vaccination
    val covidDose1: String?,
    val covidDose2: String?,
    val covidBoosterDose: String?,

    // Medical History
    val allergies: String?,
    val surgeryUndergoneInthePast: String?,
    val specificPastDisease: String?,
    val childRegularMedication: String?
)

@Serializable
@InternalSerializationApi
data class ReportCardDetail(
    val classID: Int,
    val className: String?,
    val yrID: Int,
    val academicYear: String?,
    val isCur: Int?,
    val reportCards: List<ReportCard>?
)

@Serializable
@InternalSerializationApi
data class ReportCard(
    val examName: String?,
    val viewMode: Int?,
    val fileName: String?,
    val fileSize: String?,
    val frontFileName: String?,
    val frontFileSize: String?,
    val backFileName: String?,
    val backFileSize: String?,
    val updatedOn: String?
)

@Serializable
@InternalSerializationApi
data class AttendanceYearResponse(
    val errorCode: Int,
    val status: String?,
    val message: String?,
    @SerialName("attDTL") val attDTL: AttendanceDetail?
)

@Serializable
@InternalSerializationApi
data class MonthlyAttendanceDetailResponse(
    val errorCode: Int,
    val status: String?,
    val message: String?,
    val presentDays: Int?,
    val workingDays: Int?,
    val attendance: List<DailyAttendance>?
)

@Serializable
@InternalSerializationApi
data class DailyAttendance(
    val attDate: String,
    val dayName: String?,
    val status: Int,
    val halfday: Int?,
    val isLate: Boolean?
)

enum class ProfileSection(
    val apiName: String,
    val displayName: String,
    val tabOrder: Int
) {
    PERSONAL_DETAILS("PersonalDetails", "Personal details", 0),
    ATTENDANCE("Attendance", "Attendance", 1),
    ACADEMIC_PERFORMANCE("AcademicPerformance", "Academic Performance", 2),
    FEE_DETAILS("FeeDetails", "Fee details", 3),
    INFIRMARY("Infirmary", "Infirmary", 4),
    LIBRARY("Library", "Library", 5),
    TRANSPORT("Transport", "Transport", 6),
    INFRACTION("Infraction", "Infraction", 7),
    APPRECIATION("Appreciation", "Appreciation", 8),
    MEDICAL_CARD("MedicalCard", "Medical Card", 9),
    REPORT_CARD("ReportCard", "Report Card", 10),
    SESSION_LOG("SessionLog", "Session Log", 11);

    companion object {
        fun fromApiName(name: String): ProfileSection? {
            return values().find { it.apiName == name }
        }
    }
}

enum class PersonalDetailSection(
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
    FATHER_DETAILS(
        "Father's details",
        "person.circle",
        0xFF2196F3,
        0x262196F3
    ),
    MOTHER_DETAILS(
        "Mother's details",
        "person.circle.fill",
        0xFFE91E63,
        0x26E91E63
    ),
    SIBLING_DETAILS(
        "Sibling's details",
        "person.2.fill",
        0xFF00BCD4,
        0x2600BCD4
    ),
    OTHER_DETAILS(
        "Other details",
        "exclamationmark.circle",
        0xFFFF9800,
        0x26FF9800
    )
}
