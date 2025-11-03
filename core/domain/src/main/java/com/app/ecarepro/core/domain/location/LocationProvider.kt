package com.app.ecarepro.core.domain.location

import android.location.Address
import com.app.ecarepro.core.domain.model.Location
import com.app.ecarepro.core.domain.model.LocationAddress
import com.app.ecarepro.core.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    suspend fun getLocation(): LocationResult
    fun startLocationUpdates(intervalMillis: Long = 10_000L): Flow<LocationResult>

    suspend fun getAddressFromLocation(location: Location): Result<LocationAddress>
}