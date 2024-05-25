package com.app.ecarepro.model

data class ReportCard(
    val backFileName: Any,
    val backFileSize: Any,
    val examName: String,
    val fileName: String,
    val fileSize: String,
    val frontFileName: Any,
    val frontFileSize: Any,
    val updatedOn: String,
    val viewMode: Int
)