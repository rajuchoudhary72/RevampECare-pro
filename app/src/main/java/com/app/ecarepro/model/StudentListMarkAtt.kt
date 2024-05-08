package com.app.ecarepro.model

data class StudentListMarkAtt(
    var contactMob: String,
    var isConstant: Int,
    var isLate: Int,
    var otherDTL: List<OtherDTL>,
    var photo: String,
    var stID: Int,
    var stName: String,
    var status: Int
)