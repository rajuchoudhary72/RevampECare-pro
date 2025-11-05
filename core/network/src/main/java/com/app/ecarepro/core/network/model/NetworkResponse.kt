package com.app.ecarepro.core.network.model

import com.app.ecarepro.core.domain.exception.ApiException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

interface NetworkResponse {
    val errorCode: Int
    val message: String
    val status: String
}

@Serializable
private data class ErrorResponse(
    val errorCode: Int,
    val message: String,
    val status: String,
)

suspend fun <T, R : NetworkResponse> R.unwrapPayload(
    successCode: IntArray = intArrayOf(0),
    payloadProvider: R.() -> T?,
): T {
    try {
        if (successCode.any { it ==errorCode}) {
            return payloadProvider()
                ?: throw IllegalStateException("Response success but payload is null")
        } else {
            throw ApiException(errorCode, message)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle HttpException specifically
        if (e is HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                try {
                    // Parse the JSON error response
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    // Throw your custom exception with parsed details
                    throw ApiException(errorResponse.errorCode, errorResponse.message)
                } catch (jsonException: Exception) {
                    // Fallback if JSON parsing fails
                    throw ApiException(e.code(), e.message())
                }
            } else {
                // Fallback for empty error body
                throw ApiException(e.code(), e.message())
            }
        }

        throw e
    }
}
