package com.app.ecarepro.core.network.model.dashboard

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkDashboardResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("showProgressStats") val showProgressStats: Boolean? = null,
    @SerialName("showProCards") val showProCards: Boolean? = null,
    @SerialName("showCards") val showCards: Boolean? = null,
    @SerialName("showFeeDafaulter") val showFeeDafaulter: Boolean? = null,
    @SerialName("showBDayCards") val showBDayCards: Boolean? = null,
    @SerialName("showStuCategoryStatistics") val showStuCategoryStatistics: Boolean? = null,
    @SerialName("showAttendanceSummery") val showAttendanceSummery: Boolean? = null,
    @SerialName("showFeed") val showFeed: Boolean? = null,
    @SerialName("showStuReligionWiseStatistics") val showStuReligionWiseStatistics: Boolean? = null,
    @SerialName("showLibraryDTL") val showLibraryDTL: Boolean? = null,
    @SerialName("showStfAttendanceSummary") val showStfAttendanceSummary: Boolean? = null,
    @SerialName("showBankBalnce") val showBankBalnce: Boolean? = null,
    @SerialName("showTeacherTimetable") val showTeacherTimetable: Boolean? = null,
    @SerialName("showFeeCollection") val showFeeCollection: Boolean? = null,
    @SerialName("showAdmissionComparison") val showAdmissionComparison: Boolean? = null,
    @SerialName("showAdmissionModeComparison") val showAdmissionModeComparison: Boolean? = null,
    @SerialName("showClassTimetable") val showClassTimetable: Boolean? = null,
    @SerialName("showStuStatusWiseStatistics") val showStuStatusWiseStatistics: Boolean? = null,
    @SerialName("showActivities") val showActivities: Boolean? = null,
    @SerialName("showQuestionnaire") val showQuestionnaire: Boolean? = null,
    @SerialName("showCollectionModeWise") val showCollectionModeWise: Boolean? = null,
    @SerialName("showstudentBDayCards") val showstudentBDayCards: Boolean? = null,
    @SerialName("showTeacherWorkLoad") val showTeacherWorkLoad: Boolean? = null,
    @SerialName("showLibraryFineStatus") val showLibraryFineStatus: Boolean? = null,
    @SerialName("dashboardButtons") val dashboardButtons: List<NetworkDashboardButton>? = null,
    @SerialName("cards") val cards: List<NetworkDashboardCard>? = null,
    @SerialName("proCards") val proCards: List<NetworkDashboardCard>? = null,
    @SerialName("progressStats") val progressStats: NetworkProgressStats? = null,
    @SerialName("upcomingActivities") val upcomingActivities: List<NetworkDashboardActivity>? = null,
    @SerialName("questionnaire") val questionnaire: List<NetworkDashboardQuestionnaire>? = null,
    @SerialName("studBirthdayCards") val studBirthdayCards: List<NetworkStudentBirthday>? = null,
    @SerialName("birthDayCards") val birthDayCards: List<NetworkBirthdaySummary>? = null,
    @SerialName("classTimetable") val classTimetable: List<NetworkTimetablePeriod>? = null,
    @SerialName("attendanceSummury") val attendanceSummury: NetworkAttendanceSummary? = null,
    @SerialName("staffAttendanceSummary") val staffAttendanceSummary: NetworkStaffAttendance? = null,
    @SerialName("bankBalance") val bankBalance: List<NetworkBankBalance>? = null,
    @SerialName("teacherWorkLoad") val teacherWorkLoad: List<NetworkTeacherWorkload>? = null,
    @SerialName("feeDafaulter") val feeDafaulter: NetworkFeeDefaulterSummary? = null,
    @SerialName("libraryDTL") val libraryDTL: NetworkLibrary? = null,
    @SerialName("admissionComparison") val admissionComparison: NetworkAdmissionComparison? = null,
    @SerialName("admissionModeComparison") val admissionModeComparison: List<NetworkStatItem>? = null,
    @SerialName("stuStatusWiseStatistics") val stuStatusWiseStatistics: List<NetworkStatItem>? = null,
    @SerialName("stuCategoryWiseStatistics") val stuCategoryWiseStatistics: List<NetworkStatItem>? = null,
    @SerialName("stuReligionWiseStatistics") val stuReligionWiseStatistics: List<NetworkStatItem>? = null,
    @SerialName("sessionStartDate") val sessionStartDate: String? = null,
    @SerialName("sessionEndDate") val sessionEndDate: String? = null,
) : NetworkResponse

@Serializable
data class NetworkDashboardButton(
    @SerialName("buttonName") val buttonName: String? = null,
    @SerialName("isShow") val isShow: Boolean? = null,
)

@Serializable
data class NetworkDashboardCard(
    @SerialName("heading") val heading: String? = null,
    @SerialName("data") val data: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("tinyIcon") val tinyIcon: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("link") val link: String? = null,
    @SerialName("menuID") val menuID: Int? = null,
    @SerialName("chMenuID") val chMenuID: Int? = null,
    @SerialName("sbChMenuID") val sbChMenuID: Int? = null,
    @SerialName("data_1") val data1: String? = null,
    @SerialName("data_2") val data2: String? = null,
    @SerialName("value_1") val value1: Int? = null,
    @SerialName("value_2") val value2: Int? = null,
)

@Serializable
data class NetworkProgressStats(
    @SerialName("totalStudent") val totalStudent: Int? = null,
    @SerialName("newAdmission") val newAdmission: Int? = null,
    @SerialName("totalStaff") val totalStaff: Int? = null,
    @SerialName("newStaff") val newStaff: Int? = null,
)

@Serializable
data class NetworkDashboardActivity(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("isWorking") val isWorking: Boolean? = null,
    @SerialName("fromDate") val fromDate: String? = null,
    @SerialName("tillDate") val tillDate: String? = null,
    @SerialName("duration") val duration: Int? = null,
)

@Serializable
data class NetworkDashboardQuestionnaire(
    @SerialName("qid") val qid: Int? = null,
    @SerialName("que") val que: String? = null,
    @SerialName("likes") val likes: Int? = null,
    @SerialName("totalAnswer") val totalAnswer: Int? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("updatedOn") val updatedOn: String? = null,
    @SerialName("updatedBy") val updatedBy: String? = null,
    @SerialName("isAnswered") val isAnswered: Boolean? = null,
    @SerialName("isVerified") val isVerified: Boolean? = null,
    @SerialName("queImg") val queImg: String? = null,
    @SerialName("qType") val qType: Int? = null,
)

@Serializable
data class NetworkStudentBirthday(
    @SerialName("studentName") val studentName: String? = null,
    @SerialName("birthdayOn") val birthdayOn: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("isToday") val isToday: Boolean? = null,
)

@Serializable
data class NetworkBirthdaySummary(
    @SerialName("heading") val heading: String? = null,
    @SerialName("data") val data: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("link") val link: String? = null,
)

@Serializable
data class NetworkTimetablePeriod(
    @SerialName("period") val period: Int? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("teachBy") val teachBy: String? = null,
    @SerialName("bookCover") val bookCover: String? = null,
)

@Serializable
data class NetworkAttendanceSummary(
    @SerialName("classSummary") val classSummary: List<NetworkClassSummary>? = null,
)

@Serializable
data class NetworkClassSummary(
    @SerialName("className") val className: String? = null,
    @SerialName("present") val present: Int? = null,
    @SerialName("absent") val absent: Int? = null,
    @SerialName("leave") val leave: Int? = null,
    @SerialName("late") val late: Int? = null,
)

@Serializable
data class NetworkStaffAttendance(
    @SerialName("present") val present: Int? = null,
    @SerialName("absent") val absent: Int? = null,
    @SerialName("onLeave") val onLeave: Int? = null,
    @SerialName("total") val total: Int? = null,
)

@Serializable
data class NetworkBankBalance(
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("balnce") val balnce: String? = null,
)

@Serializable
data class NetworkTeacherWorkload(
    @SerialName("teacherName") val teacherName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("periodCount") val periodCount: Int? = null,
    @SerialName("photo") val photo: String? = null,
)

@Serializable
data class NetworkFeeDefaulterSummary(
    @SerialName("totalStudent") val totalStudent: Int? = null,
    @SerialName("defaulterCount") val defaulterCount: Int? = null,
    @SerialName("amount") val amount: String? = null,
)

@Serializable
data class NetworkLibrary(
    @SerialName("dueFine") val dueFine: Int? = null,
    @SerialName("fineCollected") val fineCollected: Int? = null,
    @SerialName("totalBooks") val totalBooks: Int? = null,
    @SerialName("circulatedBooks") val circulatedBooks: Int? = null,
    @SerialName("discardedBooks") val discardedBooks: Int? = null,
    @SerialName("newsSubscribed") val newsSubscribed: Int? = null,
    @SerialName("magzineSubscribed") val magzineSubscribed: Int? = null,
)

@Serializable
data class NetworkAdmissionComparison(
    @SerialName("previousSession") val previousSession: String? = null,
    @SerialName("currentSession") val currentSession: String? = null,
    @SerialName("nextSession") val nextSession: String? = null,
    @SerialName("isNextSessionActive") val isNextSessionActive: Boolean? = null,
    @SerialName("studentCountStandardWise") val studentCountStandardWise: List<NetworkAdmissionStandard>? = null,
)

@Serializable
data class NetworkAdmissionStandard(
    @SerialName("standard") val standard: String? = null,
    @SerialName("previousSession") val previousSession: Int? = null,
    @SerialName("currentSession") val currentSession: Int? = null,
    @SerialName("nextSession") val nextSession: Int? = null,
)

@Serializable
data class NetworkStatItem(
    @SerialName("data") val data: String? = null,
    @SerialName("value") val value: Int? = null,
)

@Serializable
data class NetworkFeeCollectionResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("estimate") val estimate: String? = null,
    @SerialName("received") val received: String? = null,
    @SerialName("concession") val concession: String? = null,
    @SerialName("due") val due: String? = null,
) : NetworkResponse

@Serializable
data class NetworkModeWiseCollectionResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("totalCollection") val totalCollection: Double? = null,
    @SerialName("transactionDetails") val transactionDetails: List<NetworkTransactionDetail>? = null,
) : NetworkResponse

@Serializable
data class NetworkTransactionDetail(
    @SerialName("mode") val mode: String? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("color") val color: String? = null,
)

@Serializable
data class NetworkDashFeedResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("total") val total: Int? = null,
    @SerialName("updates") val updates: List<NetworkDashFeedItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkDashFeedItem(
    @SerialName("menuID") val menuID: Int? = null,
    @SerialName("chMenuID") val chMenuID: Int? = null,
    @SerialName("sbChMenuID") val sbChMenuID: Int? = null,
    @SerialName("module") val module: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("caption") val caption: String? = null,
    @SerialName("hasAttachment") val hasAttachment: Boolean? = null,
    @SerialName("updtedOn") val updtedOn: String? = null,
)
