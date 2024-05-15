package com.app.ecarepro.ui.medicine_issue

data class MedicineIsuueModel(
    val admissionNo: Any?,
    val className: Any?,
    val coverImage: String?,
    val designation: String?,
    val errorCode: Int?,
    val medicineIssued: List<MedicineIssued>,
    val message: String?,
    val name: String?,
    val photo: String?,
    val status: String?
)