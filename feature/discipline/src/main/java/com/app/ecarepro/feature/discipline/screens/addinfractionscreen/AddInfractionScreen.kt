package com.app.ecarepro.feature.discipline.screens.addinfractionscreen

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProFileAttachment
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.UploadOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.discipline.components.DropdownField
import com.app.ecarepro.feature.discipline.components.InputField
import com.app.ecarepro.feature.discipline.components.UserHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInfractionScreen(
    viewModel: AddInfractionViewModel,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AddInfractionEvent.NavigateBack -> navigateToBack()
                is AddInfractionEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Add Infraction",
                onNavigationClicked = { viewModel.handleIntent(AddInfractionIntent.OnBackClicked) },
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
                // User Header
                data.userInfo?.let { info ->
                    UserHeaderCard(userInfo = info)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    // Category
                    DropdownField(
                        label = "Select infraction category",
                        value = data.selectedCategory?.name,
                        placeholder = "Select category",
                        onClick = { viewModel.handleIntent(AddInfractionIntent.OnCategorySelectClicked) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subcategory
                    DropdownField(
                        label = "Select infraction subcategory",
                        value = data.selectedSubcategory?.name,
                        placeholder = "Select subcategory",
                        onClick = { viewModel.handleIntent(AddInfractionIntent.OnSubcategorySelectClicked) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instance count + Consequences row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            InputField(
                                label = "Infraction count",
                                value = data.instanceCount,
                                placeholder = "No of instance",
                                onValueChange = {},
                                enabled = false,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            DropdownField(
                                label = "Consequences",
                                value = data.selectedConsequence?.name,
                                placeholder = "Select",
                                onClick = { viewModel.handleIntent(AddInfractionIntent.OnConsequenceSelectClicked) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // File attachment
                    Text(
                        text = "Add consequence attachment",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Text(
                        text = "Max 10 MB files are allowed",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    EcareProFileAttachment(
                        selectedFile = data.selectedFile?.let { listOf(it) } ?: emptyList(),
                        onClickPickFile = { viewModel.handleIntent(AddInfractionIntent.OnAddFileClicked) },
                        onClickDeleteFile = { viewModel.handleIntent(AddInfractionIntent.OnDeleteFile) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remarks
                    InputField(
                        label = "Add remarks",
                        value = data.remarks,
                        placeholder = "Reason for infraction",
                        onValueChange = { viewModel.handleIntent(AddInfractionIntent.OnRemarksChanged(it)) },
                        maxLines = 4,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Compliance checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.handleIntent(AddInfractionIntent.OnComplianceToggled(!data.isComplianceActive))
                        },
                    ) {
                        Checkbox(
                            checked = data.isComplianceActive,
                            onCheckedChange = {
                                viewModel.handleIntent(AddInfractionIntent.OnComplianceToggled(it))
                            },
                        )
                        Text(
                            text = "Activate compliance",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.handleIntent(AddInfractionIntent.OnSaveClicked(1)) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Save")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            title = "Save & Notify",
                            onClick = { viewModel.handleIntent(AddInfractionIntent.OnSaveClicked(2)) },
                        )
                    }
                }

                // File upload sheet
                EcareProFileUploadBottomSheet(
                    isVisible = data.isFileUploadSheetVisible,
                    onDismiss = { viewModel.handleIntent(AddInfractionIntent.OnDismissFileUploadSheet) },
                    allowedOptions = listOf(UploadOption.CAMERA, UploadOption.GALLERY, UploadOption.DOCUMENT),
                    onShowError = { viewModel.handleIntent(AddInfractionIntent.OnShowError(it)) },
                    onFilesSelected = { files ->
                        files.firstOrNull()?.let {
                            viewModel.handleIntent(AddInfractionIntent.OnFileSelected(it))
                        }
                    },
                )
            }

            // Selection sheets
            EcareProSelectionBottomSheet(
                title = "Select infraction category",
                isVisible = data.isCategorySheetVisible,
                onDismiss = { viewModel.handleIntent(AddInfractionIntent.OnDismissCategorySheet) },
                options = data.categories.map { it.name },
                selectedOptions = listOfNotNull(data.selectedCategory?.name),
                onOptionsSelected = {
                    it.firstOrNull()?.let { name ->
                        viewModel.handleIntent(AddInfractionIntent.OnCategorySelected(name))
                    }
                },
            )

            EcareProSelectionBottomSheet(
                title = "Select infraction subcategory",
                isVisible = data.isSubcategorySheetVisible,
                onDismiss = { viewModel.handleIntent(AddInfractionIntent.OnDismissSubcategorySheet) },
                options = data.subcategories.map { it.name },
                selectedOptions = listOfNotNull(data.selectedSubcategory?.name),
                onOptionsSelected = {
                    it.firstOrNull()?.let { name ->
                        viewModel.handleIntent(AddInfractionIntent.OnSubcategorySelected(name))
                    }
                },
            )

            EcareProSelectionBottomSheet(
                title = "Select consequences",
                isVisible = data.isConsequenceSheetVisible,
                onDismiss = { viewModel.handleIntent(AddInfractionIntent.OnDismissConsequenceSheet) },
                options = data.consequences.map { it.name },
                selectedOptions = listOfNotNull(data.selectedConsequence?.name),
                onOptionsSelected = {
                    it.firstOrNull()?.let { name ->
                        viewModel.handleIntent(AddInfractionIntent.OnConsequenceSelected(name))
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddInfractionContent(
    data: AddInfractionUiState,
    onIntent: (AddInfractionIntent) -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Add Infraction",
                onNavigationClicked = { onIntent(AddInfractionIntent.OnBackClicked) },
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
            data.userInfo?.let { info ->
                UserHeaderCard(userInfo = info)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                DropdownField(
                    label = "Select infraction category",
                    value = data.selectedCategory?.name,
                    placeholder = "Select category",
                    onClick = { onIntent(AddInfractionIntent.OnCategorySelectClicked) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownField(
                    label = "Select infraction subcategory",
                    value = data.selectedSubcategory?.name,
                    placeholder = "Select subcategory",
                    onClick = { onIntent(AddInfractionIntent.OnSubcategorySelectClicked) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        InputField(
                            label = "Infraction count",
                            value = data.instanceCount,
                            placeholder = "No of instance",
                            onValueChange = {},
                            enabled = false,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label = "Consequences",
                            value = data.selectedConsequence?.name,
                            placeholder = "Select",
                            onClick = { onIntent(AddInfractionIntent.OnConsequenceSelectClicked) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Add consequence attachment",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = "Max 10 MB files are allowed",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                EcareProFileAttachment(
                    selectedFile = data.selectedFile?.let { listOf(it) } ?: emptyList(),
                    onClickPickFile = { onIntent(AddInfractionIntent.OnAddFileClicked) },
                    onClickDeleteFile = { onIntent(AddInfractionIntent.OnDeleteFile) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    label = "Add remarks",
                    value = data.remarks,
                    placeholder = "Reason for infraction",
                    onValueChange = { onIntent(AddInfractionIntent.OnRemarksChanged(it)) },
                    maxLines = 4,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onIntent(AddInfractionIntent.OnComplianceToggled(!data.isComplianceActive))
                    },
                ) {
                    Checkbox(
                        checked = data.isComplianceActive,
                        onCheckedChange = {
                            onIntent(AddInfractionIntent.OnComplianceToggled(it))
                        },
                    )
                    Text(
                        text = "Activate compliance",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { onIntent(AddInfractionIntent.OnSaveClicked(1)) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Save")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        title = "Save & Notify",
                        onClick = { onIntent(AddInfractionIntent.OnSaveClicked(2)) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Infraction Screen")
@Composable
private fun AddInfractionScreenPreview() {
    EcareProTheme {
        AddInfractionContent(
            data = AddInfractionUiState(
                userInfo = DisciplineUserInfo(
                    id = 1, name = "John Doe", className = "10-A",
                    admissionNo = "ADM001", photo = null, contactPerson = null,
                    contactMob = null, designation = null, mobile = null, qualification = null,
                ),
                instanceCount = "2",
                remarks = "Repeated offense",
                isComplianceActive = true,
            ),
        )
    }
}
