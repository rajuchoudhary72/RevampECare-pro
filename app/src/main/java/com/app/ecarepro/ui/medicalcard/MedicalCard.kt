package com.app.ecarepro.ui.medicalcard

data class MedicalCard(
    val drugAllergy: String?,
    val emrAddress: String?,
    val emrContact: String?,
    val emrName: String?,
    val isPhysicallyFitForGame: Boolean,
    val sufferingFrom: String?
)