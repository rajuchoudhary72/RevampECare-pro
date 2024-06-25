package com.app.ecarepro.model

data class TimeTable(
    val className: String,
    val period: Int,
    val subject: String,
    val time: String,
    val teachBy: String,
)