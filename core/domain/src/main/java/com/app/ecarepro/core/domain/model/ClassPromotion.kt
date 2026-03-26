package com.app.ecarepro.core.domain.model

data class ClassPromotionData(
    val yrID: Int,
    val session: String,
    val nYrID: Int,
    val nextSession: String,
    val students: List<PromotionStudent>,
)

data class PromotionStudent(
    val stID: Int,
    val name: String,
    val studentClass: String,
    val rollNumber: String,
    val admissionNumber: String,
    val fatherName: String,
    val photo: String,
    val isSelected: Boolean,
    val nextSessionClasses: List<NextSessionClass>,
)

data class NextSessionClass(
    val classID: Int,
    val className: String,
    val sections: List<PromotionSection>,
    val isSelected: Boolean,
)

data class PromotionSection(
    val classID: Int,
    val secID: Int,
    val secName: String,
    val isSelected: Boolean,
)

data class StudentPromotion(
    val stID: Int,
    val newClassID: Int,
    val newSectionID: Int,
)
