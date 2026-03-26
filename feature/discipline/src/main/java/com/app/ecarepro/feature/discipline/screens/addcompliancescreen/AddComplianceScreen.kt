package com.app.ecarepro.feature.discipline.screens.addcompliancescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.model.discipline.InfractionRecord
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProFileAttachment
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.UploadOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.discipline.components.InputField
import com.app.ecarepro.feature.discipline.components.UserHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddComplianceScreen(
    viewModel: AddComplianceViewModel,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AddComplianceEvent.NavigateBack -> navigateToBack()
                is AddComplianceEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Compliance",
                onNavigationClicked = { viewModel.handleIntent(AddComplianceIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        isLoading = (uiState as? UiState.Success)?.data?.isLoading == true,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                data.userInfo?.let { UserHeaderCard(userInfo = it) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    data.infraction?.let { record ->
                        // Read-only infraction details
                        InfoRow(label = "Category", value = record.infraction.orEmpty())
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Subcategory", value = record.subInfraction.orEmpty())
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                InfoRow(label = "Instance", value = record.instance.orEmpty())
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                InfoRow(label = "Consequences", value = record.consequences.orEmpty())
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Corrective Action", value = record.correctiveAction.orEmpty())
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Infraction On", value = record.infractionOn.orEmpty())
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Only show editable fields if compliance active and not resolved
                    val showActions = data.infraction?.isComplianceActive == true &&
                            data.infraction.isResolved != true

                    if (showActions) {
                        // Remarks
                        InputField(
                            label = "Remarks",
                            value = data.complianceText,
                            placeholder = "Add compliance remarks",
                            onValueChange = { viewModel.handleIntent(AddComplianceIntent.OnRemarksChanged(it)) },
                            maxLines = 4,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // File attachment
                        Text(
                            text = "Add consequence attachment",
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        EcareProFileAttachment(
                            selectedFile = data.selectedFile?.let { listOf(it) } ?: emptyList(),
                            onClickPickFile = { viewModel.handleIntent(AddComplianceIntent.OnAddFileClicked) },
                            onClickDeleteFile = { viewModel.handleIntent(AddComplianceIntent.OnDeleteFile) },
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.handleIntent(AddComplianceIntent.OnSaveCompliance) },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Save compliance")
                            }

                            Button(
                                modifier = Modifier.weight(1f),
                                title = "Resolve",
                                onClick = { viewModel.handleIntent(AddComplianceIntent.OnResolve) },
                            )
                        }
                    }
                }

                // File upload sheet
                EcareProFileUploadBottomSheet(
                    isVisible = data.isFileUploadSheetVisible,
                    onDismiss = { viewModel.handleIntent(AddComplianceIntent.OnDismissFileUploadSheet) },
                    allowedOptions = listOf(UploadOption.CAMERA, UploadOption.GALLERY, UploadOption.DOCUMENT),
                    onShowError = { viewModel.handleIntent(AddComplianceIntent.OnShowError(it)) },
                    onFilesSelected = { files ->
                        files.firstOrNull()?.let {
                            viewModel.handleIntent(AddComplianceIntent.OnFileSelected(it))
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddComplianceContent(
    data: AddComplianceUiState,
    onIntent: (AddComplianceIntent) -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Compliance",
                onNavigationClicked = { onIntent(AddComplianceIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = remember { SnackbarHostState() },
        isLoading = data.isLoading,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            data.userInfo?.let { UserHeaderCard(userInfo = it) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                data.infraction?.let { record ->
                    InfoRow(label = "Category", value = record.infraction.orEmpty())
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Subcategory", value = record.subInfraction.orEmpty())
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow(label = "Instance", value = record.instance.orEmpty())
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            InfoRow(label = "Consequences", value = record.consequences.orEmpty())
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Corrective Action", value = record.correctiveAction.orEmpty())
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Infraction On", value = record.infractionOn.orEmpty())
                }

                Spacer(modifier = Modifier.height(20.dp))

                val showActions = data.infraction?.isComplianceActive == true &&
                        data.infraction.isResolved != true

                if (showActions) {
                    InputField(
                        label = "Remarks",
                        value = data.complianceText,
                        placeholder = "Add compliance remarks",
                        onValueChange = { onIntent(AddComplianceIntent.OnRemarksChanged(it)) },
                        maxLines = 4,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Add consequence attachment",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    EcareProFileAttachment(
                        selectedFile = data.selectedFile?.let { listOf(it) } ?: emptyList(),
                        onClickPickFile = { onIntent(AddComplianceIntent.OnAddFileClicked) },
                        onClickDeleteFile = { onIntent(AddComplianceIntent.OnDeleteFile) },
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { onIntent(AddComplianceIntent.OnSaveCompliance) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Save compliance")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            title = "Resolve",
                            onClick = { onIntent(AddComplianceIntent.OnResolve) },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Compliance Screen")
@Composable
private fun AddComplianceScreenPreview() {
    EcareProTheme {
        AddComplianceContent(
            data = AddComplianceUiState(
                userInfo = DisciplineUserInfo(
                    id = 1, name = "John Doe", className = "10-A",
                    admissionNo = "ADM001", photo = null, contactPerson = null,
                    contactMob = null, designation = null, mobile = null, qualification = null,
                ),
                infraction = InfractionRecord(
                    infractionID = "1", infraction = "Bullying", subInfraction = "Verbal",
                    consequences = "Warning", designation = null, stffPhoto = null,
                    admissionNo = "ADM001", correctiveAction = "Counseling", stID = 1,
                    studentName = "John Doe", point = 5, instance = "2",
                    infractionOn = "15-Jan-2026", staffName = "Mr. Smith", issueBy = "Admin",
                    recordClass = "10-A", photo = null, isResolved = false,
                    canDelete = true, showResolvedButton = true, isComplianceActive = true,
                    complianceAttachment = null, contactMob = null, remarks = null,
                ),
                complianceText = "Student has shown improvement",
            ),
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}
