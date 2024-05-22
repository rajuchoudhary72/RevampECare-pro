package com.app.ecarepro.ui.appuserreport

data class AppUserReportResponse(
    val deviceUsers: ArrayList<DeviceUser>,
    val errorCode: Int?,
    val message: String?,
    val status: String?
)