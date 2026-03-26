package com.app.ecarepro.feature.login.screens.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.R
import com.app.ecarepro.feature.login.component.Footer
import com.app.ecarepro.feature.login.dial
import com.app.ecarepro.feature.login.sendMail

@Composable
fun HelpScreen(
    viewModel: HelpViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                HelpEvent.NavigateBack -> navigateToBack()
                is HelpEvent.OpenMailApp -> context.sendMail(event.email)
                is HelpEvent.OpenPhoneDialer -> context.dial(event.phoneNumber)
            }
        }
    }

    HelpScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreenContent(
    uiState: HelpUiState,
    handleIntent: (HelpIntent) -> Unit,
) {
    EcareProScaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(
                        elevation = 2.dp
                    ),
                title = {
                    Text(
                        text = stringResource(R.string.feature_login_help),
                        style = MaterialTheme.appTypography.interMedium16px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleIntent(HelpIntent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        isLoading = uiState.isLoading,
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            uiState.schoolDetails?.let { school ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EcareProAsyncImage(
                        imageUrl = school.logo,
                        contentDescription = "School Logo",
                        modifier = Modifier.size(48.dp)
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = school.schoolName.orEmpty(),
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = MaterialTheme.appColors.textPrimary
                        )
                        Text(
                            text = school.schAdd1.orEmpty(),
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                ContactRow(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_headphone),
                            contentDescription = "Contact",
                        )
                    },
                    label = stringResource(R.string.feature_login_contact_number),
                    value = school.supportPhone.orEmpty(),
                    onClick = { handleIntent(HelpIntent.OnContactNumberClicked(school.supportPhone.orEmpty())) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.appColors.divider)
                Spacer(modifier = Modifier.height(24.dp))
                ContactRow(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_email_outline),
                            contentDescription = "Email",
                        )
                    },
                    label = stringResource(R.string.feature_login_email_id),
                    value = school.supportEmail.orEmpty(),
                    onClick = { handleIntent(HelpIntent.OnEmailClicked(school.supportEmail.orEmpty())) }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Footer(color = MaterialTheme.appColors.textSecondary)
        }
    }
}

@Composable
private fun ContactRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Column(
    ) {
        icon()
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
                color = MaterialTheme.appColors.textPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = value,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.info,
                modifier = Modifier.clickable { onClick() },
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Preview()
@Composable
fun HelpScreenPreview() {
    EcareProTheme {
        HelpScreenContent(
            uiState = HelpUiState(
                schoolDetails = SchoolDetail(
                    schoolName = "Franciscan Public School",
                    schAdd1 = "Gaur city 2, Greater Noida",
                    schAdd2 = "Gaur city 2, Greater Noida",
                    logo = "",
                    supportPhone = "+91 9524624569",
                    supportEmail = "contact@littleschlars-kashipur.com",
                    schoolCode = "",
                    active = null,
                    assessmentMarksURL = null,
                    city = null,
                    contactEmail = null,
                    eCareProSch = null,
                    feePaymentURL = null,
                    feeReportURL = null,
                    isBoardingSchool = null,
                    isStudentLoginBlocked = null,
                    logoNScName = null,
                    logoScName = null,
                    marksEntryURL = null,
                    schUpdatedOn = null,
                    state = null,
                    supportDays = null,
                    supportHours = null,
                    webSite = null,
                    themColor = null,
                )
            ),
            handleIntent = {}
        )
    }
}