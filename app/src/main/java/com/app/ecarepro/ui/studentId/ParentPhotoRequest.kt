package com.app.ecarepro.ui.studentId

data class ParentPhotoRequest(
    val stID: Int,
    var escortPhoto: EscortPhoto?=null,
    var fatherPhoto: FatherPhoto?=null,
    var motherPhoto: MotherPhoto?=null
)