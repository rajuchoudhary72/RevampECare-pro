package com.app.ecarepro.model

data class FeeInstallment(
    val actualFee: Double,
    val concession: Double,
    val installment: String,
    val outstanding: Double,
    val received: Double
)