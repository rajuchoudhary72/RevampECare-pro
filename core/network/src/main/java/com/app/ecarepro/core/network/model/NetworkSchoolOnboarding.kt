package com.app.ecarepro.core.network.model

data class NetworkSchoolOnboarding(
    val errorCode: Int,
    val message: String,
    val slides: List<NetworkOnboardingItem>,
    val status: String
)


data class NetworkOnboardingItem(
    val heading: String,
    val imageURL: String,
    val text: String
)
