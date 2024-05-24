package com.app.ecarepro.model


data class PromotionModel(
    val errorCode: Int?,
    val message: String?,
    val nYrID: Int?,
    val nextSession: String?,
    val session: String?,
    val status: String?,
    val students: MutableList<StudentPro?>,
    val yrID: Int?
)

data class StudentPro(
    val admissionNumber: String?,
    val `class`: String?,
    val fatherName: String?,
    val isSelected: Boolean?,
    val name: String?,
    val nextSessionClasses: MutableList<NextSessionClasse?>?,
    val photo: String?,
    val rollNumber: String?,
    val stID: Int?,
    var selected: Section?
)

data class NextSessionClasse(
    var classID: Int?,
    val className: String?,
    var isSelected: Boolean = false,
    val sections: MutableList<Section>?
)

data class Section(
    var classID: Int?,
    val secID: Int?,
    val secName: String?
)

