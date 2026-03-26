package com.app.ecarepro.feature.gallery.kids.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.KidsPhoto
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.component.EcareProScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsDetailScreen(
    viewModel: KidsDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is KidsDetailEvent.NavigateBack -> navigateBack()
                is KidsDetailEvent.OpenPhoto -> { /* full-screen viewer can be added later */ }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = viewModel.navKey.title,
                onNavigationClicked = { viewModel.handleIntent(KidsDetailIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                if (data.description.isNotBlank()) {
                    Text(
                        text = stripHtmlTags(data.description),
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }

                if (data.photos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (data.albumIcon.isNotBlank()) {
                                EcareProAsyncImage(
                                    imageUrl = data.albumIcon,
                                    contentDescription = data.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                            Text(
                                text = "No photos yet",
                                style = MaterialTheme.appTypography.interRegular14px,
                                color = MaterialTheme.appColors.textSecondary,
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(data.photos, key = { it.id.ifBlank { it.fileName } }) { photo ->
                            KidsPhotoItem(
                                photo = photo,
                                onClick = { viewModel.handleIntent(KidsDetailIntent.OnPhotoClicked(photo.fileName)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KidsPhotoItem(
    photo: KidsPhoto,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(MaterialTheme.appColors.surface)
            .clickable(onClick = onClick),
    ) {
        if (photo.fileName.isNotBlank()) {
            EcareProAsyncImage(
                imageUrl = photo.fileName,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun stripHtmlTags(html: String): String =
    html.replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ").trim()

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Light")
@Composable
private fun KidsDetailScreenPreviewLight() {
    val data = KidsDetailUiState(
        kid = "kid1",
        title = "Kids Corner Media Headlines",
        description = "",
        albumIcon = "https://picsum.photos/300",
        yearName = "2025-2026",
        totalPhoto = 0,
        createdOn = "08 Aug",
        updatedOn = "08 Aug",
        photos = emptyList(),
    )
    EcareProTheme {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = data.title,
                    onNavigationClicked = {},
                )
            },
            snackbarHostState = remember { SnackbarHostState() },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                if (data.photos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (data.albumIcon.isNotBlank()) {
                                EcareProAsyncImage(
                                    imageUrl = data.albumIcon,
                                    contentDescription = data.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                            Text(
                                text = "No photos yet",
                                style = MaterialTheme.appTypography.interRegular14px,
                                color = MaterialTheme.appColors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun KidsDetailScreenPreviewDark() {
    val data = KidsDetailUiState(
        kid = "kid1",
        title = "Kids Corner Media Headlines",
        description = "",
        albumIcon = "https://picsum.photos/300",
        yearName = "2025-2026",
        totalPhoto = 0,
        createdOn = "08 Aug",
        updatedOn = "08 Aug",
        photos = emptyList(),
    )
    EcareProTheme {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = data.title,
                    onNavigationClicked = {},
                )
            },
            snackbarHostState = remember { SnackbarHostState() },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                if (data.photos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (data.albumIcon.isNotBlank()) {
                                EcareProAsyncImage(
                                    imageUrl = data.albumIcon,
                                    contentDescription = data.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                            Text(
                                text = "No photos yet",
                                style = MaterialTheme.appTypography.interRegular14px,
                                color = MaterialTheme.appColors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}
