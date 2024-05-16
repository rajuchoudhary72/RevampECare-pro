package com.app.ecarepro.model

data class ClassMateResponse(
    val classmateLST: List<ClassmateLST>,
    val errorCode: Int?,
    val message: String?,
    val status: String?
)