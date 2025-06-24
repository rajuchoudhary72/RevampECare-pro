package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AuditorLst
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.model.RequiredField

data class NetworkCreateLesson(
    val auditorLst: List<AuditorLst>,
    val errorCode: Int,
    val message: String,
    val requiredField: RequiredField,
    val status: String,
    val subjects: List<MySubject>,

)