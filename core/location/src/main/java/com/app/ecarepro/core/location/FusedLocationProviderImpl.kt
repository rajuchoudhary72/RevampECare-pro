package com.app.ecarepro.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.app.ecarepro.core.domain.location.LocationProvider
import com.app.ecarepro.core.domain.model.Location
import com.app.ecarepro.core.domain.model.LocationResult
import com.app.ecarepro.core.domain.model.LocationAddress
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FusedLocationProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : LocationProvider {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private fun hasLocationPermission(): Boolean {
        val fine =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION]
    )
    override suspend fun getLocation(): LocationResult {
        if (!hasLocationPermission()) return LocationResult.PermissionDenied
        if (!isLocationEnabled()) return LocationResult.LocationDisabled

        return try {
            val currentLocation =
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            if (currentLocation != null) {
                return LocationResult.Success(
                    Location(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            }

            val lastLocation = fusedClient.lastLocation.await()
            if (lastLocation != null) {
                LocationResult.Success(Location(lastLocation.latitude, lastLocation.longitude))
            } else {
                LocationResult.Error("Unable to get location")
            }
        } catch (e: SecurityException) {
            LocationResult.PermissionDenied
        } catch (e: Exception) {
            LocationResult.Error("Failed to get location: ${e.message}")
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION]
    )
    override fun startLocationUpdates(intervalMillis: Long): Flow<LocationResult> = callbackFlow {
        if (!hasLocationPermission()) {
            trySend(LocationResult.PermissionDenied)
            close()
            return@callbackFlow
        }
        if (!isLocationEnabled()) {
            trySend(LocationResult.LocationDisabled)
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        ).setMinUpdateIntervalMillis(intervalMillis / 2)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                val loc = result.lastLocation ?: return
                trySend(LocationResult.Success(Location(loc.latitude, loc.longitude)))
            }
        }

        try {
            fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            trySend(LocationResult.PermissionDenied)
            close(e) // Close the flow with the exception
        }

        awaitClose {
            fusedClient.removeLocationUpdates(locationCallback)
        }
    }

    override suspend fun getAddressFromLocation(location: Location): Result<LocationAddress> {
        return LocationUtils.getAddressFromLocation(context, location)
    }
}


