package com.app.ecarepro.model

data class StopLST(
    val stopID: Int,
    val stopName: String,
    val stuLst: List<StuLst>,
    var checked  : Boolean
)