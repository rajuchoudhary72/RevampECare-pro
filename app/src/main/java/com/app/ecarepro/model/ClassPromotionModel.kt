package com.app.ecarepro.model


data class ClassPromotionModel(
    val editMode: Boolean?,
    val errorCode: Int?,
    val message: String?,
    val myClasses: List<MyClasseX>?,
    val openPreviousDay: Boolean?,
    val status: String?
)

data class MyClasseX(
    val classID: Int?,
    val className: String?,
    val id: String?,
    val isSelect: Boolean?
) {
    override fun toString(): String {
        return className ?: ""
    }
}

