package com.app.ecarepro.core.network.model.ebook

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkEBookResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("total") val total: Int? = null,
    @SerialName("megaBookLink") val megaBookLink: String? = null,
    @SerialName("books") val books: List<NetworkEBookItem>? = null,
) : NetworkResponse
