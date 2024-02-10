package com.app.ecarepro.model

data class MonthlyPaySlip(
    val filePath: String,
    val month: String,
    val monthID: Int,
    val protectedFilePath: String
)