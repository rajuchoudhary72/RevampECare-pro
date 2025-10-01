package com.app.ecarepro.core.network.model

interface NetworkResponse {
    val errorCode: Int
    val message: String
    val status: String
}


class ApiException(val code: Int, override val message: String) : Exception(message)

suspend fun <T, R : NetworkResponse> R.unwrapPayload(
    payloadProvider: R.() -> T?
): T {
    if (errorCode == 0) {
        return payloadProvider() ?: throw IllegalStateException("Response success but payload is null")
    } else {
        throw ApiException(errorCode, message)
    }
}
