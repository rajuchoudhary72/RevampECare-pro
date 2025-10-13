package com.app.ecarepro.core.network.model.onboarding

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkSchoolOnboarding(
    override val errorCode: Int,
    override val message: String,
    override val status: String,
    val slides: List<NetworkOnboardingItem>,
) : NetworkResponse

@Serializable
@InternalSerializationApi
data class NetworkOnboardingItem(
    val heading: String,
    val imageURL: String,
    val text: String,
)
