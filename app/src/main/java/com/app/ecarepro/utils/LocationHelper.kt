import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.ui.home.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

class LocationHelper(private val fragment: Fragment) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mViewModel: HomeViewModel // Replace with your actual ViewModel class
    private val context: Context get() = fragment.requireContext()
    private val activity: AppCompatActivity get() = fragment.requireActivity() as AppCompatActivity

    // Permission launcher for location access
    private val locationPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                // Fine location permission granted
                getCurrentLocationAndCity()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Coarse location permission granted
                getCurrentLocationAndCity()
            }
            else -> {
                // No location permissions granted
                mViewModel.setCityName("India")
//                Toast.makeText(
//                    context,
//                    "Location permission is required to get your city",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        }
    }

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Main function to request location access and get city name
     * Call this function from your Activity/Fragment
     */
    fun requestLocationAndGetCity(viewModel: HomeViewModel) {
        mViewModel = viewModel

        when {
            hasLocationPermission() -> {
                // Permission already granted
                getCurrentLocationAndCity()
            }
            else -> {
                // Request location permissions
                requestLocationPermissions()
            }
        }
    }

    /**
     * Check if location permissions are granted
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request location permissions from user
     */
    private fun requestLocationPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Always request fine location first, then coarse as fallback
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        locationPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    /**
     * Get current location and extract city name
     */
    private fun getCurrentLocationAndCity() {
        if (!hasLocationPermission()) {
           // Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
            Log.v("Location", "Location permission not granted")
            return
        }

        // Check if location services are enabled
        if (!isLocationEnabled()) {
            Toast.makeText(
                context,
                "Please enable location services",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        fragment.lifecycleScope.launch {
            try {
                val location = getCurrentLocation()
                if (location != null) {
                    val cityName = getCityNameFromLocation(location)
                    if (cityName.isNotEmpty()) {
                        // Pass city name to ViewModel
                        mViewModel.setCityName(cityName)
//                        Toast.makeText(
//                            context,
//                            "Location found: $cityName",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        Log.v("Location", "Location found: $cityName")
                    } else {
//                        Toast.makeText(
//                            context,
//                            "Could not determine city name",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        Log.v("Location", "Could not determine city name")

                    }
                } else {
//                    Toast.makeText(
//                        context,
//                        "Could not get current location",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    Log.v("Location", "Could not get current location")
                }
            } catch (e: Exception) {
//                Toast.makeText(
//                    context,
//                    "Error getting location: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
                Log.v("Location", "Error getting location: ${e.message}")
            }
        }
    }

    /**
     * Get current location using FusedLocationProviderClient
     */
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            continuation.resume(location)
        }.addOnFailureListener { exception ->
            continuation.resume(null)
        }

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }

    /**
     * Get city name from location coordinates using Geocoder
     */
    private suspend fun getCityNameFromLocation(location: Location): String =
        suspendCancellableCoroutine { continuation ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // For Android 13+ (API 33+)
                    val geocoder = Geocoder(context, Locale.getDefault())
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) { addresses ->
                        val cityName = if (addresses.isNotEmpty()) {
                            addresses[0].locality ?: addresses[0].subAdminArea ?:
                            addresses[0].adminArea ?: "Unknown"
                        } else {
                            ""
                        }
                        continuation.resume(cityName)
                    }
                } else {
                    // For older Android versions
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses: List<Address> = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) ?: emptyList()

                    val cityName = if (addresses.isNotEmpty()) {
                        addresses[0].locality ?: addresses[0].subAdminArea ?:
                        addresses[0].adminArea ?: "Unknown"
                    } else {
                        ""
                    }
                    continuation.resume(cityName)
                }
            } catch (e: Exception) {
                continuation.resume("")
            }
        }

    /**
     * Check if location services are enabled
     */
    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}