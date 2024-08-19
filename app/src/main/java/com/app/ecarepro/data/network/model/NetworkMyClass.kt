package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.MyClasse

data class NetworkMyClass(
    val editMode: Boolean?,
    val errorCode: Int?,
    val message: String?,
    val myClasses: List<MyClasseItem>?,
    val openPreviousDay: Boolean?,
    val status: String?
)

data class MyClasseItem(
    val classID: Int?,
    val className: String?,
    val id: String?,
    var checked: Boolean
)

fun MyClasseItem.asExternalModel()=MyClasse(
    classID,className,id,checked
)