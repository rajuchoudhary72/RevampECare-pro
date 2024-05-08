package com.app.ecarepro.model

data class ClassSummary(
    val absent: Int,
    val classID: Int,
    val className: String,
    val id: String,
    val isMarked: Boolean,
    val late: Int,
    val leave: Int,
    val present: Int
)