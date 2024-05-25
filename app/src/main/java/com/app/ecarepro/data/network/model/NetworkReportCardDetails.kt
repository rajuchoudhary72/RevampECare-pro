package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ReportClasse

data class NetworkReportCardDetails(
    val classes: List<ReportClasse>,
    val errorCode: Int,
    val message: String,
    val status: String
)