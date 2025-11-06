package com.app.ecarepro.core.network.model.user

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


@InternalSerializationApi
@Serializable
data class NetworkGetUsernameByUIDResponse(
    override val errorCode: Int,
    override val message: String,
    override val status: String,
) : NetworkResponse

