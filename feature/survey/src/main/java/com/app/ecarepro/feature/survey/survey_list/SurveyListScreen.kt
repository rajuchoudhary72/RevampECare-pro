package com.app.ecarepro.feature.survey.survey_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.feature.survey.R
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.survey.survey_list.component.SurveyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyListScreen(
    navigateBack: () -> Unit,
    navigateToQuestions: (String) -> Unit,
    navigateToResult: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SurveyListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is SurveyListEvent.NavigateBack -> navigateBack()
                is SurveyListEvent.NavigateToQuestions -> navigateToQuestions(event.surveyId)
                is SurveyListEvent.NavigateToResult -> navigateToResult(event.surveyId)
                is SurveyListEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    EcareProScaffold(
        modifier = modifier,
        containerColor = Color(0xFFF5F5F5),
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.survey_title),
                onNavigationClicked = { viewModel.handleIntent(SurveyListIntent.OnBackClicked) },
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is SurveyListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            }

            is SurveyListUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    EcareProEmptyState(message = state.message)
                }
            }

            is SurveyListUiState.Success -> {
                if (state.surveys.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        EcareProEmptyState(message = stringResource(R.string.survey_no_surveys))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(items = state.surveys, key = { it.id }) { card ->
                            SurveyCard(
                                card = card,
                                onTap = { viewModel.handleIntent(SurveyListIntent.OnSurveyTapped(card)) },
                            )
                        }
                    }
                }
            }
        }
    }
}
