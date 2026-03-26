package com.app.ecarepro.feature.discipline.screens.addappreciationscreen

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
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.discipline.components.DropdownField
import com.app.ecarepro.feature.discipline.components.InputField
import com.app.ecarepro.feature.discipline.components.UserHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppreciationScreen(
    viewModel: AddAppreciationViewModel,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AddAppreciationEvent.NavigateBack -> navigateToBack()
                is AddAppreciationEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Add Appreciation",
                onNavigationClicked = { viewModel.handleIntent(AddAppreciationIntent.OnBackClicked) },
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
                        label = "Select appreciation category",
                        value = data.selectedCategory?.name,
                        placeholder = "Select category",
                        onClick = { viewModel.handleIntent(AddAppreciationIntent.OnCategorySelectClicked) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subcategory
                    DropdownField(
                        label = "Select appreciation subcategory",
                        value = data.selectedSubcategory?.name,
                        placeholder = "Select subcategory",
                        onClick = { viewModel.handleIntent(AddAppreciationIntent.OnSubcategorySelectClicked) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instance count + Reward row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            InputField(
                                label = "Appreciation count",
                                value = data.instanceCount,
                                placeholder = "No of instance",
                                onValueChange = {},
                                enabled = false,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            DropdownField(
                                label = "Reward",
                                value = data.selectedReward?.name,
                                placeholder = "Select",
                                onClick = { viewModel.handleIntent(AddAppreciationIntent.OnRewardSelectClicked) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remarks
                    InputField(
                        label = "Add remarks",
                        value = data.remarks,
                        placeholder = "Reason for appreciation",
                        onValueChange = { viewModel.handleIntent(AddAppreciationIntent.OnRemarksChanged(it)) },
                        maxLines = 4,
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.handleIntent(AddAppreciationIntent.OnSaveClicked(1)) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Save")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            title = "Save & Notify",
                            onClick = { viewModel.handleIntent(AddAppreciationIntent.OnSaveClicked(2)) },
                        )
                    }
                }
            }

            // Selection sheets
            EcareProSelectionBottomSheet(
                title = "Select appreciation category",
                isVisible = data.isCategorySheetVisible,
                onDismiss = { viewModel.handleIntent(AddAppreciationIntent.OnDismissCategorySheet) },
                options = data.categories.map { it.name },
                selectedOptions = listOfNotNull(data.selectedCategory?.name),
                onOptionsSelected = {
                    it.firstOrNull()?.let { name ->
                        viewModel.handleIntent(AddAppreciationIntent.OnCategorySelected(name))
                    }
                },
            )

            EcareProSelectionBottomSheet(
                title = "Select appreciation subcategory",
                isVisible = data.isSubcategorySheetVisible,
                onDismiss = { viewModel.handleIntent(AddAppreciationIntent.OnDismissSubcategorySheet) },
                options = data.subcategories.map { it.name },
                selectedOptions = listOfNotNull(data.selectedSubcategory?.name),
                onOptionsSelected = {
                    it.firstOrNull()?.let { name ->
                        viewModel.handleIntent(AddAppreciationIntent.OnSubcategorySelected(name))
                    }
                },
            )

            EcareProSelectionBottomSheet(
                title = "Select reward",
                isVisible = data.isRewardSheetVisible,
                onDismiss = { viewModel.handleIntent(AddAppreciationIntent.OnDismissRewardSheet) },
                options = data.rewards.map { it.name },
                selectedOptions = listOfNotNull(data.selectedReward?.name),
                onOptionsSelected = {
                    it.firstOrNull()?.let { name ->
                        viewModel.handleIntent(AddAppreciationIntent.OnRewardSelected(name))
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddAppreciationContent(
    data: AddAppreciationUiState,
    onIntent: (AddAppreciationIntent) -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Add Appreciation",
                onNavigationClicked = { onIntent(AddAppreciationIntent.OnBackClicked) },
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
                    label = "Select appreciation category",
                    value = data.selectedCategory?.name,
                    placeholder = "Select category",
                    onClick = { onIntent(AddAppreciationIntent.OnCategorySelectClicked) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownField(
                    label = "Select appreciation subcategory",
                    value = data.selectedSubcategory?.name,
                    placeholder = "Select subcategory",
                    onClick = { onIntent(AddAppreciationIntent.OnSubcategorySelectClicked) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        InputField(
                            label = "Appreciation count",
                            value = data.instanceCount,
                            placeholder = "No of instance",
                            onValueChange = {},
                            enabled = false,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label = "Reward",
                            value = data.selectedReward?.name,
                            placeholder = "Select",
                            onClick = { onIntent(AddAppreciationIntent.OnRewardSelectClicked) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    label = "Add remarks",
                    value = data.remarks,
                    placeholder = "Reason for appreciation",
                    onValueChange = { onIntent(AddAppreciationIntent.OnRemarksChanged(it)) },
                    maxLines = 4,
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { onIntent(AddAppreciationIntent.OnSaveClicked(1)) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Save")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        title = "Save & Notify",
                        onClick = { onIntent(AddAppreciationIntent.OnSaveClicked(2)) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Appreciation Screen")
@Composable
private fun AddAppreciationScreenPreview() {
    EcareProTheme {
        AddAppreciationContent(
            data = AddAppreciationUiState(
                userInfo = DisciplineUserInfo(
                    id = 1,
                    name = "John Doe",
                    className = "10-A",
                    admissionNo = "ADM001",
                    photo = null,
                    contactPerson = null,
                    contactMob = null,
                    designation = null,
                    mobile = null,
                    qualification = null,
                ),
                instanceCount = "3",
                remarks = "Good behavior",
            ),
        )
    }
}
