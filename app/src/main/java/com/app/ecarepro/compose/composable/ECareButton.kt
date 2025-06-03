package com.app.ecarepro.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ECareButton(
    title: String,
    style: Style,
    triggeredAction: (() -> Unit)? = null
) {
    Button(
        onClick = { triggeredAction?.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .height(style.height ?: ButtonDefaults.MinHeight)
            .then(
                style.borderColor?.let {
                    Modifier.border(1.dp, it)
                } ?: Modifier
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = style.backgroundColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
      /*  Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = when (style.alignment) {
                Alignment.CenterStart -> Alignment.CenterStart
                Alignment.CenterEnd -> Alignment.CenterEnd
                Alignment.Center -> Alignment.Center
            }
        ) {
            Text(
                text = title,
                color = style.foregroundColor,
                fontSize = style.fontSize ?: 16.sp,
                fontWeight = style.fontWeight ?: FontWeight.Normal,
                fontFamily = style.fontFamily,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }*/
    }
}

data class Style(
    val fontSize: TextUnit? = null,
    val fontWeight: FontWeight? = null,
    val fontFamily: FontFamily? = null,
    val backgroundColor: Color = Color.Blue,
    val foregroundColor: Color = Color.White,
    val borderColor: Color? = null,
    val height: Dp? = null,
    val alignment: Alignment = Alignment.CenterStart
) {
    companion object {
        val primary = Style(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            backgroundColor = Color.Blue,
            foregroundColor = Color.White,
            borderColor = null,
            height = 48.dp
        )

        val secondary = Style(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            backgroundColor = Color.Transparent,
            foregroundColor = Color.Blue,
            borderColor = Color.Blue,
            height = 48.dp
        )
    }
}

enum class Alignment {
    Leading,
    Center,
    Trailing
}