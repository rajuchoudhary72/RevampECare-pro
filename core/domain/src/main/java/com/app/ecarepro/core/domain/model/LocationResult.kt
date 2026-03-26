package com.app.ecarepro.core.domain.model

sealed interface LocationResult {
    data class Success(val location: Location) : LocationResult
    data class Error(val message: String) : LocationResult
    data object PermissionDenied : LocationResult
    data object LocationDisabled : LocationResult
}
