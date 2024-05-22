package com.app.ecarepro.model

data class StuLst(
    var admissionNo: String,
    var className: String,
    var dropAtt: Any,
    var dropStatus: Int,
    var dropTime: Any,
    var isConstant: Boolean,
    var isdropped: Boolean,
    var photo: String,
    var pickupAtt: Any,
    var pickupStatus: Int,
    var pickupTime: Any,
    var rollNo: String,
    var route: String,
    var stID: Int,
    var stName: String,
    var stop: String,
    var stopID: Int,
    var status: Int,
    var isSelected: Boolean = false
)