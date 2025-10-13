package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTypography

// ---------- TextComponent and TextComponentStyle already defined ----------
data class TextComponentStyle(
    val foregroundColor: Color = Color.Black,
    val backgroundColor: Color = Color.Transparent,
    val textStyle: androidx.compose.ui.text.TextStyle = EcareProTypography.interRegular16px,
    val alignment: Alignment = Alignment.CenterStart
)

data class TextComponent(
    val text: String,
    val style: TextComponentStyle = TextComponentStyle()
)

@Composable
fun TextComponentView(component: TextComponent, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(component.style.backgroundColor)
            .padding(8.dp),
        contentAlignment = component.style.alignment
    ) {
        androidx.compose.material3.Text(
            text = component.text,
            color = component.style.foregroundColor,
            style = component.style.textStyle
        )
    }
}

// ---------- Preview ----------
@Preview(showBackground = true, widthDp = 360, heightDp = 200)
@Composable
fun TextComponentPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFEFEF))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Default TextComponent (Inter, Regular, 16sp)
        TextComponentView(
            component = TextComponent(
                text = "Default TextComponent"
            )
        )

        // Customized TextComponent (Nunito Bold, White text, Black background, Center)
        TextComponentView(
            component = TextComponent(
                text = "Custom Styled TextComponent",
                style = TextComponentStyle(
                    foregroundColor = Color.White,
                    backgroundColor = Color.Black,
                    textStyle = EcareProTypography.nunitoBold12px,
                    alignment = Alignment.Center
                )
            )
        )

        // Another variation with different alignment
        TextComponentView(
            component = TextComponent(
                text = "Right Aligned Text",
                style = TextComponentStyle(
                    foregroundColor = Color.Blue,
                    backgroundColor = Color.Yellow,
                    textStyle = EcareProTypography.interMedium16px,
                    alignment = Alignment.CenterEnd
                )
            )
        )
    }
}