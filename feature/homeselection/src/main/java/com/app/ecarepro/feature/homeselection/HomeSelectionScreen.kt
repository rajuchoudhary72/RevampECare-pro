package com.app.ecarepro.feature.homeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.homeselection.component.HeaderSection
import com.app.ecarepro.feature.homeselection.component.HomeSelectionCard

@Composable
fun HomeSelectionScreen(
    viewModel: HomeSelectionViewModel = hiltViewModel(),
    onComplete: (HomeScreenType) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is HomeSelectionEvent.OnCompletion -> onComplete(event.homeScreenType)
            }
        }
    }

    HomeSelectionContent(
        uiState = uiState,
        handleIntent = { intent -> viewModel.handleIntent(intent) }
    )
}

@Composable
private fun HomeSelectionContent(
    uiState: HomeSelectionUiState,
    handleIntent: (HomeSelectionIntent) -> Unit,
) {
    EcareProBackground(
        modifier = Modifier.fillMaxSize(),
        overlayColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    HeaderSection(uiState.schoolDetails)
                }

                items(uiState.options) { option ->
                    HomeSelectionCard(
                        option = option,
                        isSelected = uiState.selectedHomeScreenType == option.type,
                        onClick = {
                            handleIntent(HomeSelectionIntent.OnOptionSelected(option.type))
                        }
                    )
                }
            }

            Text(
                text = stringResource(R.string.feature_homeselection_you_can_switch_anytime_later_from_settings),
                style = MaterialTheme.appTypography.interRegular12px
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { handleIntent(HomeSelectionIntent.OnSetAsHomeClicked) },
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.feature_homeselection_set_as_home)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeSelectionScreenPreview() {

    EcareProTheme() {
        HomeSelectionContent(
            uiState = HomeSelectionUiState(selectedHomeScreenType = HomeScreenType.DASHBOARD),
            handleIntent = {}
        )
    }

}

