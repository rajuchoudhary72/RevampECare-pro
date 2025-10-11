 package com.app.ecarepro.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingResponseModel(
     override val errorCode: Int,
     override val message: String,
     override val status: String,
     val slides: List<NetworkOnboardingItem>,
 ):NetworkResponse

@Serializable
data class NetworkOnboardingItem(
    val heading: String,
    val imageURL: String,
    val text: String
)
