package com.app.ecarepro.core.data.auth

import com.app.ecarepro.core.domain.auth.TokenProvider
import com.app.ecarepro.core.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Provider

class TokenProviderImpl @Inject constructor(
    private val userRepositoryProvider: Provider<UserRepository>
): TokenProvider {
    companion object {
        private const val DEFAULT_AUTH_TOKEN = "Kq4IYAuSXLh4EsnexoTSfA=="
    }
    override suspend fun getAuthToken(): String {
        return userRepositoryProvider.get().getActiveUserAuthToken()?:DEFAULT_AUTH_TOKEN
    }
}