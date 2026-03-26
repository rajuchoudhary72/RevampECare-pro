package com.app.ecarepro.core.domain.model

import androidx.annotation.DrawableRes

data class HomeSelection(
    val type: HomeScreenType,
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
)