package com.app.ecarepro.core.domain.auth

interface TokenProvider {
    suspend fun getAuthToken(): String
}