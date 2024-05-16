package com.app.ecarepro.ui.medicalcard

data class MedicalCardResponse(
    val errorCode: Int?,
    val medicalCard: MedicalCard?,
    val message: String?,
    val status: String?
)