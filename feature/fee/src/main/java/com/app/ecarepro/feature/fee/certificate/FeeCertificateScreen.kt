package com.app.ecarepro.feature.fee.certificate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.fee.FeeSessionDomain
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FeeCertificateScreen(
    navigateBack: () -> Unit,
    viewModel: FeeCertificateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is FeeCertificateEvent.NavigateBack -> navigateBack()
                is FeeCertificateEvent.SharePdf -> {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        event.file,
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
                }
            }
        }
    }

    FeeCertificateContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeeCertificateContent(
    uiState: FeeCertificateUiState,
    handleIntent: (FeeCertificateIntent) -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "Fee certificate",
                onNavigationClicked = { handleIntent(FeeCertificateIntent.OnBackClicked) },
                actions = {
                    if (uiState.pdfFile != null) {
                        IconButton(onClick = { handleIntent(FeeCertificateIntent.OnShareClicked) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share certificate",
                                tint = MaterialTheme.appColors.textPrimary,
                            )
                        }
                    }
                },
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Session dropdown
            SessionDropdown(
                selectedSession = uiState.selectedSession,
                onClick = { handleIntent(FeeCertificateIntent.ShowSessionPicker) },
            )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        EcareProEmptyState(message = uiState.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { handleIntent(FeeCertificateIntent.Retry) }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.pdfFile != null -> {
                    PdfPageViewer(
                        file = uiState.pdfFile,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> {
                    EcareProEmptyState(message = "No certificate available")
                }
            }
        }
    }

    if (uiState.showSessionPicker) {
        EcareProSelectionBottomSheet(
            isVisible = true,
            title = "Select session",
            options = uiState.sessions.map { it.yearname },
            selectedOptions = listOfNotNull(uiState.selectedSession?.yearname),
            isMultiSelection = false,
            onDismiss = { handleIntent(FeeCertificateIntent.DismissSessionPicker) },
            onOptionsSelected = { selected ->
                val yearname = selected.firstOrNull() ?: return@EcareProSelectionBottomSheet
                val session = uiState.sessions.find { it.yearname == yearname } ?: return@EcareProSelectionBottomSheet
                handleIntent(FeeCertificateIntent.OnSessionSelected(session))
            },
        )
    }
}

@Composable
private fun SessionDropdown(
    selectedSession: FeeSessionDomain?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedSession?.yearname ?: "Select session",
                style = MaterialTheme.appTypography.interRegular14px,
                color = if (selectedSession != null) MaterialTheme.appColors.textPrimary
                        else MaterialTheme.appColors.textSecondary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select session",
                tint = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

@Composable
private fun PdfPageViewer(
    file: File,
    modifier: Modifier = Modifier,
) {
    val pages by produceState<List<Bitmap>>(initialValue = emptyList(), file) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val renderer = PdfRenderer(
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                )
                val bitmaps = (0 until renderer.pageCount).map { index ->
                    renderer.openPage(index).use { page ->
                        val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
                renderer.close()
                bitmaps
            }.getOrDefault(emptyList())
        }
    }

    if (pages.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(pages) { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat()),
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Fee Certificate - Loading")
@Composable
private fun PreviewFeeCertificateLoading() {
    EcareProTheme {
        FeeCertificateContent(
            uiState = FeeCertificateUiState(isLoading = true),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Fee Certificate - No Certificate")
@Composable
private fun PreviewFeeCertificateEmpty() {
    EcareProTheme {
        FeeCertificateContent(
            uiState = FeeCertificateUiState(
                isLoading = false,
                selectedSession = FeeSessionDomain(yrid = 5, yearname = "2021-2022", isActive = true),
                sessions = listOf(
                    FeeSessionDomain(yrid = 5, yearname = "2021-2022", isActive = true),
                    FeeSessionDomain(yrid = 4, yearname = "2020-2021", isActive = false),
                ),
                pdfFile = null,
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Fee Certificate - Error")
@Composable
private fun PreviewFeeCertificateError() {
    EcareProTheme {
        FeeCertificateContent(
            uiState = FeeCertificateUiState(
                isLoading = false,
                error = "Failed to load certificate. Please try again.",
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Fee Certificate - Session Picker")
@Composable
private fun PreviewFeeCertificateSessionPicker() {
    EcareProTheme {
        FeeCertificateContent(
            uiState = FeeCertificateUiState(
                isLoading = false,
                sessions = listOf(
                    FeeSessionDomain(yrid = 5, yearname = "2021-2022", isActive = true),
                    FeeSessionDomain(yrid = 4, yearname = "2020-2021", isActive = false),
                    FeeSessionDomain(yrid = 3, yearname = "2019-2020", isActive = false),
                ),
                selectedSession = FeeSessionDomain(yrid = 5, yearname = "2021-2022", isActive = true),
                showSessionPicker = true,
            ),
            handleIntent = {},
        )
    }
}
