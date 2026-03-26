package com.app.ecarepro.feature.library.book_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is BookDetailEvent.NavigateBack -> navigateBack()
                is BookDetailEvent.ShowMessage -> { /* snackbar could be added here */ }
            }
        }
    }

    BookDetailContent(
        uiState = uiState,
        onBackClick = { viewModel.handleIntent(BookDetailIntent.OnBackClicked) },
    )
}

@Composable
private fun BookDetailContent(
    uiState: BookDetailUiState,
    onBackClick: () -> Unit,
) {
    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.isError -> {
            EcareProEmptyState(message = "Something went wrong")
        }
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                HeroImageSection(
                    title = uiState.title,
                    coverImageURL = uiState.coverImageURL,
                    onBackClick = onBackClick,
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    itemsIndexed(uiState.fields) { index, field ->
                        DetailFieldRow(
                            field = field,
                            isAvailable = uiState.isAvailable,
                        )
                        if (index < uiState.fields.lastIndex) {
                            DashedDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroImageSection(
    title: String,
    coverImageURL: String?,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
    ) {
        if (!coverImageURL.isNullOrEmpty()) {
            EcareProAsyncImage(
                imageUrl = coverImageURL,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C1C1C)),
            )
        }

        // Dark gradient overlay at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopStart)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                    )
                ),
        )

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 8.dp, start = 4.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 56.dp, top = 16.dp, end = 16.dp),
            maxLines = 2,
        )
    }
}

@Composable
private fun DetailFieldRow(
    field: BookDetailField,
    isAvailable: Boolean,
) {
    val valueColor = if (field.isStatusField && isAvailable) {
        MaterialTheme.appColors.success
    } else {
        MaterialTheme.appColors.textPrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = field.label,
            style = MaterialTheme.appTypography.interRegular13px,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = field.value,
            style = MaterialTheme.appTypography.interMedium14px,
            color = valueColor,
        )
    }
}

@Composable
private fun DashedDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .drawWithContent {
                val dashWidth = 8.dp.toPx()
                val gapWidth = 4.dp.toPx()
                val strokeWidth = 1.dp.toPx()
                var x = 0f
                drawContent()
                while (x < size.width) {
                    drawLine(
                        color = Color(0xFFEEEEEE),
                        start = androidx.compose.ui.geometry.Offset(x, size.height / 2),
                        end = androidx.compose.ui.geometry.Offset((x + dashWidth).coerceAtMost(size.width), size.height / 2),
                        strokeWidth = strokeWidth,
                    )
                    x += dashWidth + gapWidth
                }
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailContentPreview() {
    EcareProTheme {
        BookDetailContent(
            uiState = BookDetailUiState(
                isLoading = false,
                title = "The Great Gatsby",
                isAvailable = true,
                fields = listOf(
                    BookDetailField(label = "Accession No", value = "ACC001"),
                    BookDetailField(label = "Author", value = "F. Scott Fitzgerald"),
                    BookDetailField(label = "Status", value = "Available", isStatusField = true),
                ),
            ),
            onBackClick = {},
        )
    }
}
