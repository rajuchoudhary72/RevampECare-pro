package com.app.ecarepro.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = {},
    navigateToDashboard: (User) -> Unit = {},
    ) {
    val uiState: SplashUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is SplashEvent.NavigateToDashboard -> navigateToDashboard(event.user)
                SplashEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    SplashScreenContent(
        uiState = uiState
    )
}

@Composable
fun SplashScreenContent(
    uiState: SplashUiState,
) {

    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    val gradientColors = listOf(
        Color(0xFF66BB6A),
        Color(0xFF589B5B),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EcareProAsyncImage(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(286f / 92f)
                    .scale(scale.value),
                imageUrl = null,
                placeholder = painterResource(id = uiState.placeholder),
                error = painterResource(id = uiState.placeholder),
                contentDescription = "Franciscan e-care",
                colorFilter = ColorFilter.tint(White)
            )
        }

        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 23.dp)
                .size(height = 17.dp, width = 176.dp),
            painter = painterResource(id = R.drawable.tagline),
            contentDescription = "Franciscan e-care"
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    EcareProTheme {
        SplashScreenContent(
            uiState = SplashUiState()
        )
    }
}