package com.app.ecarepro.feature.schoolcode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.CodeInput
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.EcareProSnackbar
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.schoolcode.component.FindCodeLink
import com.app.ecarepro.feature.schoolcode.component.Footer
import com.app.ecarepro.feature.schoolcode.component.HeaderSection
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SchoolCodeScreen(
    viewModel: SchoolCodeViewModel = hiltViewModel(),
    navigateToNextScreen: (String) -> Unit,
    navigateToFindCodeScreen: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message.text)
            // Notify the ViewModel that the error has been shown
            viewModel.handleIntent(SchoolCodeIntent.OnErrorShown)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collectLatest { event ->
            when (event) {
                SchoolCodeEvent.NavigateToSearchSchoolScreen -> navigateToFindCodeScreen()
                is SchoolCodeEvent.NavigateToNextScreen -> navigateToNextScreen(event.schoolCode)
            }
        }
    }

    SchoolCodeScreenContent(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        handleIntent = { intent ->
            viewModel.handleIntent(intent)
        }
    )

}

@Composable
private fun SchoolCodeScreenContent(
    snackbarHostState: SnackbarHostState,
    uiState: SchoolCodeUiState,
    handleIntent: (SchoolCodeIntent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        EcareProBackground {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        uiState.errorMessage?.let {
                            EcareProSnackbar(it)
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    HeaderSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    CodeInput(
                        modifier = Modifier.fillMaxWidth(),
                        code = uiState.schoolCode,
                        textStyle = MaterialTheme.appTypography.interSemiBold14px.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        onOtpEntered = { code: String ->
                            handleIntent(SchoolCodeIntent.OnCodeChanged(code))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FindCodeLink(onFindCodeClicked = {
                        handleIntent(SchoolCodeIntent.OnFindCodeClicked)
                    })
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            handleIntent(SchoolCodeIntent.OnNextClicked)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = "Next",
                        enabled = uiState.isCodeEntered,
                        backgroundColor = MaterialTheme.appColors.accent
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Footer()
                }
            }
        }

        if (uiState.isLoading) {
            Loader()
        }
    }
}


@Preview
@Composable
fun SchoolCodeScreenContentPreview() {
    // Wrap in your theme for previews
    EcareProTheme {
        SchoolCodeScreenContent(
            snackbarHostState = remember { SnackbarHostState() },
            uiState = SchoolCodeUiState(),
            handleIntent = {}
        )
    }
}