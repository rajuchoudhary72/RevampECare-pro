package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Custom pull-to-refresh implementation with gesture detection.
 * Detects when user pulls down from the top and triggers refresh.
 */
@Composable
fun EcareProPullToRefreshFallback(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val threshold = with(LocalDensity.current) { 100.dp.toPx() }

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var isAtTop by remember { mutableStateOf(true) }

    // Reset when refresh completes
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullOffset = 0f
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Detect if we're at the top
                if (source == NestedScrollSource.UserInput) {
                    isAtTop = available.y < 0 && pullOffset == 0f
                }

                // If pulling up while offset is active, consume it
                if (available.y < 0 && pullOffset > 0) {
                    val consumed = pullOffset.coerceAtMost(available.y.absoluteValue)
                    pullOffset = (pullOffset - consumed).coerceAtLeast(0f)
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Only handle pull down when at top
                if (source == NestedScrollSource.UserInput && available.y > 0 && !isRefreshing) {
                    val dragAmount = available.y * 0.5f // Add resistance
                    pullOffset = (pullOffset + dragAmount).coerceAtMost(threshold * 2)
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: androidx.compose.ui.unit.Velocity, available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                // Trigger refresh if pulled past threshold
                if (pullOffset >= threshold) {
                    onRefresh()
                } else {
                    pullOffset = 0f
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // Main content with offset during pull
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, pullOffset.roundToInt()) }
        ) {
            content()
        }

        // Loading indicator
        if (isRefreshing || pullOffset > 0) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
