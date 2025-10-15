package com.app.ecarepro.core.domain.location

import com.app.ecarepro.core.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    suspend fun getLocation(): LocationResult
    fun startLocationUpdates(intervalMillis: Long = 10_000L): Flow<LocationResult>
}