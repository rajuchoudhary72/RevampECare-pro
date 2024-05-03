package com.app.ecarepro.model

data class TimeTable(
    val className: String,
    val period: Int,
    val subject: String,
    val bookCover: String,
    val teachBy: String,
    val time: String
)