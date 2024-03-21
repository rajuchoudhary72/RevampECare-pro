package com.app.ecarepro.model

data class RequestClassPromotion(
    var nYrID: String="",
    var studentPromotedClasses: MutableList<StudentPromotedClass>?=null,
    var yrID: String=""
)

data class StudentPromotedClass(
    var newClassID: String,
    var newSectionID: String,
    var stID: String
)