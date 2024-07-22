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

    fun details() = "Admission No: $admissionNumber\n" +
            "DOB: $dob\n" +
            "Father Name: $fatherName\n" +
            "Contact Number: $contactMob\n" +
            "Contact Person: $contactPerson"
}