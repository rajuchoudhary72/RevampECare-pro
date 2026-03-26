package com.app.ecarepro.feature.profile.edit_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun EditProfileScreen(
    navigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                EditProfileEvent.NavigateBack -> navigateBack()
                is EditProfileEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    EditProfileContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    handleIntent: (EditProfileIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Edit profile",
                onNavigationClicked = { handleIntent(EditProfileIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = Color.White,
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
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (uiState.userType) {
                        2 -> {
                            // Parent: only contact fields
                            item {
                                SectionLabel("Contact info")
                            }
                            item {
                                EcareProInputField(
                                    label = "Mobile",
                                    value = uiState.mobile,
                                    placeholder = "Enter mobile number",
                                    onValueChange = { handleIntent(EditProfileIntent.OnMobileChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Email ID",
                                    value = uiState.email,
                                    placeholder = "Enter email",
                                    onValueChange = { handleIntent(EditProfileIntent.OnEmailChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Address",
                                    value = uiState.address,
                                    placeholder = "Enter address",
                                    onValueChange = { handleIntent(EditProfileIntent.OnAddressChanged(it)) },
                                )
                            }
                        }

                        else -> {
                            // Staff: full form
                            item { SectionLabel("Personal info") }

                            item {
                                EcareProInputField(
                                    label = "First Name",
                                    value = uiState.fName,
                                    placeholder = "Enter first name",
                                    onValueChange = { handleIntent(EditProfileIntent.OnFirstNameChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Middle Name",
                                    value = uiState.mName,
                                    placeholder = "Enter middle name",
                                    onValueChange = { handleIntent(EditProfileIntent.OnMiddleNameChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Last Name",
                                    value = uiState.lName,
                                    placeholder = "Enter last name",
                                    onValueChange = { handleIntent(EditProfileIntent.OnLastNameChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Date of Birth",
                                    value = uiState.dob,
                                    placeholder = "DD-MMM-YYYY",
                                    onValueChange = { handleIntent(EditProfileIntent.OnDobChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Date of Anniversary",
                                    value = uiState.doAnniversary,
                                    placeholder = "DD-MMM-YYYY",
                                    onValueChange = { handleIntent(EditProfileIntent.OnAnniversaryChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Father / Husband Name",
                                    value = uiState.fatherHusbandName,
                                    placeholder = "Enter name",
                                    onValueChange = { handleIntent(EditProfileIntent.OnFatherHusbandNameChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Marital Status",
                                    value = uiState.maritalStatus,
                                    placeholder = "Enter marital status",
                                    onValueChange = { handleIntent(EditProfileIntent.OnMaritalStatusChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Blood Group",
                                    value = uiState.bloodGroup,
                                    placeholder = "Enter blood group",
                                    onValueChange = { handleIntent(EditProfileIntent.OnBloodGroupChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Religion",
                                    value = uiState.religion,
                                    placeholder = "Enter religion",
                                    onValueChange = { handleIntent(EditProfileIntent.OnReligionChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Nationality",
                                    value = uiState.nationality,
                                    placeholder = "Enter nationality",
                                    onValueChange = { handleIntent(EditProfileIntent.OnNationalityChanged(it)) },
                                )
                            }

                            item { SectionLabel("Contact info") }

                            item {
                                EcareProInputField(
                                    label = "Mobile",
                                    value = uiState.mobile,
                                    placeholder = "Enter mobile number",
                                    onValueChange = { handleIntent(EditProfileIntent.OnMobileChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Father / Spouse Contact",
                                    value = uiState.fatherHusbandMob,
                                    placeholder = "Enter contact number",
                                    onValueChange = { handleIntent(EditProfileIntent.OnFatherHusbandMobChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Alternate Mobile",
                                    value = uiState.alternateMobile,
                                    placeholder = "Enter alternate mobile",
                                    onValueChange = { handleIntent(EditProfileIntent.OnAlternateMobileChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Emergency Contact",
                                    value = uiState.emergencyContact,
                                    placeholder = "Enter emergency contact",
                                    onValueChange = { handleIntent(EditProfileIntent.OnEmergencyContactChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Email ID",
                                    value = uiState.email,
                                    placeholder = "Enter email",
                                    onValueChange = { handleIntent(EditProfileIntent.OnEmailChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Alternate Email ID",
                                    value = uiState.alternateEmail,
                                    placeholder = "Enter alternate email",
                                    onValueChange = { handleIntent(EditProfileIntent.OnAlternateEmailChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Address",
                                    value = uiState.address,
                                    placeholder = "Enter address",
                                    onValueChange = { handleIntent(EditProfileIntent.OnAddressChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "Permanent Address",
                                    value = uiState.permanentAddress,
                                    placeholder = "Enter permanent address",
                                    onValueChange = { handleIntent(EditProfileIntent.OnPermanentAddressChanged(it)) },
                                )
                            }

                            item { SectionLabel("IDs") }

                            item {
                                EcareProInputField(
                                    label = "Aadhaar Card Number",
                                    value = uiState.aadhaar,
                                    placeholder = "Enter aadhaar number",
                                    onValueChange = { handleIntent(EditProfileIntent.OnAadhaarChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "PAN",
                                    value = uiState.pan,
                                    placeholder = "Enter PAN",
                                    onValueChange = { handleIntent(EditProfileIntent.OnPanChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "CBSE ID",
                                    value = uiState.cbseId,
                                    placeholder = "Enter CBSE ID",
                                    onValueChange = { handleIntent(EditProfileIntent.OnCbseIdChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "UAN",
                                    value = uiState.uan,
                                    placeholder = "Enter UAN",
                                    onValueChange = { handleIntent(EditProfileIntent.OnUanChanged(it)) },
                                )
                            }
                            item {
                                EcareProInputField(
                                    label = "National Teacher ID",
                                    value = uiState.nationalCode,
                                    placeholder = "Enter national teacher ID",
                                    onValueChange = { handleIntent(EditProfileIntent.OnNationalCodeChanged(it)) },
                                )
                            }

                            item { SectionLabel("Educational qualifications") }

                            item {
                                EcareProInputField(
                                    label = "Qualification",
                                    value = uiState.qualification,
                                    placeholder = "Enter qualification",
                                    onValueChange = { handleIntent(EditProfileIntent.OnQualificationChanged(it)) },
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { handleIntent(EditProfileIntent.SaveChanges) },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.appColors.primary,
                            ),
                        ) {
                            Text(
                                text = "Save changes",
                                style = MaterialTheme.appTypography.interSemiBold14px,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEditProfileStaff() {
    EcareProTheme {
        EditProfileContent(
            uiState = EditProfileUiState(
                isLoading = false,
                userType = 3,
                fName = "Mohit",
                mName = "Singh",
                lName = "Pawar",
                mobile = "8800192019",
                email = "email@example.com",
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEditProfileParent() {
    EcareProTheme {
        EditProfileContent(
            uiState = EditProfileUiState(
                isLoading = false,
                userType = 2,
                mobile = "9876543210",
                email = "parent@example.com",
                address = "123 Main St",
            ),
            handleIntent = {},
        )
    }
}
