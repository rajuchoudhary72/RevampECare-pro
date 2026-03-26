package com.app.ecarepro.feature.survey.survey_questions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.feature.survey.R
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.survey.survey_questions.component.QuestionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyQuestionsScreen(
    viewModel: SurveyQuestionsViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is SurveyQuestionsEvent.NavigateBack -> navigateBack()
                is SurveyQuestionsEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    EcareProScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.survey_form_title),
                onNavigationClicked = { viewModel.handleIntent(SurveyQuestionsIntent.OnBackClicked) },
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(SurveyQuestionsIntent.OnSubmitClicked) },
                        enabled = !uiState.isSubmitting,
                    ) {
                        Text(
                            text = stringResource(R.string.survey_submit),
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = MaterialTheme.appColors.primary,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Text(
                text = stringResource(R.string.survey_mandatory_note),
                style = MaterialTheme.appTypography.interRegular13px,
                color = MaterialTheme.appColors.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }

                uiState.isError -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        EcareProEmptyState(message = uiState.errorMessage ?: "Failed to load questions")
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = uiState.questions, key = { it.queID }) { question ->
                            QuestionCard(
                                question = question,
                                uiState = uiState,
                                handleIntent = viewModel::handleIntent,
                            )
                            HorizontalDivider(
                                color = Color(0xFFEEEEEE),
                                thickness = 2.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}
