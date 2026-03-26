package com.app.ecarepro.feature.ebook.ebook_main

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.ebook.ebook_main.component.EBookCardRow

@Composable
fun EBookScreen(
    viewModel: EBookViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPdf: (title: String, url: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is EBookEvent.OpenPdf -> navigateToPdf(event.title, event.url)
                is EBookEvent.ShowMessage -> snackbarMessage = event.message
            }
        }
    }

    EBookContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EBookContent(
    uiState: EBookUiState,
    handleIntent: (EBookIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    navigateBack: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "e-books",
                onNavigationClicked = navigateBack,
            )
        },
        bottomBar = {
            if (uiState.selectedTab == EBookTab.MY_SCHOOL_LIBRARY) {
                BottomSearchBarView(
                    searchText = uiState.searchQuery,
                    onSearchTextChange = { handleIntent(EBookIntent.OnSearchQueryChanged(it)) },
                    placeholder = "Search by name, author or publication...",
                )
            }
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                EBookTabBar(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { handleIntent(EBookIntent.SelectTab(it)) },
                )

                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.isError -> {
                        EcareProEmptyState(message = "Something went wrong")
                    }
                    uiState.selectedTab == EBookTab.MY_SCHOOL_LIBRARY -> {
                        MySchoolLibraryContent(uiState = uiState, handleIntent = handleIntent)
                    }
                    else -> {
                        MegaEBookContent(megaBookLink = uiState.megaBookLink)
                    }
                }
            }

            if (uiState.isSilentLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun EBookTabBar(
    selectedTab: EBookTab,
    onTabSelected: (EBookTab) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        EBookTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = tab.displayName(),
                    style = if (isSelected) MaterialTheme.appTypography.interSemiBold14px
                    else MaterialTheme.appTypography.interRegular14px,
                    color = if (isSelected) MaterialTheme.appColors.primary
                    else MaterialTheme.appColors.textPrimary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(MaterialTheme.appColors.primary),
                    )
                }
            }
        }
    }
    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
}

@Composable
private fun MySchoolLibraryContent(
    uiState: EBookUiState,
    handleIntent: (EBookIntent) -> Unit,
) {
    if (uiState.eBooks.isEmpty()) {
        EcareProEmptyState(message = "No results found")
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = uiState.eBooks,
                key = { it.id },
            ) { book ->
                EBookCardRow(
                    book = book,
                    onClick = { handleIntent(EBookIntent.OnEBookClicked(book)) },
                )
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun MegaEBookContent(megaBookLink: String?) {
    if (!megaBookLink.isNullOrEmpty()) {
        val context = LocalContext.current
        AndroidView(
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(megaBookLink)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        EcareProEmptyState(message = "No results found")
    }
}

@Preview(showBackground = true)
@Composable
private fun EBookContentPreview() {
    EcareProTheme {
        EBookContent(
            uiState = EBookUiState(
                isLoading = false,
                eBooks = listOf(
                    EBookCardPresentation(
                        id = "EB001",
                        title = "Digital Science",
                        author = "John Smith",
                        coverImageURL = null,
                        accessionNo = "EB001",
                    ),
                ),
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            navigateBack = {},
        )
    }
}
