package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ClassesForClsTeach
import com.app.ecarepro.model.ClassesForSubTeach

data class NetworkMarkAttendance(
    val attDate: Any,
    val backDate: Int,
    val classAttendance: Boolean,
    val classesForClsTeach: List<ClassesForClsTeach>,
    val classesForSubTeach: List<ClassesForSubTeach>,
    val editMode: Boolean,
    val errorCode: Int,
    val message: String,
    val openPreviousDay: Boolean,
    val status: String,
    val subjectAttendance: Boolean
)