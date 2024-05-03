package com.app.ecarepro.model

data class PaidHistory(
    val amount: Double,
    val installment: String,
    val paidOn: String,
    val payMode: String,
    val recNo: String
)