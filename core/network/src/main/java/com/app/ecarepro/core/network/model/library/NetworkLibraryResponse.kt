package com.app.ecarepro.core.network.model.library

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLibraryResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("megaBookLink") val megaBookLink: String? = null,
    @SerialName("latestBook") val latestBook: List<NetworkLibraryBookDetail>? = null,
    @SerialName("myAccount") val myAccount: List<NetworkLibraryBookDetail>? = null,
) : NetworkResponse
