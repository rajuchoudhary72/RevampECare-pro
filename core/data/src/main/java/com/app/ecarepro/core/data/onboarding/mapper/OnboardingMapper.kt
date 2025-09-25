package com.app.ecarepro.core.data.onboarding.mapper

import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.network.model.NetworkOnboardingItem

fun NetworkOnboardingItem.toDomainModel(): OnboardingItem = OnboardingItem(
    headline = this.heading,
    description = this.text,
    image = this.imageURL
)