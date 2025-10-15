package com.app.ecarepro.feature.schoolcode.component


import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    initialOtp: String = "",
    boxSize: Dp = 56.dp,
    boxSpacing: Dp = 8.dp,
    cornerRadius: Dp = 8.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    errorBorderColor: Color = MaterialTheme.colorScheme.error,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textStyle: TextStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
    maskInput: Boolean = false,
    maskChar: Char = '●',
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Number,
    disableCopyPaste: Boolean = false,
    autoFocusFirst: Boolean = true,
    onOtpChange: (String) -> Unit = {},
    onOtpComplete: (String) -> Unit = {},
    onOtpSubmitDone: (() -> Unit)? = null, // optional when you want to hide keyboard etc.
) {
    val layoutDirection = LocalLayoutDirection.current
    val rtl = layoutDirection == LayoutDirection.Rtl

    // State: otp chars
    val otpState = remember {
        mutableStateListOf<Char?>().apply {
            repeat(otpLength) { add(null) }
            // if initialOtp provided, fill
            initialOtp.take(otpLength).forEachIndexed { i, c -> this[i] = c }
        }
    }

    // Focus requesters for each cell
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    // For keyboard control
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Clipboard manager for paste fallback if needed
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    // helper to compute current OTP string
    fun currentOtpString(): String = otpState.joinToString("") { it?.toString() ?: "" }

    // initial focus
    LaunchedEffect(autoFocusFirst) {
        if (autoFocusFirst) {
            // delay slightly so compose settles
            delay(120)
            focusRequesters.firstOrNull()?.requestFocus()
        }
    }

    // Notify parent of partial changes
    LaunchedEffect(otpState) {
        onOtpChange(currentOtpString())
        if (otpState.all { it != null }) {
            // all filled
            onOtpComplete(currentOtpString())
            // allow optionally hiding keyboard
            onOtpSubmitDone?.invoke()
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(boxSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until otpLength) {
            val char = otpState.getOrNull(index)
            val showChar = if (maskInput) maskChar.toString() else (char?.toString() ?: "")

            // Determine dynamic border color based on focus/error
            var isFocused by remember { mutableStateOf(false) }

            // semantics description for accessibility (announce index + filled/empty)
            val desc = remember(index, char) {
                "OTP digit ${index + 1} of $otpLength, " + (if (char != null) "filled" else "empty")
            }

            // render one box
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(backgroundColor)
                    .semantics { contentDescription = desc }
                    .then(
                        Modifier
                            .focusRequester(focusRequesters[index])
                    ),
                contentAlignment = Alignment.Center
            ) {
                // We'll use BasicTextField to fully control behavior
                var internalText by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = char?.toString() ?: ""
                        )
                    )
                }

                BasicTextField(
                    value = internalText,
                    onValueChange = { newValue ->
                        // If user pastes a multi-char string into this field, distribute.
                        val newText = newValue.text
                        if (newText.length > 1) {
                            // paste handling: distribute characters starting from this index.
                            val pasteChars = newText.toCharArray()
                            var putIndex = index
                            for (c in pasteChars) {
                                if (putIndex >= otpLength) break
                                otpState[putIndex] = c
                                putIndex++
                            }
                            // move focus to next empty or last
                            val nextFocusIndex =
                                (index + pasteChars.size).coerceAtMost(otpLength - 1)
                            // update internal for current
                            internalText = TextFieldValue(
                                text = otpState[index]?.toString() ?: ""
                            )
                            coroutineScope.launch {
                                // ensure focus move happens after state updates
                                delay(10)
                                focusRequesters.getOrNull(nextFocusIndex)?.requestFocus()
                            }
                        } else {
                            // single char: accept if not empty
                            val charStr = newText
                            if (charStr.isEmpty()) {
                                otpState[index] = null
                                internalText = TextFieldValue(text = "")
                            } else {
                                // only first character
                                val c = charStr[0]
                                otpState[index] = c
                                internalText = TextFieldValue(text = c.toString())
                                // move to next focus if available
                                val nextIndex = if (rtl) index - 1 else index + 1
                                if (nextIndex in 0 until otpLength) {
                                    focusRequesters[nextIndex].requestFocus()
                                } else {
                                    // finished last box -> hide keyboard maybe
                                    keyboardController?.hide()
                                }
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = textStyle,
                    cursorBrush = SolidColor(focusedBorderColor),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next,
                        capitalization = KeyboardCapitalization.None
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        // If all filled, call complete (already called via LaunchedEffect),
                        // otherwise hide keyboard
                        keyboardController?.hide()
                    }),
                    decorationBox = { innerTextField ->
                        // Draw border + text inside
                        val currentBorderColor =
                            when {
                                isError -> errorBorderColor
                                isFocused -> focusedBorderColor
                                else -> borderColor
                            }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(cornerRadius))
                                .then(
                                    if (disableCopyPaste) {
                                        // consume long-press to reduce system paste menu
                                        Modifier.pointerInput(Unit) {
                                            detectTapGestures(onLongPress = { /* consume */ })
                                        }
                                    } else Modifier
                                ),
                            shape = RoundedCornerShape(cornerRadius),
                            color = backgroundColor,
                            border = BorderStroke(borderWidth, currentBorderColor)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(2.dp)
                            ) {
                                // actual displayed char
                                if (internalText.text.isEmpty()) {
                                    // placeholder is empty
                                    Text(
                                        text = "",
                                        style = textStyle,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    // show char (masked if requested)
                                    Text(
                                        text = if (maskInput) maskChar.toString() else internalText.text,
                                        style = textStyle,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                // BasicTextField content (invisible but handles input)
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                ) {
                                    // place inner text field with transparent text color to avoid duplicated text
                                    innerTextField()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .matchParentSize()
                        .onKeyEvent { keyEvent ->
                            // detect backspace and move focus backward if needed
                            val pressedKey = keyEvent.key
                            if (pressedKey == Key.Backspace && keyEvent.nativeKeyEvent.action == 0) {
                                // Compose sends events differently across devices; we check content
                                if (otpState[index] == null) {
                                    // box empty -> move back and clear previous
                                    val prev = if (rtl) index + 1 else index - 1
                                    if (prev in 0 until otpLength) {
                                        otpState[prev] = null
                                        focusRequesters[prev].requestFocus()
                                    }
                                } else {
                                    // if not empty, clear current
                                    otpState[index] = null
                                }
                            }
                            false
                        }
                        .focusable(interactionSource = remember { MutableInteractionSource() })
                        .pointerInput(disableCopyPaste) {
                            if (disableCopyPaste) {
                                detectTapGestures(onLongPress = {}) // consume long press
                            }
                        }
                        .semantics {
                            // make it accessible as a button-like element
                            this.role = Role.Button
                        }
                )
            }
        }
    }
}
@Preview
@Composable
fun OtpTextFieldPreview() {
    EcareProTheme {
        OtpTextField()
    }

}