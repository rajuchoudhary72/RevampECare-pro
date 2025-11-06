package com.app.ecarepro.designsystem.core.component

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
internal fun CodeInputField(
    value: String?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onValueChanged: (String?) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    strokeWidth: Dp,
    defaultStrokeColor: Color,
    filledStrokeColor: Color,
    errorStrokeColor: Color,
    cornerRadius: Dp,
    backgroundColor: Color,
    textStyle: TextStyle,
    textColor: Color,
    textColorError: Color,
) {
    val text by remember(value) {
        mutableStateOf(
            TextFieldValue(
                text = value.orEmpty(),
                selection = TextRange(value.orEmpty().length)
            )
        )
    }

    val strokeColor = when {
        isError -> errorStrokeColor
        !value.isNullOrEmpty() -> filledStrokeColor
        else -> defaultStrokeColor
    }
    val textColor = when {
        isError -> textColorError
        else -> textColor
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = strokeWidth,
                color = strokeColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(if (text.text.isEmpty()) backgroundColor else White),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                val newValue = newText.text.uppercase()
                if (newValue.length <= 1) {
                    onValueChanged(newValue.ifEmpty { null })
                }
            },
            cursorBrush = SolidColor(MaterialTheme.appColors.primary),
            singleLine = true,
            textStyle = textStyle.copy(
                textAlign = TextAlign.Center,
                color = textColor
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier
                .padding(6.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event ->
                    val didPressDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
                    if (didPressDelete && value == null) {
                        onKeyboardBack()
                    }
                    false
                },
        )
    }
}