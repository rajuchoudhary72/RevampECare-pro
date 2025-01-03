package com.app.ecarepro.model

data class WebSession(
    val browser: String,
    val deviceType: String,
    val ipAddress: String,
    val isMobile: Boolean,
    val isThisDevice: Boolean,
    val lastActivityTime: String,
    val locationCity: String,
    val loginTime: String,
    val operatingSystem: String,
    val sessionID: String
)