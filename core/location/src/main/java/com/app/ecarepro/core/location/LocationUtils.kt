package com.app.ecarepro.core.location

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.app.ecarepro.core.domain.model.Location
import com.app.ecarepro.core.domain.model.LocationAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


/**
 * A utility object for handling location-related actions, such as checking GPS status
 * and providing intents to open system settings.
 */
object LocationUtils {

    /**
     * Checks if the GPS (or any other location provider) is enabled on the device.
     *
     * @param context The context to access system services.
     * @return `true` if GPS is enabled, `false` otherwise.
     */
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Opens the device's main location settings screen.
     *
     * From here, the user can enable or disable GPS for the entire device.
     *
     * @param context The context to start the activity.
     */
    fun openGpsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    /**
     * Opens the specific settings screen for this application.
     *
     * This is the recommended way to send a user to grant a permission that they
     * have permanently denied.
     *
     * @param context The context to get the package name and start the activity.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Performs reverse geocoding to convert a Location object into a structured address.
     *
     * This is a suspend function that performs network I/O on the Dispatchers.IO context.
     *
     * @param context The context used to create the Geocoder instance.
     * @param location The Location object containing latitude and longitude.
     * @return A [Result] containing the [LocationAddress] on success, or an exception on failure.
     */
    suspend fun getAddressFromLocation(
        context: Context,
        location: Location,
    ): Result<LocationAddress> = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context)
        try {
            val addresses: List<Address>? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // For Android 13 (Tiramisu) and above, the synchronous API is deprecated.
                    // This implementation still uses it for simplicity, wrapped in a try-catch.
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                }

            val address = addresses?.firstOrNull()
            if (address != null) {
                Result.success(
                    LocationAddress(
                        city = address.locality,
                        state = address.adminArea,
                        country = address.countryName,
                        postalCode = address.postalCode,
                        fullAddress = address.getAddressLine(0)
                    )
                )
            } else {
                Result.failure(IOException("Could not find any address for the given location."))
            }
        } catch (e: IOException) {
            // This can happen if the network is unavailable or the geocoder service is not working.
            Result.failure(e)
        } catch (e: IllegalArgumentException) {
            // This can happen if the provided latitude or longitude is invalid.
            Result.failure(e)
        }
    }


}