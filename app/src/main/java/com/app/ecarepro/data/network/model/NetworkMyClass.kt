package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.MyClasse

data class NetworkMyClass(
    val EditMode: Boolean,
    val ErrorCode: Int,
    val Message: String,
    val MyClasses: List<MyClasseItem>,
    val OpenPreviousDay: Boolean,
    val Status: String
)

data class MyClasseItem(
    val ClassID: Int,
    val ClassName: String,
    val ID: Any,
    val isSelect: Boolean
)

fun MyClasseItem.asExternalModel()=MyClasse(
    ClassID,ClassName,ID,isSelect
)