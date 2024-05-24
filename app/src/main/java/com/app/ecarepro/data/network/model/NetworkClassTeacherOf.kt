package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.MyClasseTeacherOf

data class NetworkClassTeacherOf(
    val editMode: Boolean,
    val errorCode: Int,
    val message: String,
    val myClasses: List<MyClasseTeacherOf>,
    val openPreviousDay: Boolean,
    val status: String
)