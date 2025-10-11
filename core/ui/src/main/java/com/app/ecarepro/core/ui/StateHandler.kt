package com.app.ecarepro.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.ui.component.DefaultError
import com.app.ecarepro.designsystem.core.component.Loader

@Composable
fun <T> UiStateHandler(
    state: UiState<T>,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { Loader() },
    errorContent: @Composable (String, () -> Unit) -> Unit = { msg, retry ->
        DefaultError(msg, retry)
    },
    successContent: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> loadingContent()
        is UiState.Error -> errorContent(state.message, { onRetry?.invoke() })
        is UiState.Success -> successContent(state.data)
    }
}

@Preview
@Composable
fun StateHandlerLoadingPreview() {
    UiStateHandler(
        state = UiState.Loading,
        successContent = { data: String -> Text(text = data) }
    )
}

@Preview
@Composable
fun StateHandlerErrorPreview() {
    UiStateHandler(
        state = UiState.Error("Something went wrong"),
        onRetry = {},
        successContent = { data: String -> Text(text = data) }
    )
}

@Preview
@Composable
fun StateHandlerSuccessPreview() {
    UiStateHandler(
        state = UiState.Success("Sample Data"),
        successContent = { data: String ->
            Text(text = data)
        }
    )
}