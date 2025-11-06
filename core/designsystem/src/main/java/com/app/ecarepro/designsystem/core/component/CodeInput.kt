package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.Blue
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun CodeInput(
    modifier: Modifier = Modifier,
    code: String? = null,
    otpLength: Int = 6,
    strokeWidth: Dp = 1.dp,
    itemWidth: Dp = Dp.Unspecified,
    itemHeight: Dp = Dp.Unspecified,
    isError: Boolean = false,
    textColor: Color = MaterialTheme.appColors.textPrimary,
    textColorError: Color = MaterialTheme.appColors.error,
    defaultStrokeColor: Color = MaterialTheme.appColors.border,
    filledStrokeColor: Color = Blue,
    errorStrokeColor: Color = MaterialTheme.appColors.warning,
    cornerRadius: Dp = 8.dp,
    backgroundColor: Color = White,
    textStyle: TextStyle = TextStyle.Default.copy(
        textAlign = TextAlign.Center,
        color = MaterialTheme.appColors.primary
    ),
    onOtpEntered: (String) -> Unit,
) {
    var codeState by remember {
        // --- MODIFIED LOGIC ---
        // Always create a list of size 'otpLength'.
        val initialCode = MutableList<String?>(otpLength) { null }.apply {
            // Fill it with the provided code if it's not null or empty.
            if (!code.isNullOrEmpty()) {
                for (i in 0 until minOf(code.length, otpLength)) {
                    this[i] = code[i].toString()
                }
            }
        }
        mutableStateOf(CodeState(code = initialCode))
    }
    val focusRequesters = remember {
        List(otpLength) { FocusRequester() }
    }
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        codeState.code.forEachIndexed { index, value ->

            val itemModifier = Modifier
                .weight(1f)
                .then(
                    // Apply explicit width if provided
                    if (itemWidth != Dp.Unspecified) Modifier.width(itemWidth)
                    else Modifier
                )
                .then(
                    // Apply explicit height if provided
                    if (itemHeight != Dp.Unspecified) Modifier.height(itemHeight)
                    // Otherwise, if NO explicit height or width, fall back to aspect ratio
                    else if (itemWidth == Dp.Unspecified) Modifier.aspectRatio(1f)
                    else Modifier
                )

            CodeInputField(
                value = value,
                focusRequester = focusRequesters[index],
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        codeState = codeState.copy(focusedIndex = index)
                    }
                },
                onValueChanged = { newValue ->
                    val oldCode = codeState.code
                    val newCode = oldCode.toMutableList()
                    newCode[index] = newValue
                    codeState = codeState.copy(code = newCode)

                    if (newValue != null) {
                        focusRequesters.getOrNull(index + 1)?.requestFocus()
                    }

                    if (newCode.none { it.isNullOrEmpty() }) {
                        val enteredOtp = newCode.joinToString("")
                        onOtpEntered(enteredOtp)
                        focusRequesters.forEach { it.freeFocus() }
                        focusManager.clearFocus()
                        keyboardManager?.hide()
                    }
                },
                onKeyboardBack = {
                    focusRequesters.getOrNull(index - 1)?.requestFocus()
                },
                modifier = itemModifier,
                strokeWidth = strokeWidth,
                defaultStrokeColor = defaultStrokeColor,
                filledStrokeColor = filledStrokeColor,
                errorStrokeColor = errorStrokeColor,
                cornerRadius = cornerRadius,
                backgroundColor = backgroundColor,
                textStyle = textStyle,
                isError = isError,
                textColor = textColor,
                textColorError = textColorError
            )
        }
    }
}

internal data class CodeState(
    val code: List<String?> = (1..6).map { null },
    val focusedIndex: Int? = null
)

@Preview(showBackground = true)
@Composable
private fun CodeInputPreview() {
    EcareProTheme {
        CodeInput(
            onOtpEntered = {},
            code = "13"
        )
    }
}