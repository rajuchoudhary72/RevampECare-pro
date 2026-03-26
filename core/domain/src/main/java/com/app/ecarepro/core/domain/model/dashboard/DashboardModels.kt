package com.app.ecarepro.core.domain.model.dashboard

data class DashboardOverview(
    val totalStudents: Int,
    val girlsCount: Int,
    val boysCount: Int,
    val assignmentCount: Int,
    val newMessagesCount: Int,
    val newNoticesCount: Int,
    val workloadCount: Int,
    val totalStaff: Int,
    val femaleStaff: Int,
    val maleStaff: Int,
    val financialOverview: DashboardFinancialOverview,
    val libraryStats: LibraryStats,
    val admissionsStats: AdmissionsStats,
)

data class DashboardFinancialOverview(
    val estimatedCollection: Double,
    val due: Double,
    val received: Double,
    val concession: Double,
    val bankBalance: Double,
    val bankItems: List<BankBalanceItem>,
    val feeDefaulterAmount: Double,
    val feeDefaulterCount: Int,
    val feeDefaulterTotalStudents: Int,
    val modeOfCollectionTotal: Double,
    val cashCollection: Double,
    val chequeCollection: Double,
    val upiCollection: Double,
    val othersCollection: Double,
)

data class BankBalanceItem(
    val bankName: String,
    val amount: Double,
    val progress: Float,
    val colorHex: String,
)

data class LibraryStats(
    val finePending: Double,
    val fineCollected: Double,
    val totalBooks: Int,
    val circulated: Int,
    val discarded: Int,
    val newsSubscribed: Int,
    val magazineSubscribed: Int,
)

data class AdmissionsStats(
    val studentCategories: List<LabelCountItem>,
    val studentReligions: List<LabelCountItem>,
    val admissionModes: List<LabelCountItem>,
)

data class LabelCountItem(
    val label: String,
    val count: Int,
)
