package com.app.ecarepro.core.domain.model

data class NotificationItem(
    val id: String,
    val moduleID: Int,
    val chMenuID: Int,
    val title: String,
    val body: String,
    val hasSeen: Boolean,
    val sentOn: String,
    val icon: String?,
    val link: String?,
    val refID: String?,
)
