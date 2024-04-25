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
    @SerializedName("status")
    val status: String?,
    @SerializedName("timetable")
    val timetable: List<Timetable>?,
    @SerializedName("upcomingActivities")
    val upcomingActivities: List<Any>?,
    @SerializedName("collectionModeWise")
    val collectionModeWise: CollectionModeWise?,
    @SerializedName("feeCollection")
    val feeCollection: FeeCollection?,
    @SerializedName("feeDafaulter")
    val feeDafaulter: FeeDefaulter?,
    @SerializedName("bankBalance")
    val bankBalance: List<BankBalance>?,
    @SerializedName("staffAttendanceSummary")
    val staffAttendanceSummary: StaffAttendance?,
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
) : Serializable

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
    val link: String?
)

data class Card(
    @SerializedName("color")
    val color: String?,
    @SerializedName("data")
    val data: String?,
    @SerializedName("heading")
    val heading: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("data_1")
    val data1: String?,
    @SerializedName("data_2")
    val data2: String?
) : Serializable {
    fun getIconUrl() = BASE_URL_COM + icon
}

data class Timetable(
    @SerializedName("className")
    val className: String?,
    @SerializedName("period")
    val period: Int?,
    @SerializedName("subject")
    val subject: String?,
    @SerializedName("time")
    val time: String?
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
    val concession: Double?,
    @SerializedName("due")
    val due: Double?,
    @SerializedName("estimate")
    val estimate: Double?,
    @SerializedName("feeTypes")
    val feeTypes: List<FeeType>?,
    @SerializedName("installmentCollections")
    val installmentCollections: List<InstallmentCollection>?,
    @SerializedName("received")
    val received: Double?
)

data class FeeType(
    @SerializedName("feeTypeID")
    val feeTypeID: Int?,
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

