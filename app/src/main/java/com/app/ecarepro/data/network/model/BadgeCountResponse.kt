package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response from the Notification Count API
 */

data class BadgeCountResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val notificationCount: Int,
    val messageCount: Int
)