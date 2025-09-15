package com.app.ecarepro.onboarding.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.onboarding.R
import com.app.ecarepro.onboarding.feature.component.OnboardingButtons
import com.app.ecarepro.onboarding.feature.component.OnboardingPagerItem
import com.app.ecarepro.onboarding.feature.component.PagerIndicator
import com.app.ecarepro.onboarding.feature.model.OnboardingPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_img1, // Replace with your actual drawable
            title = "Step Into the Future of Schooling",
            description = "All your academics, activities, and communication — beautifully brought together in one app."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_img1, // Replace with your actual drawable
            title = "Always Connected to Your School",
            description = "From big announcements to small notices, Franciscan keeps you in sync with every moment that matters."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_img1, // Replace with your actual drawable
            title = "Every Role, Made Smarter",
            description = "Teachers, principals, staff, and parents – Franciscan adapts to what matters most for you."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_img1, // Replace with your actual drawable
            title = "30+ Modules. One Seamless Experience",
            description = "Attendance, reports, timetable, assignments, communication, and more - all just a swipe away."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {


                AnimatedVisibility(
                    visible = pagerState.currentPage != 0,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
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
                }


                if (pagerState.currentPage != 0) {
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
                OnboardingPagerItem(page = pages[pageIndex])
            }


            OnboardingButtons(
                modifier = Modifier.fillMaxWidth(),
                isLastPage = pagerState.currentPage == pages.size - 1,
                onSkipClicked = {
                    onOnboardingFinished()
                },
                onNextClicked = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onAddSchoolClicked = {
                    onOnboardingFinished()
                }
            )
        }
    }
}


@Preview()
@Composable
fun OnboardingScreenPreview() {
    EcareProTheme {
        OnboardingScreen(onOnboardingFinished = {})
    }
}