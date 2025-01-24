package com.app.ecarepro.model

data class NetworkUserSessionsResponse(
    val appSessionCount: Int,
    val appSessions: List<AppSession>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val webSessionCount: Int,
    val webSessions: List<WebSession>
)