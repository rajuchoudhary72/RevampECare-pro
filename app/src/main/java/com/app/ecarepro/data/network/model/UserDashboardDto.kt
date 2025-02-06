package com.app.ecarepro.data.network.model

import com.app.ecarepro.utils.Constant.Companion.BASE_URL_COM
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UserDashboardDto(
    @SerializedName("attendanceSummury")
    val attendanceSummary: AttendanceSummary?,
    @SerializedName("birthDayCards")
    val birthDayCards: List<BirthDayCard>?,
    @SerializedName("cards")
    val cards: List<Card>?,
    @SerializedName("proCards")
    val proCards: List<Card>?,
    @SerializedName("dashboardButtons")
    val dashboardButtons: List<DashboardButtons >?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("showActivities")
    val showActivities: Boolean?,
    @SerializedName("showAttendanceSummery")
    val showAttendanceSummery: Boolean?,
    @SerializedName("showCollectionModeWise")
    val showCollectionModeWise: Boolean?,
    @SerializedName("showTeacherWorkLoad")
    val showTeacherWorkLoad: Boolean?,
    @SerializedName("showQuestionnaire")
    val showQuestionnaire: Boolean?,
    @SerializedName("showBDayCards")
    val showBDayCards: Boolean?,
    @SerializedName("showCards")
    val showCards: Boolean?,
    @SerializedName("showProCards")
    val showProCards: Boolean?,
    @SerializedName("showTimetable")
    val showTimetable: Boolean?,
    @SerializedName("showFeeCollection")
    val showFeeCollection: Boolean?,
    @SerializedName("showBankBalnce")
    val showBankBalnce: Boolean?,
    @SerializedName("showFeeDafaulter")
    val showFeeDafaulter: Boolean?,
    @SerializedName("showStfAttendanceSummary")
    val showStfAttendanceSummary: Boolean?,
    @SerializedName("showAdmissionComparison")
    val showAdmissionComparison: Boolean?,
    @SerializedName("showStuStatusWiseStatistics")
    val showStuStatusWiseStatistics: Boolean?,
    @SerializedName("showAdmissionModeComparison")
    val showAdmissionModeComparison: Boolean?,
    @SerializedName("showStuCategoryStatistics")
    val showStuCategoryStatistics: Boolean?,
    @SerializedName("showStuReligionWiseStatistics")
    val showStuReligionWiseStatistics: Boolean?,
    @SerializedName("showLibraryDTL")
    val showLibraryDTL: Boolean?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("sessionStartDate")
    val sessionStartDate: String?,
    @SerializedName("sessionEndDate")
    val sessionEndDate: String?,
    @SerializedName("collectionStartDate")
    val collectionStartDate: String?,
    @SerializedName("collectionEndDate")
    val collectionEndDate: String?,
    @SerializedName("timetable")
    val timetable: List<Timetable>?,
    @SerializedName("upcomingActivities")
    val upcomingActivities: List<Activity>?,
    @SerializedName("collectionModeWise")
    val collectionModeWise: CollectionModeWise?,
    @SerializedName("feeCollection")
    val feeCollection: FeeCollection?,
    @SerializedName("feeDafaulter")
    val feeDafaulter: FeeDefaulter?,
    @SerializedName("bankBalance")
    val bankBalance: List<BankBalance>?,
    @SerializedName("stuStatusWiseStatistics")
    val stuStatusWiseStatistics: List<StatusWiseStatistics>?,
    @SerializedName("admissionModeComparison")
    val admissionModeComparison: List<DataValue>?,
    @SerializedName("teacherWorkLoad")
    val teacherWorkLoad: List<Workload>?,
    @SerializedName("stuReligionWiseStatistics")
    val stuReligionWiseStatistics: List<DataValue>?,

    @SerializedName("stuCategoryWiseStatistics")
    val stuCategoryWiseStatistics: List<DataValue>?,


    @SerializedName("questionnaire")
    val questionnaire: List<Questionnaire>?,
    @SerializedName("libraryDTL")
    val libraryDTL: LibraryDetails?,
    @SerializedName("staffAttendanceSummary")
    val staffAttendanceSummary: StaffAttendance?,
    @SerializedName("admissionComparison")
    val admissionComparison: AdmissionComparison?,
)
data class Workload(
    @SerializedName("id")
    val id: String?,
    @SerializedName("periodCount")
    val periodCount: Int?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("teacherName")
    val teacherName: String?
)
data class Activity(
    @SerializedName("fromDate")
    val fromDate: String?,
    @SerializedName("id")
    val id: Any?,
    @SerializedName("isWorking")
    val isWorking: Boolean?,
    @SerializedName("title")
    val title: String?
)
data class AttendanceSummary(
    @SerializedName("classSummary")
    val classSummary: List<ClassSummary>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("isLateEnabled")
    val isLateEnabled: Boolean?,
    @SerializedName("message")
    val message: Any?,
    @SerializedName("status")
    val status: Any?,
    @SerializedName("totalAbsent")
    val totalAbsent: Int?,
    @SerializedName("totalLate")
    val totalLate: Int?,
    @SerializedName("totalLeave")
    val totalLeave: Int?,
    @SerializedName("totalPresent")
    val totalPresent: Int?
) : Serializable {
    fun totalStudent() =
        (totalPresent ?: 0) + (totalAbsent ?: 0) + (totalLeave ?: 0) + (totalLate ?: 0)
}

data class BirthDayCard(
    @SerializedName("color")
    val color: Any?,
    @SerializedName("data")
    val `data`: String?,
    @SerializedName("heading")
    val heading: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("utype")
    val utype: Int,
    @SerializedName("rtype")
    val rtype: Int,
    @SerializedName("date")
    val date: String?,
    @SerializedName("month")
    val month: String?
) {
    fun getIconUrl() = BASE_URL_COM + icon
}

data class Card(
    @SerializedName("color")
    val color: String?,
    @SerializedName("data")
    val data: String?,
    @SerializedName("heading")
    val heading: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("tinyIcon")
    val tinyIcon: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("data_1")
    val data1: String?,
    @SerializedName("data_2")
    val data2: String?,
    @SerializedName("menuID")
    val menuID: Int,
    @SerializedName("chMenuID")
    val chMenuID: Int,
    @SerializedName("sbChMenuID")
    val sbChMenuID: Int
) : Serializable {
    fun getIconUrl() = image
}

data class Timetable(
    @SerializedName("className")
    val className: String?,
    @SerializedName("period")
    val period: Int?,
    @SerializedName("subject")
    val subject: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("teachBy")
    val teachBy: String?
)

data class ClassSummary(
    @SerializedName("absent")
    val absent: Int?,
    @SerializedName("classID")
    val classID: Int?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isMarked")
    val isMarked: Boolean?,
    @SerializedName("late")
    val late: Int?,
    @SerializedName("leave")
    val leave: Int?,
    @SerializedName("present")
    val present: Int?
) : Serializable

data class CollectionModeWise(
    @SerializedName("transactionDetails")
    val transactionDetails: List<TransactionDetail>?
) {
    fun total() = transactionDetails?.sumOf { it.amount ?: 0.0 }
}

data class TransactionDetail(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("color")
    val color: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("mode")
    val mode: String?
) {
    fun getIconUrl() = BASE_URL_COM + icon
}

data class FeeCollection(
    @SerializedName("concession")
    val concession: String?,
    @SerializedName("due")
    val due: String?,
    @SerializedName("estimate")
    val estimate: String?,
    @SerializedName("feeTypes")
    val feeTypes: List<FeeType>?,
    @SerializedName("installmentCollections")
    val installmentCollections: List<InstallmentCollection>?,
    @SerializedName("received")
    val received: String?
)

data class FeeType(
    @SerializedName("feeTypeID")
    val feeTypeID: Int,
    @SerializedName("feeTypeName")
    val feeTypeName: String?
)

data class InstallmentCollection(
    @SerializedName("concession")
    val concession: Double?,
    @SerializedName("due")
    val due: Double?,
    @SerializedName("estimate")
    val estimate: Double?,
    @SerializedName("installment")
    val installment: String?,
    @SerializedName("received")
    val received: Double?
)

data class FeeDefaulter(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("dafaulterCount")
    val dafaulterCount: Int?,
    @SerializedName("totalStudent")
    val totalStudent: Int?
)

data class BankBalance(
    @SerializedName("accountName")
    val accountName: String?,
    @SerializedName("balnce")
    val balnce: String?
)

data class StaffAttendance(
    @SerializedName("absent")
    val absent: Int?,
    @SerializedName("onLeave")
    val onLeave: Int?,
    @SerializedName("present")
    val present: Int?,
    @SerializedName("total")
    val total: Int?
)

data class AdmissionComparison(
    @SerializedName("currentSession")
    val currentSession: String?,
    @SerializedName("previousSession")
    val previousSession: String?,
    @SerializedName("studentCountStandardWise")
    val studentCountStandardWise: List<StudentCountStandardWise>?
)

data class StudentCountStandardWise(
    @SerializedName("currentSession")
    val currentSession: Int?,
    @SerializedName("previousSession")
    val previousSession: Int?,
    @SerializedName("standard")
    val standard: String?
)


data class StatusWiseStatistics(
    @SerializedName("data")
    val `data`: String?,
    @SerializedName("value")
    val value: Int?
)

data class DataValue(
    @SerializedName("data")
    val `data`: String?,
    @SerializedName("value")
    val value: Int?
)

data class LibraryDetails(
    @SerializedName("circulatedBooks")
    val circulatedBooks: Int?,
    @SerializedName("discardedBooks")
    val discardedBooks: Int?,
    @SerializedName("dueFine")
    val dueFine: Int?,
    @SerializedName("fineCollected")
    val fineCollected: Int?,
    @SerializedName("magzineSubscribed")
    val magzineSubscribed: Int?,
    @SerializedName("newsSubscribed")
    val newsSubscribed: Int?,
    @SerializedName("totalBooks")
    val totalBooks: Int?
)

data class Questionnaire(
    @SerializedName("isAnswered")
    val isAnswered: Boolean?,
    @SerializedName("isILike")
    val isILike: Boolean?,
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    @SerializedName("likes")
    val likes: Int?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("qType")
    val qType: Int?,
    @SerializedName("qid")
    val qid: Int?,
    @SerializedName("que")
    val que: String?,
    @SerializedName("queImg")
    val queImg: Any?,
    @SerializedName("totalAnswer")
    val totalAnswer: Int?,
    @SerializedName("updatedBy")
    val updatedBy: String?,
    @SerializedName("updatedOn")
    val updatedOn: String?,
    @SerializedName("userID")
    val userID: Int?,
    @SerializedName("userType")
    val userType: Int?
)


data class DashboardButtons(


    @SerializedName("buttonName")
    val buttonName: String?,
    @SerializedName("isShow")
    val isShow: Boolean?
)
