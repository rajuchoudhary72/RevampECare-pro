package com.app.ecarepro.model

data class Student(
    val admissionNumber: String,
    val `class`: String,
    val contactMob: String,
    val contactPerson: String,
    val dob: String,
    val fatherName: String,
    var isSelected: Boolean,
    val name: String,
    val recipientName: String,

    val photo: String,
    val rollNumber: String,
    val stID: Int,
    val classID: Int,

) {
    override fun toString(): String {
        return "$name $rollNumber $admissionNumber"
    }

    fun nameAndClass() = "$name (${`class`})"

    fun details() = "<b>Admission No:</b> $admissionNumber<br>" +
            "<b>DOB:</b> $dob<br>" +
            "<b>Father Name:</b> $fatherName<br>" +
            "<b>Contact Number:</b> $contactMob<br>" +
            "<b>Contact Person:</b> $contactPerson"
}