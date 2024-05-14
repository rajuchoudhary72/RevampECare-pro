package com.app.ecarepro.ui.studentId

data class StudentCardResponse(
    val actionOn: Any?,
    val browseImgEnable: Boolean,
    val canChangeApprovedImg: Boolean,
    val errorCode: Int?,
    val escortImgURL: String?,
    val escortReq: Any?,
    val fatherImgURL: String?,
    val fatherReq: Any?,
    val message: String?,
    val modifiedOn: Any?,
    val motherImgURL: String?,
    val motherReq: Any?,
    val requstedOn: Any?,
    val status: String?,
    val studentDTL: StudentDTL?
)