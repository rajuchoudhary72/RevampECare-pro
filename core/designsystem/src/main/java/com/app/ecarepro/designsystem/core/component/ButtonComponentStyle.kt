package com.app.ecarepro.designsystem.core.component
// ButtonComponent.kt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.LocalIndication
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * Style data class (similar to your SwiftUI ButtonComponentStyle)
 */
data class ButtonComponentStyle(
    val height: Dp = 48.dp,
    val fontFamily: FontFamily = FontFamily.Default,
    val fontWeight: FontWeight = FontWeight.Medium,
    val fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    val foregroundColor: Color = Color.White,
    val imageWidth: Dp = 22.dp,
    val imageHeight: Dp = 22.dp,
    val imagePadding: Dp = 10.dp,
    val backgroundColor: Color = Color.Blue,
    val linearGradient: Brush? = null,
    val cornerRadius: Dp = 12.dp,
    val borderWidth: Dp = 0.dp,
    val borderColor: Color = Color.Blue,
    val useOnTapGesture: Boolean = false
) {
    companion object {
        fun primary(
            height: Dp = 48.dp,
            fontFamily: FontFamily = FontFamily.Default,
            fontWeight: FontWeight = FontWeight.Medium,
            fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
            foregroundColor: Color = Color.White,
            backgroundColor: Color = Color.Blue,
            cornerRadius: Dp = 12.dp,
            useOnTapGesture: Boolean = false
        ) = ButtonComponentStyle(
            height = height,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            cornerRadius = cornerRadius,
            useOnTapGesture = useOnTapGesture
        )

        fun secondary(
            height: Dp = 48.dp,
            fontFamily: FontFamily = FontFamily.Default,
            fontWeight: FontWeight = FontWeight.Medium,
            fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
            foregroundColor: Color = Color.Blue,
            backgroundColor: Color = Color.Transparent,
            borderWidth: Dp = 2.dp,
            borderColor: Color = Color.Blue,
            cornerRadius: Dp = 12.dp,
            useOnTapGesture: Boolean = false
        ) = ButtonComponentStyle(
            height = height,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = cornerRadius,
            useOnTapGesture = useOnTapGesture
        )
    }
}

/**
 * ButtonComponent:
 * - Accepts either imagePainter (Painter) OR imageVector (ImageVector).
 * - Supports gradient background, border, corner radius, and toggling ripple via useOnTapGesture.
 */
@Composable
fun ButtonComponent(
    text: String,
    imagePainter: Painter? = null,
    imageVector: ImageVector? = null,
    style: ButtonComponentStyle = ButtonComponentStyle.primary(),
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(style.cornerRadius)

    // base modifier (size + clip)
    val base = Modifier
        .fillMaxWidth()
        .height(style.height)
        .clip(shape)

    // background (gradient or solid)
    val bgModifier = if (style.linearGradient != null) {
        base.background(style.linearGradient)
    } else {
        base.background(style.backgroundColor)
    }

    // border if needed
    val borderedModifier = if (style.borderWidth > 0.dp) {
        bgModifier.border(BorderStroke(style.borderWidth, style.borderColor), shape)
    } else bgModifier

    // use LocalIndication.current for the platform ripple; pass null to disable ripple
    val indication = if (style.useOnTapGesture) null else LocalIndication.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = borderedModifier
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(style.imagePadding)
        ) {
            // show icon (Painter OR ImageVector)
            when {
                imagePainter != null -> Icon(
                    painter = imagePainter,
                    contentDescription = null,
                    tint = style.foregroundColor,
                    modifier = Modifier.size(style.imageWidth, style.imageHeight)
                )
                imageVector != null -> Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = style.foregroundColor,
                    modifier = Modifier.size(style.imageWidth, style.imageHeight)
                )
            }

            Text(
                text = text,
                style = TextStyle(
                    color = style.foregroundColor,
                    fontSize = style.fontSize,
                    fontFamily = style.fontFamily,
                    fontWeight = style.fontWeight
                )
            )
        }
    }
}

/**
 * Preview that mirrors your SwiftUI #Preview examples
 */
@Preview(showBackground = true)
@Composable
fun ButtonComponentPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // use ImageVector
            ButtonComponent(text = "Cool", imageVector = Icons.Filled.Favorite) {}

            ButtonComponent(
                text = "Click Me",
                style = ButtonComponentStyle.primary(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black
                )
            ) {}

            ButtonComponent(
                text = "Click Me",
                imageVector = Icons.Filled.Favorite,
                style = ButtonComponentStyle.primary(
                    foregroundColor = Color.Black,
                    backgroundColor = Color(0xFF00C853),
                    cornerRadius = 20.dp
                )
            ) {}

            ButtonComponent(
                text = "Button 3",
                style = ButtonComponentStyle.secondary()
            ) {}

            ButtonComponent(
                text = "Button 4",
                style = ButtonComponentStyle.secondary(
                    foregroundColor = Color.Black,
                    backgroundColor = Color.Cyan.copy(alpha = 0.5f),
                    borderWidth = 3.dp,
                    borderColor = Color.Black,
                    useOnTapGesture = true // disables ripple
                )
            ) {}

            ButtonComponent(
                text = "Button 5",
                style = ButtonComponentStyle(
                    foregroundColor = Color.White,
                    linearGradient = Brush.linearGradient(
                        colors = listOf(Color.Green, Color.Blue, Color.Red)
                    ),
                    cornerRadius = 1000.dp // "infinite" rounding
                )
            ) {}

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ButtonComponent(
                    text = "Manage Skill",
                    style = ButtonComponentStyle.primary(
                        height = 50.dp,
                        backgroundColor = Color(0xFF00C853),
                        cornerRadius = 1000.dp
                    )
                ) {}

                ButtonComponent(
                    text = "Import From DB",
                    imageVector = Icons.Filled.Download,
                    style = ButtonComponentStyle.secondary(
                        height = 50.dp,
                        foregroundColor = Color(0xFF00C853),
                        borderColor = Color(0xFF00C853),
                        cornerRadius = 1000.dp
                    )
                ) {}
            }
        }
    }
}