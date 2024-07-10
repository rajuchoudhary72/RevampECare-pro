package com.app.ecarepro.model

data class ReportCard(
    val backFileName: String,
    val backFileSize: String,
    val examName: String,
    val fileName: String,
    val fileSize: String,
    val frontFileName: String,
    val frontFileSize: String,
    val updatedOn: String,
    val viewMode: Int
)