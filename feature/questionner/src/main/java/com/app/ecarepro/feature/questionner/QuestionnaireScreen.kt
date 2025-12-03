package com.app.ecarepro.feature.questionner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
    onCreateNewClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuestionnaireScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onTabSelected = { tab ->
            viewModel.handleIntent(QuestionnaireIntent.OnTabChanged(tab))
        },
        onCreateNewClick = {
            viewModel.handleIntent(QuestionnaireIntent.OnCreateNewClicked)
            onCreateNewClick()
        },
        onLikeClick = { questionId ->
            viewModel.handleIntent(QuestionnaireIntent.OnLikeClicked(questionId))
        },
        onQuestionClick = { questionId ->
            viewModel.handleIntent(QuestionnaireIntent.OnQuestionClicked(questionId))
            onQuestionClick(questionId)
        },
        onLoadMore = {
            viewModel.handleIntent(QuestionnaireIntent.LoadMoreQuestions)
        }
    )
}

@Composable
internal fun QuestionnaireScreenContent(
    uiState: QuestionnaireUiState,
    onBackClick: () -> Unit,
    onTabSelected: (QuestionnaireTab) -> Unit,
    onCreateNewClick: () -> Unit,
    onLikeClick: (Int) -> Unit,
    onQuestionClick: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
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
            onLoadMore()
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
                contentWindowInsets = WindowInsets.systemBars,
                topBar = {
                    QuestionnaireHeader(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = onTabSelected,
                        onBackClick = onBackClick,
                        onCreateNewClick = onCreateNewClick
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
                                    onLikeClick = { onLikeClick(question.qid) },
                                    onQuestionClick = { onQuestionClick(question.qid) },

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

@Preview(showBackground = true, name = "With Questions")
@Composable
fun QuestionnaireScreenPreview() {
    EcareProTheme {
        QuestionnaireScreenContent(
            uiState = QuestionnaireUiState(
                selectedTab = QuestionnaireTab.ALL,
                questions = listOf(
                    com.app.ecarepro.core.domain.model.Question(
                        qid = 175,
                        qType = 1,
                        que = "How does the communication between teachers, students, and parents feel through the app?",
                        queImg = null,
                        updatedBy = "Harsimrat Kaur",
                        updatedOn = "05 Apr, 25 • 11:26 AM",
                        photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
                        likes = 5,
                        isILike = false,
                        totalAnswer = 3,
                        isAnswered = false,
                        userID = 2885,
                        userType = 2,
                        isVerified = true,
                        status = null,
                        isSelected = false
                    ),
                    com.app.ecarepro.core.domain.model.Question(
                        qid = 176,
                        qType = 1,
                        que = "What features would you like to see added to the app?",
                        queImg = null,
                        updatedBy = "John Doe",
                        updatedOn = "04 Apr, 25 • 09:15 AM",
                        photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
                        likes = 12,
                        isILike = true,
                        totalAnswer = 7,
                        isAnswered = true,
                        userID = 2886,
                        userType = 2,
                        isVerified = true,
                        status = null,
                        isSelected = false
                    )
                ),
                isLoading = false,
                errorMessage = null,
                currentPage = 1,
                totalQuestions = 2,
                hasMorePages = false
            ),
            onBackClick = {},
            onTabSelected = {},
            onCreateNewClick = {},
            onLikeClick = {},
            onQuestionClick = {},
            onLoadMore = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun QuestionnaireScreenLoadingPreview() {
    EcareProTheme {
        QuestionnaireScreenContent(
            uiState = QuestionnaireUiState(
                selectedTab = QuestionnaireTab.ALL,
                questions = emptyList(),
                isLoading = true,
                errorMessage = null
            ),
            onBackClick = {},
            onTabSelected = {},
            onCreateNewClick = {},
            onLikeClick = {},
            onQuestionClick = {},
            onLoadMore = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun QuestionnaireScreenEmptyPreview() {
    EcareProTheme {
        QuestionnaireScreenContent(
            uiState = QuestionnaireUiState(
                selectedTab = QuestionnaireTab.ALL,
                questions = emptyList(),
                isLoading = false,
                errorMessage = null
            ),
            onBackClick = {},
            onTabSelected = {},
            onCreateNewClick = {},
            onLikeClick = {},
            onQuestionClick = {},
            onLoadMore = {}
        )
    }
}
