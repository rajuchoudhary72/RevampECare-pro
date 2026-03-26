package com.app.ecarepro.designsystem.core.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * A reusable Composable that handles the logic for requesting runtime permissions.
 *
 * @param permissions A list of permissions to request (e.g., location permissions).
 * @param rationale Composable content to show the user when a rationale is needed.
 * This should explain why the permission is required and guide them to grant it.
 * @param onPermissionsGranted Composable content to display when all permissions have been granted.
 * @param onPermissionsDenied Composable content to display when permissions have been denied.
 * This can guide the user to the app settings.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequester(
    permissions: List<String>,
    rationale: @Composable (requestPermissions: () -> Unit) -> Unit,
    onPermissionsGranted: @Composable () -> Unit,
    onPermissionsDenied: @Composable () -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(permissions)

    // Request permissions when the composable enters the composition
    LaunchedEffect(permissionState) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    when {
        // If all permissions are granted, show the content that depends on them
        permissionState.allPermissionsGranted -> {
            onPermissionsGranted()
        }
        // If a rationale should be shown, display the rationale UI
        permissionState.shouldShowRationale -> {
            rationale {
                permissionState.launchMultiplePermissionRequest()
            }
        }
        // If permissions are denied and no rationale is needed, show the denied UI
        else -> {
            onPermissionsDenied()
        }
    }
}