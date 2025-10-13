package com.app.ecarepro.core.data.mapper

import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.network.model.onboarding.NetworkOnboardingItem

fun NetworkOnboardingItem.toDomainModel(): OnboardingItem = OnboardingItem(
    headline = this.heading,
    description = this.text
)