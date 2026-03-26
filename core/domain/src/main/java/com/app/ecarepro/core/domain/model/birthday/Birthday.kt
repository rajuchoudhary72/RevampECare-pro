package com.app.ecarepro.core.domain.model.birthday

data class Birthday(
    val id: Int,
    val name: String,
    val fatherName: String?,
    val motherName: String?,
    val birthdayOf: String?,
    val className: String?,
    val birthdayOn: String,
    val photo: String?,
    val userType: Int,
    val designation: String?,
    val mobile: String?,
)
