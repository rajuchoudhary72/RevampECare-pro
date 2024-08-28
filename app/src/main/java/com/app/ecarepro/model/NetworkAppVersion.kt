package com.app.ecarepro.model

data class NetworkAppVersion(
    val android: Android,
    val errorCode: Int,
    val iOS: IOS,
    val message: String,
    val schUpdatedOn: String,
    val status: String
)