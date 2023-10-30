package com.app.ecarepro.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.app.ecarepro.R

data class OnboardingItem(
    @DrawableRes val imageRes: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
) {
    companion object {
        val DUMMY_ITEM = mutableListOf(
            OnboardingItem(
                R.drawable.img_onboarding_1,
                R.string.onboarding_title_1,
                R.string.onboarding_description_1,
            ),
            OnboardingItem(
                R.drawable.img_onboarding_2,
                R.string.onboarding_title_2,
                R.string.onboarding_description_2,
            ),
        )
    }
}
