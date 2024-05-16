package com.app.ecarepro.model

data class UpdateMedicalCardRequest(
    val drugAllergy: String?,
    val emrAddress: String?,
    val emrContact: String?,
    val emrName: String?,
    val isPhysicallyFitForGame: Boolean?,
    val sufferingFrom: String?
)