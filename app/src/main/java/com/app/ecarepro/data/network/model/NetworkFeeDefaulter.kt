package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.FeeDefaulter
import com.app.ecarepro.model.FeeType
import com.app.ecarepro.model.Installment

data class NetworkFeeDefaulter(
    val errorCode: Int,
    val feeDefaulters: List<FeeDefaulter>?,
    val feeTypes: List<FeeType>?,
    val installments: List<Installment>?,
    val message: String,
    val status: String,
    val totalAmount: String,
    val totalDefaulter: Int,
    val totalStudent: Int
)