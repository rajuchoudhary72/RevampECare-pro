 package com.app.ecarepro.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingResponseModel(
     override val errorCode: Int,
     override val message: String,
     override val status: String,
     val slides: List<OnboardingSlide>,
 ):NetworkResponse

@Serializable
data class OnboardingSlide(
    val heading: String,
    val imageURL: String,
    val text: String
)
