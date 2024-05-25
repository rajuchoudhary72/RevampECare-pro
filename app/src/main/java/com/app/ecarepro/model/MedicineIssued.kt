package com.app.ecarepro.model

data class MedicineIssued(
    val attendedBy: String,
    val diagnosis: String,
    val inTime: String,
    val informedParent: String,
    val medicine: String,
    val outTime: String,
    val qty: Int,
    val reasontoVisitInfirmary: String,
    val receiptDate: String,
    val remark: String
)