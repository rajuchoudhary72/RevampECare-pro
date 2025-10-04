package com.app.ecarepro.onboarding.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.ui.StateHandler
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.onboarding.R
import com.app.ecarepro.onboarding.feature.component.OnboardingButtons
import com.app.ecarepro.onboarding.feature.component.OnboardingPagerItem
import com.app.ecarepro.onboarding.feature.component.PagerIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingView(
    navigateToAddSchool: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {

    val uiState: UiState<List<OnboardingItem>> by viewModel.uiState.collectAsState()

    StateHandler(
        state = uiState,
        onRetry = { viewModel.fetchOnboardingItems() }
    ) {
        OnboardingScreenContent(
            pages = it,
            navigateToAddSchool = navigateToAddSchool
        )
    }
}

@Composable
fun OnboardingScreenContent(
    pages: List<OnboardingItem>,
    navigateToAddSchool: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                if (pagerState.currentPage != 0) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage.minus(
                                        1
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = White
                        )
                    }

                    Spacer(Modifier.weight(1f))
                }
                PagerIndicator(
                    count = pages.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        },
    ) { innerPadding ->
        Image(
            painter = painterResource(R.drawable.background_oval),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                OnboardingPagerItem(
                    page = pages[pageIndex],
                    modifier = Modifier.fillMaxWidth()
                )
            }


            OnboardingButtons(
                modifier = Modifier.fillMaxWidth(),
                isLastPage = pagerState.currentPage == pages.size - 1,
                onSkipClicked = {
                    navigateToAddSchool()
                },
                onNextClicked = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onAddSchoolClicked = {
                    navigateToAddSchool()
                }
            )
        }
    }
}


@Preview
@Composable
fun OnboardingScreenPreview() {
    EcareProTheme {
        OnboardingView(navigateToAddSchool = {})
    }
}