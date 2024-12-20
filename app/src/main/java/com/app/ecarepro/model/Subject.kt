package com.app.ecarepro.model

import android.os.Parcelable
import com.app.ecarepro.model.Mark
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subject(
    val marks: List<Mark>?,
    val subID: Int?,
    val subjectName: String?
):Parcelable