package com.app.ecarepro.core.network.model

import com.app.ecarepro.core.domain.exception.ApiException

interface NetworkResponse {
    val errorCode: Int
    val message: String
    val status: String
}
suspend fun <T, R : NetworkResponse> R.unwrapPayload(
    payloadProvider: R.() -> T?
): T {
    if (errorCode == 0) {
        return payloadProvider() ?: throw IllegalStateException("Response success but payload is null")
    } else {
        throw ApiException(errorCode, message)
    }
}
