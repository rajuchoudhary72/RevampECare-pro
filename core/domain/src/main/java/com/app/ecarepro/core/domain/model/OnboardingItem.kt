package com.app.ecarepro.core.domain.model

import androidx.annotation.DrawableRes

data class OnboardingItem(
    val headline: String,
    val description: String,
    @DrawableRes val imageRes: Int = 0,
)
