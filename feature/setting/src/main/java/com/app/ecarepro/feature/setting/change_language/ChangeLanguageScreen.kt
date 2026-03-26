package com.app.ecarepro.feature.setting.change_language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.setting.R

@Composable
fun ChangeLanguageScreen(
    navigateBack: () -> Unit,
    onRestartActivity: () -> Unit = {},
    viewModel: ChangeLanguageViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ChangeLanguageEvent.NavigateBack -> navigateBack()
                is ChangeLanguageEvent.RestartApp -> onRestartActivity()
            }
        }
    }

    ChangeLanguageContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeLanguageContent(
    uiState: ChangeLanguageUiState,
    handleIntent: (ChangeLanguageIntent) -> Unit,
    navigateBack: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.settings_change_language),
                onNavigationClicked = navigateBack,
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            uiState.languages.forEachIndexed { index, language ->
                val isSelected = language.code == uiState.selectedLanguageCode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { handleIntent(ChangeLanguageIntent.OnLanguageSelected(language.code)) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = language.name,
                        style = if (isSelected) MaterialTheme.appTypography.interSemiBold14px
                                else MaterialTheme.appTypography.interRegular14px,
                        color = if (isSelected) MaterialTheme.appColors.primary
                                else MaterialTheme.appColors.textPrimary,
                    )
                    Icon(
                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked
                                      else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isSelected) MaterialTheme.appColors.primary
                               else MaterialTheme.appColors.textSecondary,
                    )
                }
                if (index < uiState.languages.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFEEEEEE),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { handleIntent(ChangeLanguageIntent.OnConfirm) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.appColors.primary),
            ) {
                Text(
                    text = stringResource(R.string.settings_confirm_update),
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = Color.White,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Change Language - English selected")
@Composable
private fun PreviewChangeLanguageEnglish() {
    EcareProTheme {
        ChangeLanguageContent(
            uiState = ChangeLanguageUiState(selectedLanguageCode = "en"),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Change Language - Hindi selected")
@Composable
private fun PreviewChangeLanguageHindi() {
    EcareProTheme {
        ChangeLanguageContent(
            uiState = ChangeLanguageUiState(selectedLanguageCode = "hi"),
            handleIntent = {},
        )
    }
}
