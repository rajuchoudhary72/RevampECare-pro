package com.app.ecarepro.ui.medicalcard.medical_class

data class StudentMedicalCardResponse(
    val errorCode: Int?,
    val immunizationRecords: ImmunizationRecords?,
    val message: String?,
    val profile: Profile?,
    val status: String?
)