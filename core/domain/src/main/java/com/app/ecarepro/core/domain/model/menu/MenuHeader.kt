package com.app.ecarepro.core.domain.model.menu

data class MenuHeader(
    val schoolName: String,
    val schoolAddress: String,
    val schoolLogoUrl: String?,
    val currentAccountName: String,
    val currentAccountRole: String,
    val userPhotoUrl: String? = null,
)
