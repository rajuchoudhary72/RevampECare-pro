package com.app.ecarepro.core.domain.model.fee

data class FeeCollection(
    val date: String,
    val amount: Double,
    val formattedDate: String,
    val dayOfWeek: String,
    val formattedAmount: String,
)

data class FeeSessionDomain(
    val yrid: Int,
    val yearname: String,
    val isActive: Boolean,
)

data class FeeReceiptDomain(
    val recId: String,
    val recDate: String,
    val paidAmount: String,
    val receiptNumber: String,
    val paymentMode: String,
    val installment: String,
    val stId: Int,
)

data class FeeReportFiltersDomain(
    val classes: List<FilterItemDomain>,
    val schools: List<FilterItemDomain>,
    val feeTypes: List<FilterItemDomain>,
    val installments: List<FilterItemDomain>,
    val sections: List<FilterItemDomain>,
)

data class FilterItemDomain(val id: String, val name: String)

data class DefaulterDomain(
    val nameWithClass: String,
    val admNo: String,
    val contactNo: String,
    val amount: Double,
    val installmentName: String,
)

data class EstimateDomain(
    val headName: String,
    val actualAmount: Double,
    val concession: Double,
    val receivedAmount: Double,
    val duesAmount: Double,
)
