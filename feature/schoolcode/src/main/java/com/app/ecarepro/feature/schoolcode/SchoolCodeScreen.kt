package com.app.ecarepro.feature.schoolcode

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.feature.schoolcode.component.Background
import com.app.ecarepro.feature.schoolcode.component.FindCodeLink
import com.app.ecarepro.feature.schoolcode.component.Footer
import com.app.ecarepro.feature.schoolcode.component.HeaderSection

val OrangeErrorBackground = Color(0xFFFFDAB9) // Example Orange
val LinkColor = Color(0xFF0066CC) // Example Link Blue
// --- End Mock Colors ---


@Composable
fun SchoolCodeScreen(
    viewModel: SchoolCodeViewModel = viewModel(),
    navigateToNextScreen: (schoolCode: String) -> Unit,
    navigateToFindCodeScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    Background {
        Scaffold(
            containerColor = Color.Transparent,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()) // Make content scrollable
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                HeaderSection()

                Spacer(modifier = Modifier.height(16.dp))

              /*  CodeInputSection(
                    codeDigits = uiState.codeDigits, onDigitChanged = { index, digit ->
                        viewModel.onIntent(SchoolCodeIntent.OnDigitChanged(index, digit))
                    }, isError = uiState.errorMessage != null
                )*/
                Spacer(modifier = Modifier.height(16.dp))

                FindCodeLink(onFindCodeClicked = {
                    viewModel.onIntent(SchoolCodeIntent.OnFindCodeClicked)
                    navigateToFindCodeScreen()
                })
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    title = "Next"
                )

                Spacer(modifier = Modifier.weight(1f)) // Push footer to bottom
                Footer()
            }
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