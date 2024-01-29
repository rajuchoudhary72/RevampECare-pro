package com.app.ecarepro.model

data class Activity(
    val duration: Int,
    val fromDate: String,
    val isWorking: Boolean,
    val tillDate: String,
    val title: String
)