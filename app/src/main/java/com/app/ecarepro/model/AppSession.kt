package com.app.ecarepro.model

data class AppSession(
    val deviceID: String,
    val deviceType: String,
    val isThisDevice: Boolean,
    val lastActivityTime: String,
    val locationCity: String,
    val loginTime: String,
    val model: String,
    val operatingSystem: String,
    val sessionID: String
)