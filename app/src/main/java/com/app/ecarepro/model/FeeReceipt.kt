package com.app.ecarepro.model

data class FeeReceipt(
    val recid: Int?,
    val recno: String?,
    val recdate: String?,
    val paidamt: String?,
    val paymode: String?,
    val paymodekey: String?,
    val paymodevalue: String?,
    val installment: String?,
    val feetypeid: String?
)
