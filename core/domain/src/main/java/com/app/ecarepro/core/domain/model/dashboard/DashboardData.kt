package com.app.ecarepro.core.domain.model.dashboard

data class DashboardCard(
    val heading: String,
    val data: String,
    val color: String,
    val tinyIcon: String,
    val menuID: Int?,
    val chMenuID: Int?,
    val sbChMenuID: Int?,
    val value1: Int?,
    val value2: Int?,
    val data1: String?,
    val data2: String?,
)

data class DashboardActivity(
    val id: Int,
    val title: String,
    val isWorking: Boolean,
    val fromDate: String,
    val duration: Int,
)

data class DashboardQuestionnaire(
    val qid: Int,
    val question: String,
    val likes: Int,
    val totalAnswer: Int,
    val photo: String,
    val updatedOn: String,
    val updatedBy: String,
    val isAnswered: Boolean,
    val isVerified: Boolean,
)

data class StudentBirthday(
    val studentName: String,
    val birthdayOn: String,
    val photo: String,
    val isToday: Boolean,
)

data class BirthdaySummary(
    val heading: String,
    val data: String,
)

data class TimetablePeriod(
    val period: Int,
    val subject: String,
    val time: String,
    val teachBy: String,
    val bookCover: String,
)

data class ClassSummary(
    val className: String,
    val present: Int,
    val absent: Int,
    val leave: Int,
    val late: Int,
)

data class StaffAttendance(
    val present: Int,
    val absent: Int,
    val onLeave: Int,
    val total: Int,
)

data class BankBalance(
    val accountName: String,
    val balance: String,
)

data class FeeDefaulterSummary(
    val totalStudent: Int,
    val defaulterCount: Int,
    val amount: String,
)

data class LibraryData(
    val dueFine: Int,
    val fineCollected: Int,
    val totalBooks: Int,
    val circulatedBooks: Int,
    val discardedBooks: Int,
    val newsSubscribed: Int,
    val magazineSubscribed: Int,
)

data class AdmissionComparison(
    val previousSession: String,
    val currentSession: String,
    val nextSession: String,
    val isNextSessionActive: Boolean,
    val standards: List<AdmissionStandard>,
)

data class AdmissionStandard(
    val standard: String,
    val previousSession: Int,
    val currentSession: Int,
    val nextSession: Int,
)

data class StatItem(
    val label: String,
    val value: Int,
)

data class FeeCollectionData(
    val estimate: String,
    val received: String,
    val concession: String,
    val due: String,
)

data class ModeWiseData(
    val totalCollection: Double,
    val modes: List<CollectionMode>,
)

data class CollectionMode(
    val mode: String,
    val amount: Double,
    val color: String,
)

data class DashFeedItem(
    val menuID: Int?,
    val chMenuID: Int?,
    val sbChMenuID: Int?,
    val module: String,
    val id: String,
    val caption: String,
    val updatedOn: String,
)

data class DashboardResponse(
    val visibility: DashboardVisibility,
    val proCards: List<DashboardCard>,
    val cards: List<DashboardCard>,
    val activities: List<DashboardActivity>,
    val questionnaires: List<DashboardQuestionnaire>,
    val studentBirthdays: List<StudentBirthday>,
    val birthdaySummaries: List<BirthdaySummary>,
    val timetablePeriods: List<TimetablePeriod>,
    val classSummaries: List<ClassSummary>,
    val staffAttendance: StaffAttendance?,
    val bankBalances: List<BankBalance>,
    val feeDefaulter: FeeDefaulterSummary?,
    val library: LibraryData?,
    val admissionComparison: AdmissionComparison?,
    val stuStatusStats: List<StatItem>,
    val stuCategoryStats: List<StatItem>,
    val stuReligionStats: List<StatItem>,
    val admissionModeStats: List<StatItem>,
    val sessionStartDate: String,
    val sessionEndDate: String,
)
