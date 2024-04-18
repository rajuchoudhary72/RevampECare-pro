package com.app.ecarepro.model

data class FeeSummery(
    val advanceAmount: Double,
    val errorCode: Int,
    val feeInstallment: List<FeeInstallment>,
    val message: Any,
    val paidHistory: List<PaidHistory>,
    val status: Any,
    val totalActualFee: Double,
    val totalConcession: Double,
    val totalOutstanding: Double,
    val totalReceived: Double
)