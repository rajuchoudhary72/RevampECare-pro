package com.app.ecarepro.designsystem.core.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import kotlin.math.roundToInt

@Composable
fun EcareProSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 24.dp,
    thumbSize: Dp = 16.dp,
    padding: Dp = 4.dp,
    checkedTrackColor: Color = Color(0xFFC8E6C9), // Light Green (Adjust to match your exact design)
    uncheckedTrackColor: Color = Color(0xFFE0E0E0), // Light Gray
    checkedThumbColor: Color = MaterialTheme.appColors.primary, // Darker Green from your theme
    uncheckedThumbColor: Color = Color(0xFF757575), // Darker Gray
) {
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current

    // Calculate the offset for the thumb animation
    val thumbRadius = (thumbSize / 2)
    val trackWidthPx = with(density) { width.toPx() }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val paddingPx = with(density) { padding.toPx() }

    val targetOffset = if (checked) {
        trackWidthPx - thumbSizePx - paddingPx
    } else {
        paddingPx
    }

    val animatedOffset by animateFloatAsState(
        targetValue = targetOffset,
        label = "thumbOffset"
    )

    val trackColor = if (checked) checkedTrackColor else uncheckedTrackColor
    val thumbColor = if (checked) checkedThumbColor else uncheckedThumbColor

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                color = trackColor,
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(x = animatedOffset.roundToInt(), y = 0) }
                .align(Alignment.CenterStart)
                .size(thumbSize)
                .background(color = thumbColor, shape = CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EcareProSwitchPreview() {
    EcareProTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val icChecked = remember { mutableStateOf(false) }


            // Unchecked State
            EcareProSwitch(
                checked = icChecked.value,
                onCheckedChange = {
                    isChecked -> icChecked.value = isChecked
                }
            )

            // Checked State
            EcareProSwitch(
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}