package com.app.ecarepro.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.component.EcareProErrorState
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme

@Composable
fun <T> UiStateHandler(
    modifier: Modifier = Modifier,
    state: UiState<T>,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { Loader(modifier) },
    errorContent: @Composable (String, () -> Unit) -> Unit = { msg, retry ->
        EcareProErrorState(modifier, message = msg, onRetry = retry)
    },
    successContent: @Composable (T) -> Unit,
) {
    when (state) {
        is UiState.Loading -> loadingContent()
        is UiState.Error -> errorContent(state.message, { onRetry?.invoke() })
        is UiState.Success -> successContent(state.data)
    }
}

@Preview(showBackground = true)
@Composable
fun StateHandlerLoadingPreview() {
    EcareProTheme {
        UiStateHandler(
            state = UiState.Loading,
            successContent = { data: String -> Text(text = data) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StateHandlerErrorPreview() {
    EcareProTheme {
        UiStateHandler(
            state = UiState.Error("Something went wrong"),
            onRetry = {},
            successContent = { data: String -> Text(text = data) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StateHandlerSuccessPreview() {
    EcareProTheme {
        UiStateHandler(
            state = UiState.Success("Sample Data"),
            successContent = { data: String ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(text = data)
                }
            }
        )
    }
}