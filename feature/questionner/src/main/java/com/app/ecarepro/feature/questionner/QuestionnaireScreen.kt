package com.app.ecarepro.feature.questionner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.questionner.component.QuestionCard
import com.app.ecarepro.feature.questionner.component.QuestionnaireHeader

@Composable
fun QuestionnaireScreen(
    viewModel: QuestionnaireViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onQuestionClick: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Detect when scrolled to bottom for pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                    lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3 &&
                    !uiState.isLoading &&
                    uiState.hasMorePages
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.handleIntent(QuestionnaireIntent.LoadMoreQuestions)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        EcareProBackground(
            overlayColor = MaterialTheme.appColors.background
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    QuestionnaireHeader(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = { tab ->
                            viewModel.handleIntent(QuestionnaireIntent.OnTabChanged(tab))
                        },
                        onBackClick = onBackClick,
                        onCreateNewClick = {
                            viewModel.handleIntent(QuestionnaireIntent.OnCreateNewClicked)
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (uiState.errorMessage != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "Error occurred",
                                color = MaterialTheme.appColors.error
                            )
                        }
                    } else if (uiState.questions.isEmpty() && !uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No questions available",
                                color = MaterialTheme.appColors.textSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.appColors.background)
                        ) {
                            items(
                                items = uiState.questions,
                                key = { it.qid }
                            ) { question ->
                                QuestionCard(
                                    question = question,
                                    onLikeClick = {
                                        viewModel.handleIntent(
                                            QuestionnaireIntent.OnLikeClicked(question.qid)
                                        )
                                    },
                                    onQuestionClick = {
                                        viewModel.handleIntent(
                                            QuestionnaireIntent.OnQuestionClicked(question.qid)
                                        )
                                        onQuestionClick(question.qid)
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                )
                            }

                            // Show loading indicator at the bottom when loading more
                            if (uiState.isLoading && uiState.questions.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.appColors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Show full-screen loader only for initial load
        if (uiState.isLoading && uiState.questions.isEmpty()) {
            Loader()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionnaireScreenPreview() {
    EcareProTheme {
        QuestionnaireScreen()
    }
}
