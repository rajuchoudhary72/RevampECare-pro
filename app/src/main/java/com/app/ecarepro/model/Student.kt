package com.app.ecarepro.model

data class Student(
    val admissionNumber: String,
    val `class`: String,
    val contactMob: String,
    val contactPerson: String,
    val dob: String,
    val fatherName: String,
    val isSelected: Boolean,
    val name: String,
    val photo: String,
    val rollNumber: String,
    val stID: Int
) {
    override fun toString(): String {
        return "$name $rollNumber $admissionNumber"
    }

    fun nameAndClass() = "$name (${`class`})"

    fun details() = "<b>Admission No:</b> $admissionNumber<br>" +
            "<b>Father Name:</b> $fatherName<br>" +
            "<b>Contact Number:</b> $contactMob<br>"
}