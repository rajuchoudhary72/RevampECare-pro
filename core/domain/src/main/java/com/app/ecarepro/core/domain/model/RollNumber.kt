package com.app.ecarepro.core.domain.model

data class RollNumberStudent(
    val stID: Int,
    val name: String,
    val rollNumber: String,
    val admissionNumber: String,
    val photo: String,
    val houseID: Int,
    val isSelected: Boolean,
)
