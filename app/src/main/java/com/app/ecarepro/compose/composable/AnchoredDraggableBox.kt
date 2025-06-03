package com.app.ecarepro.compose.composable

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.fillMaxHeight


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnchoredDraggableBox(
    modifier: Modifier,
    firstContent: @Composable (modifier: Modifier) -> Unit,
    secondContent: @Composable (modifier: Modifier) -> Unit,
    offsetSize: Dp = 180.dp,
    dragAnchors: DragAnchors = DragAnchors.Start,
) {
    val density = LocalDensity.current
    val positionalThresholds: (totalDistance: Float) -> Float =
        { totalDistance -> totalDistance * 0.5f }
    val velocityThreshold: () -> Float = { with(density) { 100.dp.toPx() } }

    val decay = rememberSplineBasedDecay<Float>()

    val state = remember {
        AnchoredDraggableState(
            initialValue = dragAnchors,
            positionalThreshold = positionalThresholds,
            velocityThreshold = velocityThreshold,
            snapAnimationSpec = tween(),
            decayAnimationSpec = decay,
            confirmValueChange = { true }

        ).apply {
            val newAnchors = with(density) {
                DraggableAnchors {
                    DragAnchors.Start at 0.dp.toPx()
                    DragAnchors.End at -offsetSize.toPx()
                }
            }
            updateAnchors(newAnchors)
        }
    }

    LaunchedEffect(key1 = dragAnchors) {
        if (dragAnchors == DragAnchors.End) {
            state.animateTo(DragAnchors.End)
        } else {
            state.animateTo(DragAnchors.Start)
        }
    }

    Box(
        modifier = modifier
    ) {
        firstContent(Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .offset {
                IntOffset(
                    state.requireOffset().roundToInt(), 0
                )
            }

            .anchoredDraggable(state, Orientation.Horizontal))
        secondContent(Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .offset {
                IntOffset(
                    (state.requireOffset() + offsetSize.toPx()).roundToInt(), 0
                )
            }
            .anchoredDraggable(state, Orientation.Horizontal))
    }

}

enum class DragAnchors {
    Start, End,
}
