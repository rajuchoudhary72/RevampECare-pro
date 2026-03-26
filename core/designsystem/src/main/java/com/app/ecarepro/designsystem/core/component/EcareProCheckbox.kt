package com.app.ecarepro.designsystem.core.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun EcareProCheckboxWithText(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!isChecked) }
            )
    ) {
        EcareProCheckbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

@Composable
fun EcareProCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    checkedColor: Color = MaterialTheme.appColors.primary,
    uncheckedColor: Color = MaterialTheme.appColors.textSecondary,
    checkmarkColor: Color = MaterialTheme.appColors.background,
) {
    // Animation states
    val animatedColor by animateColorAsState(
        if (checked) checkedColor else uncheckedColor,
        label = "color"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(40.dp) // Standard touch target size
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 20.dp),
                enabled = onCheckedChange != null,
                onClick = { onCheckedChange?.invoke(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.requiredSize(20.dp)) {
            val strokeWidth = 1.dp.toPx()
            val checkboxSize = size.width
            val cornerRadius = 4.dp.toPx()

            if (checked) {
                drawRoundRect(
                    color = animatedColor,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            } else {
                drawRoundRect(
                    color = animatedColor,
                    style = Stroke(width = strokeWidth),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }

            // 2. Draw the Checkmark
            if (checked) {
                val path = Path()
                path.moveTo(checkboxSize * 0.2f, checkboxSize * 0.5f)
                path.lineTo(checkboxSize * 0.4f, checkboxSize * 0.7f)
                path.lineTo(checkboxSize * 0.8f, checkboxSize * 0.3f)

                drawPath(
                    path = path,
                    color = checkmarkColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EcareProCheckboxWithTextPreview() {
    val isChecked = remember { mutableStateOf(false) }
    EcareProTheme {
        EcareProCheckboxWithText(
            text = "Notify Student",
            isChecked = isChecked.value,
            onCheckedChange = {
                isChecked.value = isChecked.value.not()
            }
        )
    }
}