package com.app.ecarepro.feature.survey.survey_result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyResultScreen(
    viewModel: SurveyResultViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is SurveyResultEvent.NavigateBack -> navigateBack()
            }
        }
    }

    EcareProScaffold(
        modifier = modifier,
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.survey_result_title),
                onNavigationClicked = { viewModel.handleIntent(SurveyResultIntent.OnBackClicked) },
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is SurveyResultUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            }

            is SurveyResultUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    EcareProEmptyState(message = state.message)
                }
            }

            is SurveyResultUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    items(items = state.results, key = { it.queID }) { presentation ->
                        ResultQuestionCard(presentation = presentation)
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

@Composable
private fun ResultQuestionCard(
    presentation: SurveyResultPresentation,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = presentation.questionText,
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.primary,
        )
        Text(
            text = "${stringResource(R.string.survey_total_responses)} ${presentation.totalResponses}",
            style = MaterialTheme.appTypography.interRegular13px,
            color = MaterialTheme.appColors.textSecondary,
        )
        presentation.options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = option.optionText,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = "- ${option.responseCount} (${option.percentage}%)",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }
    }
}
