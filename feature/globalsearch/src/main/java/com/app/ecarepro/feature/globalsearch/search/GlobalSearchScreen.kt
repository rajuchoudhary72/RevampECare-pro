package com.app.ecarepro.feature.globalsearch.search

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.globalsearch.SearchFilterType
import com.app.ecarepro.core.domain.model.globalsearch.SearchResultPresentation
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.globalsearch.search.component.SearchResultRow

@Composable
fun GlobalSearchScreen(
    navigateBack: () -> Unit,
    navigateToStudentProfile: (Int) -> Unit,
    navigateToModule: (Int) -> Unit,
    viewModel: GlobalSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is GlobalSearchEvent.NavigateBack -> navigateBack()
                is GlobalSearchEvent.NavigateToStudentProfile -> navigateToStudentProfile(event.studentId)
                is GlobalSearchEvent.NavigateToModule -> navigateToModule(event.menuId)
            }
        }
    }

    GlobalSearchContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GlobalSearchContent(
    uiState: GlobalSearchUiState,
    handleIntent: (GlobalSearchIntent) -> Unit,
    navigateBack: () -> Unit,
) {
    if (uiState.showFilterSheet) {
        EcareProSelectionBottomSheet(
            isVisible = true,
            title = "Filter",
            options = SearchFilterType.entries.map { it.displayName },
            selectedOptions = listOf(uiState.selectedFilter.displayName),
            isMultiSelection = false,
            onDismiss = { handleIntent(GlobalSearchIntent.DismissFilterSheet) },
            onOptionsSelected = { selected ->
                val displayName = selected.firstOrNull() ?: return@EcareProSelectionBottomSheet
                val filter = SearchFilterType.entries.firstOrNull { it.displayName == displayName }
                    ?: return@EcareProSelectionBottomSheet
                handleIntent(GlobalSearchIntent.OnFilterSelected(filter))
            },
        )
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Search for student, class etc..",
                onNavigationClicked = { handleIntent(GlobalSearchIntent.OnBackClicked) },
            )
        },
        containerColor = MaterialTheme.appColors.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SearchBar(
                searchText = uiState.searchText,
                selectedFilter = uiState.selectedFilter,
                showFilter = uiState.searchText.length >= 3,
                onSearchTextChanged = { handleIntent(GlobalSearchIntent.OnSearchTextChanged(it)) },
                onFilterClicked = { handleIntent(GlobalSearchIntent.OnFilterClicked) },
            )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }
                uiState.isError -> {
                    EcareProEmptyState(
                        message = "Something went wrong.\nTap to retry.",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { handleIntent(GlobalSearchIntent.OnRetry) },
                    )
                }
                uiState.searchText.length < 3 -> {
                    InitialState(modifier = Modifier.fillMaxSize())
                }
                uiState.isFiltering -> {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
                uiState.results.isEmpty() -> {
                    EcareProEmptyState(
                        message = "No results.\nMaybe try a broader search?",
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> {
                    ResultsList(
                        results = uiState.results,
                        onResultTapped = { handleIntent(GlobalSearchIntent.OnResultTapped(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    selectedFilter: SearchFilterType,
    showFilter: Boolean,
    onSearchTextChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.appColors.textSecondary,
        )

        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.appTypography.interRegular14px.copy(
                color = MaterialTheme.appColors.textPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Try 'Add new student'",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (showFilter) {
            Row(
                modifier = Modifier
                    .background(Color(0xFFEAEAEA), RoundedCornerShape(6.dp))
                    .clickable(onClick = onFilterClicked)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = selectedFilter.displayName,
                    style = MaterialTheme.appTypography.interMedium14px,
                    color = MaterialTheme.appColors.textPrimary,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Filter",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.appColors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun InitialState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.appColors.textSecondary,
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.appColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Your search journey starts here",
            style = MaterialTheme.appTypography.interRegular16px,
            color = MaterialTheme.appColors.textSecondary,
        )
    }
}

@Composable
private fun ResultsList(
    results: List<SearchResultPresentation>,
    onResultTapped: (SearchResultPresentation) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp),
    ) {
        items(
            items = results,
            key = { it.id },
        ) { result ->
            SearchResultRow(
                result = result,
                onClick = { onResultTapped(result) },
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFEEEEEE),
                thickness = 0.5.dp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GlobalSearchInitialPreview() {
    EcareProTheme {
        GlobalSearchContent(
            uiState = GlobalSearchUiState(),
            handleIntent = {},
            navigateBack = {},
        )
    }
}
