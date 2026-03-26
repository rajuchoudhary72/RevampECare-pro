package com.app.ecarepro.feature.library.library_main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.app.ecarepro.feature.library.library_main.component.BookCardRow
import com.app.ecarepro.feature.library.library_main.component.MyAccountCard

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToBookDetail: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is LibraryEvent.NavigateToBookDetail -> navigateToBookDetail(event.bookId)
                is LibraryEvent.ShowMessage -> snackbarMessage = event.message
            }
        }
    }

    LibraryContent(
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
private fun LibraryContent(
    uiState: LibraryUiState,
    handleIntent: (LibraryIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    navigateBack: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "e-Library",
                onNavigationClicked = navigateBack,
            )
        },
        bottomBar = {
            if (uiState.selectedTab == LibraryTab.LATEST_BOOKS) {
                BottomSearchBarView(
                    searchText = uiState.searchQuery,
                    onSearchTextChange = { handleIntent(LibraryIntent.OnSearchQueryChanged(it)) },
                    placeholder = "Search by name, author or publication...",
                )
            }
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LibraryTabBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { handleIntent(LibraryIntent.SelectTab(it)) },
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
                uiState.selectedTab == LibraryTab.LATEST_BOOKS -> {
                    LatestBooksContent(uiState = uiState, handleIntent = handleIntent)
                }
                else -> {
                    MyAccountContent(uiState = uiState, handleIntent = handleIntent)
                }
            }
        }
    }
}

@Composable
private fun LibraryTabBar(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        LibraryTab.entries.forEach { tab ->
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
private fun LatestBooksContent(
    uiState: LibraryUiState,
    handleIntent: (LibraryIntent) -> Unit,
) {
    val books = if (uiState.isSearchActive) uiState.searchResults else uiState.latestBooks

    if (uiState.isSearchActive && uiState.isSearchLoading && uiState.searchResults.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (books.isEmpty()) {
        EcareProEmptyState(message = "No results found")
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = books,
                key = { index, book -> "${book.id}_$index" },
            ) { _, book ->
                BookCardRow(
                    book = book,
                    onClick = { handleIntent(LibraryIntent.OnBookClicked(book.id)) },
                )
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

                if (uiState.isSearchActive) {
                    LaunchedEffect(book.id) {
                        handleIntent(LibraryIntent.OnSearchResultAppeared(book.id))
                    }
                }
            }
        }
    }
}

@Composable
private fun MyAccountContent(
    uiState: LibraryUiState,
    handleIntent: (LibraryIntent) -> Unit,
) {
    if (uiState.myAccountBooks.isEmpty()) {
        EcareProEmptyState(message = "No results found")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = uiState.myAccountBooks,
                key = { it.id },
            ) { book ->
                MyAccountCard(
                    book = book,
                    isExpanded = uiState.expandedBookIds.contains(book.id),
                    onToggle = { handleIntent(LibraryIntent.ToggleMyAccountCard(book.id)) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryContentPreview() {
    EcareProTheme {
        LibraryContent(
            uiState = LibraryUiState(
                isLoading = false,
                latestBooks = listOf(
                    BookCardPresentation(
                        id = 1,
                        title = "The Great Gatsby",
                        author = "F. Scott Fitzgerald",
                        publication = "Scribner",
                        coverImageURL = null,
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
