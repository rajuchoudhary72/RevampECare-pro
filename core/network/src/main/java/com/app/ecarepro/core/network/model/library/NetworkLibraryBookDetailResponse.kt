package com.app.ecarepro.core.network.model.library

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLibraryBookDetailResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("bookDTL") val bookDTL: List<NetworkLibraryBookDetail>? = null,
) : NetworkResponse
