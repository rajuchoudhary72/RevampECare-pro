package com.app.ecarepro.core.domain.model

data class Student(
    val stID: Int,
    val classID: Int,
    val recipientName: String?,
    val recipientPhoto: String?,
    val studentName: String?,
    val className: String?,
    val rollNumber: String?,
    val admissionNumber: String?,
    val dob: String?,
    val fatherName: String?,
    val contactPerson: String?,
    val mobileNumber: String?,
    val emailID: String?,
    val msUser: String?,
    val msPassword: String?,
    val isSelected: Boolean = false,
)