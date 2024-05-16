package com.app.ecarepro.ui.assign_home

data class StudentList(
    val canAutoAssignRollNo: Boolean?,
    val errorCode: Int?,
    val houses: List<House>?,
    val message: String?,
    val status: String?,
    val students: List<Student>?
)