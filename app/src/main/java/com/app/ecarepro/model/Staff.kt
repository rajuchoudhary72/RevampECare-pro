package com.app.ecarepro.model

data class
Staff(
    val designation: String,
    val email: String,
    val gender: String,
    val id: String,
    val isSelected: Boolean,
    val maritialStatus: String,
    val mobile: String,
    val name: String,
    val pending: Int,
    val photo: String,
    val qualification: String,
    val sid: Int
){
    fun details() = "Designation: $designation\n" +
            "Mobile: $mobile\n" +
            "Email: $email\n" +
            "Marital Status: $maritialStatus\n" +
            "Gender: $gender"
}