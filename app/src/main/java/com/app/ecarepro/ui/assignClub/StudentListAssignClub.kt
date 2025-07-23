package com.app.ecarepro.ui.assignClub

import com.app.ecarepro.ui.assign_home.House
import com.app.ecarepro.ui.assign_home.Student

data class StudentListAssignClub(
    val canAutoAssignRollNo: Boolean?,
    val errorCode: Int?,
    val message: String?,
    val status: String?,
    val students: List<Student>?,
    val clubs: List<Clubs>?
)