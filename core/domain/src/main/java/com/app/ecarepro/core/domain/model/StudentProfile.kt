package com.app.ecarepro.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentProfile(
    val id: String,
    val stID: Int? = null,
    val name: String? = null,
    val gender: String? = null,
    val classSTD: String,
    val classID: Int? = null,
    val rollNumber: String? = null,
    val section: String? = null,
    val admissionNumber: String? = null,
    val photo: String? = null,
    val dob: String? = null,
    val fatherName: String? = null,
    val motherName: String? = null,
    val contactPerson: String? = null,
    val mobileNumber: String? = null,
    val emailID: String? = null,
    val address: String? = null,
    val updatedOn: String? = null,
)
