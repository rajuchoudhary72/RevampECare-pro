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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.CodeInput
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.schoolcode.component.Background
import com.app.ecarepro.feature.schoolcode.component.FindCodeLink
import com.app.ecarepro.feature.schoolcode.component.Footer
import com.app.ecarepro.feature.schoolcode.component.HeaderSection
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SchoolCodeScreen(
    viewModel: SchoolCodeViewModel = viewModel(),
    navigateToNextScreen: () -> Unit,
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
                SchoolCodeEvent.NavigateToNextScreen -> navigateToNextScreen()
                SchoolCodeEvent.NavigateToSearchSchoolScreen -> navigateToFindCodeScreen()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Background {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState){
                    uiState.errorMessage?.let {
                        AppSnackbar(it)
                    }
                } }
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
                        textStyle = MaterialTheme.appTypography.interSemiBold14px.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        onOtpEntered = { code: String ->
                            viewModel.handleIntent(SchoolCodeIntent.OnCodeChanged(code))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FindCodeLink(onFindCodeClicked = {
                        viewModel.handleIntent(SchoolCodeIntent.OnFindCodeClicked)
                    })
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.handleIntent(SchoolCodeIntent.OnNextClicked)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = "Next",
                        enabled = uiState.isCodeEntered
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
fun SchoolCodeScreenPreview_Default() {
    // Wrap in your theme for previews
    EcareProTheme {
        SchoolCodeScreen(navigateToNextScreen = {}, navigateToFindCodeScreen = {})
    }
}