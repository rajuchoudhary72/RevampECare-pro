package com.app.ecarepro.feature.profile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareConfirmationBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.profile.profile.component.ProfileHeader
import com.app.ecarepro.feature.profile.profile.component.ProfileSectionCard
import com.app.ecarepro.feature.profile.profile.component.ProfileTileRow
import com.app.ecarepro.feature.profile.profile.component.SwitchAccountSheet

@Composable
fun MyProfileScreen(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToAddAccount: () -> Unit,
    onRestartApp: () -> Unit,
    viewModel: MyProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                MyProfileEvent.NavigateBack -> navigateBack()
                MyProfileEvent.NavigateToEdit -> navigateToEdit()
                MyProfileEvent.NavigateToLogin -> navigateToLogin()
                MyProfileEvent.NavigateToAddAccount -> navigateToAddAccount()
                MyProfileEvent.RestartApp -> onRestartApp()
            }
        }
    }

    MyProfileContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
    )

    if (uiState.showLogoutConfirmation) {
        EcareConfirmationBottomSheet(
            title = "Logout",
            description = "Are you sure you want to logout?",
            buttonText = "Logout",
            buttonColor = MaterialTheme.appColors.error,
            onDismiss = { viewModel.handleIntent(MyProfileIntent.DismissLogoutConfirmation) },
            onDeleteClick = { viewModel.handleIntent(MyProfileIntent.OnLogoutConfirmed) },
        )
    }

    if (uiState.showSwitchAccount) {
        SwitchAccountSheet(
            accounts = uiState.savedAccounts,
            onDismiss = { viewModel.handleIntent(MyProfileIntent.DismissSwitchAccount) },
            onSwitchAccount = { localId -> viewModel.handleIntent(MyProfileIntent.SwitchToAccount(localId)) },
            onDeleteAccount = { localId -> viewModel.handleIntent(MyProfileIntent.RemoveAccount(localId)) },
            onAddAccount = {
                viewModel.handleIntent(MyProfileIntent.DismissSwitchAccount)
                viewModel.handleIntent(MyProfileIntent.OnAddAccountClicked)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileContent(
    uiState: MyProfileUiState,
    handleIntent: (MyProfileIntent) -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "My Profile",
                onNavigationClicked = { handleIntent(MyProfileIntent.OnBackClicked) },
            )
        },
        containerColor = Color(0xFFF5F5F5),
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    EcareProEmptyState(message = uiState.error)
                }
            }

            uiState.profileData != null -> {
                val userType = uiState.profileData.userType
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        ProfileHeader(
                            profileData = uiState.profileData,
                            canEdit = uiState.canEditProfile,
                            onEditClicked = { handleIntent(MyProfileIntent.OnEditClicked) },
                            onManageAccountsClicked = { handleIntent(MyProfileIntent.ShowSwitchAccount) },
                        )
                    }

                    items(uiState.visibleSections) { section ->
                        val fields = uiState.sectionFields[section] ?: emptyList()
                        if (fields.isNotEmpty()) {
                            ProfileSectionCard(
                                title = section.titleFor(userType),
                                icon = section.icon(),
                                iconColor = section.iconColor(),
                                isExpanded = section in uiState.expandedSections,
                                fields = fields,
                                onToggle = { handleIntent(MyProfileIntent.ToggleSection(section)) },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Preferences",
                            style = MaterialTheme.appTypography.interSemiBold16px,
                            color = MaterialTheme.appColors.textSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }

                    item {
                        ProfileTileRow(
                            icon = Icons.Default.Notifications,
                            title = "Push notifications",
                            iconColor = Color(0xFF5C6BC0),
                            showChevron = true,
                            onClick = {},
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    item {
                        ProfileTileRow(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            title = "Logout",
                            iconColor = Color(0xFFEF5350),
                            onClick = { handleIntent(MyProfileIntent.OnLogoutClicked) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyProfileContent() {
    EcareProTheme {
        MyProfileContent(
            uiState = MyProfileUiState(
                isLoading = false,
                profileData = com.app.ecarepro.core.domain.model.profile.MyProfileData(
                    name = "Ms. Mohit Singh Pawar",
                    username = "sf001",
                    designation = "Manager",
                    roleName = "Management",
                    photo = null,
                    coverImg = null,
                    fName = "Mohit",
                    mName = "Singh",
                    lName = "Pawar",
                    gender = "Female",
                    dob = "16-Dec-1988",
                    doj = "25-Jul-2011",
                    maritalStatus = "Married",
                    doAnniversary = "11-Dec-2024",
                    fatherHusbandName = "Dr. Mahipal Singh",
                    fatherHusbandMob = "6356697608",
                    religion = "Hindu",
                    nationality = "Indian",
                    bloodGroup = "AB-",
                    mobile = "8800192019",
                    alternateMobile = "6356697608",
                    emergencyContactNo = "6392384645",
                    emailID = "email@example.com",
                    alternateEmailID = "alt@example.com",
                    address = "J-1002, Greater Noida",
                    permanentAddress = "",
                    qualification = "MCA",
                    aadhar = "753951644545",
                    pan = "AAACH2702H",
                    cbseId = "DF54GH6DF5H46",
                    uan = "ABCD12445",
                    nationalCode = "INDUP32536",
                    penNumber = "",
                    apaarId = "",
                    satNumber = "",
                    canEditProfile = true,
                    canChangeProfileImg = true,
                    canChangeCoverImg = true,
                    userType = 3,
                    studentProfile = null,
                ),
                canEditProfile = true,
                visibleSections = ProfileSectionType.entries.toList(),
                sectionFields = mapOf(
                    ProfileSectionType.PERSONAL_INFO to listOf(
                        ProfileFieldItem("First Name", "Mohit"),
                        ProfileFieldItem("Last Name", "Pawar"),
                    )
                ),
            ),
            handleIntent = {},
        )
    }
}
