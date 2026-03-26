package com.app.ecarepro.feature.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.feature.menu.R
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.menu.component.MenuCategorySection
import com.app.ecarepro.feature.menu.component.MenuHeaderContent

@Composable
fun MenuDrawerContent(
    uiState: MenuUiState,
    handleIntent: (MenuIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header
        uiState.header?.let { header ->
            MenuHeaderContent(
                header = header,
                isAccountExpanded = uiState.isAccountSectionExpanded,
                onToggleAccountSection = { handleIntent(MenuIntent.ToggleAccountSection) },
            )
        }

        HorizontalDivider(
            color = Color(0xFFEEEEEE),
            thickness = 0.5.dp,
        )

        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.appColors.primary,
                    )
                }
            }
            uiState.isError -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EcareProEmptyState(
                        message = stringResource(R.string.menu_failed_to_load),
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = { handleIntent(MenuIntent.Retry) },
                        modifier = Modifier.padding(bottom = 16.dp),
                    ) {
                        Text(text = stringResource(R.string.menu_retry))
                    }
                }
            }
            uiState.categories.isEmpty() && uiState.isLoaded -> {
                EcareProEmptyState(
                    message = stringResource(R.string.menu_no_items),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    uiState.categories.forEach { category ->
                        item(key = "cat_${category.id}") {
                            MenuCategorySection(
                                category = category,
                                isExpanded = uiState.expandedCategoryIds.contains(category.id),
                                expandedMenuItemIds = uiState.expandedMenuItemIds,
                                onToggleCategory = { handleIntent(MenuIntent.ToggleCategory(it)) },
                                onMenuItemClicked = { handleIntent(MenuIntent.OnMenuItemClicked(it)) },
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } }
}
