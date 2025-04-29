package com.app.ecarepro.compose.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.R
import com.app.ecarepro.compose.UiState
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE

@Composable
fun <T> LoadingComposable(
    modifier: Modifier = Modifier,
    uiState: UiState<T>,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit
) {
    if (uiState.isLoading()) {
        LoadingView(modifier = modifier)
    } else if (uiState.getErrorOrNull() != null) {
        ErrorView(
            message = uiState.getErrorOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE, onRetry = onRetry
        )
    } else {
        content(uiState.getValueOrNull() ?: throw IllegalStateException("Value should not be null"))
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error), style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    LoadingView()
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    ErrorView(message = "Failed to load data.") {}
}

@Preview(showBackground = true)
@Composable
fun LoadingComposableLoadingPreview() {
    LoadingComposable(uiState = UiState.Loading, onRetry = {}) {}
}

@Preview(showBackground = true)
@Composable
fun LoadingComposableErrorPreview() {
    LoadingComposable(
        uiState = UiState.Error(Exception("Failed to load data.")), onRetry = {}) {}
}

@Preview(showBackground = true)
@Composable
fun LoadingComposableSuccessPreview() {
    LoadingComposable(uiState = UiState.Success(Unit), onRetry = {}) {
        Text(text = "Data is here")
    }
}