package com.app.ecarepro.core.network.model.user

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response for GET /User/GenerateToken?Device=1
 *
 * Example:
 * {
 *   "errorCode": 0,
 *   "status": "ok",
 *   "message": "Token Key",
 *   "tokenKey": "UyEknqj8..."
 * }
 */
@Serializable
data class NetworkGenerateTokenResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("tokenKey") val tokenKey: String,
) : NetworkResponse
