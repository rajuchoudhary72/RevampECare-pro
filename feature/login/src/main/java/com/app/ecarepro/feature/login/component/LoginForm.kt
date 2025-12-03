package com.app.ecarepro.feature.login.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProOutlinedTextField
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    username: String,
    password: String,
    onUsernameChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onLoginClicked: () -> Unit = {},
    onForgotPasswordClicked: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        var isUsernameFocused by remember { mutableStateOf(false) }
        var isPasswordFocused by remember { mutableStateOf(false) }


        EcareProOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isUsernameFocused = it.isFocused
                },
            value = username,
            isError = isError,
            onValueChange = onUsernameChanged,
            label = {
                Text(
                    text = if (isUsernameFocused || username.isNotEmpty()) "Username *" else "Enter your username",
                    style = MaterialTheme.appTypography.interRegular14px.copy(color = if(isError) MaterialTheme.appColors.error else  MaterialTheme.appColors.textSecondary)
                )
            },
        )

        EcareProOutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            isError = isError,
            label = {
                Text(
                    text = if (isPasswordFocused || username.isNotEmpty()) "Password *" else "Enter your password",
                    style = MaterialTheme.appTypography.interRegular14px.copy(color = if(isError) MaterialTheme.appColors.error else MaterialTheme.appColors.textSecondary)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isPasswordFocused = it.isFocused
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        TextButton(
            onClick = onForgotPasswordClicked,
            title = "Forgot Password?",
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
        )



        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onLoginClicked()
            },
            title = "Login"
        )

    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LoginFormPreview() {
    EcareProTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LoginForm(
                username = "",
                password = "",
                onUsernameChanged = {},
                onPasswordChanged = {},
                onLoginClicked = {},
                onForgotPasswordClicked = {}
            )
        }
    }
}