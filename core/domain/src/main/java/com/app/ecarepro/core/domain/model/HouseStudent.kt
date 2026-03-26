package com.app.ecarepro.core.domain.model

data class HouseItem(
    val houseID: Int,
    val houseName: String,
)

data class HouseStudent(
    val stID: Int,
    val name: String,
    val photo: String,
    val admissionNumber: String,
    val rollNumber: String,
    val houseID: Int,
    val houseName: String,
)

data class HouseStudentData(
    val houses: List<HouseItem>,
    val students: List<HouseStudent>,
)
