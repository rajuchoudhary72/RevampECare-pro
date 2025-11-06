package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.let

/**
 * A reusable Scaffold component for the EcarePro application.
 *
 * This scaffold provides a consistent structure for screens, including a top app bar,
 * a floating action button, a custom snackbar host, and a loading indicator.
 * The loader is displayed within the content area.
 *
 * @param modifier The modifier to be applied to the scaffold.
 * @param isLoading Whether to show a loading overlay within the content area.
 * @param snackbarHostState The state of the snackbar host, used to show snackbars.
 * @param snackbarMessage The message to be displayed in the snackbar.
 * @param onSnackbarDismissed A callback function invoked when the snackbar is dismissed.
 * @param topBar A composable function for the top app bar.
 * @param bottomBar A composable function for the bottom app bar.
 * @param floatingActionButton A composable function for the floating action button.
 * @param containerColor The color of the scaffold's container.
 * @param content The main content of the screen.
 */
@Composable
fun EcareProScaffold(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = Color.Transparent,
    content: @Composable (PaddingValues) -> Unit,
) {
    LaunchedEffect(snackbarHostState.currentSnackbarData) {
        if (snackbarHostState.currentSnackbarData == null) {
            onSnackbarDismissed()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    snackbarMessage?.let {
                        EcareProSnackbar(snackbarMessage = it)
                    }
                }
            },
            containerColor = containerColor,
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            content(paddingValues)
        }

        if (isLoading) {
            Loader()
        }
    }
}