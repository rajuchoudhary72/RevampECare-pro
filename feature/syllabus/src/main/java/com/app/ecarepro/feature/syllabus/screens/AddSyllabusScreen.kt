package com.app.ecarepro.feature.syllabus.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
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
import com.app.ecarepro.feature.syllabus.R
import com.app.ecarepro.feature.syllabus.componets.DropdownField
import com.app.ecarepro.feature.syllabus.componets.FileUploadBox
import com.app.ecarepro.feature.syllabus.componets.InputField

@Composable
fun AddSyllabusScreen(
    viewModel: AddSyllabusViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AddSyllabusEvent.NavigateBack -> navigateToBack()
                is AddSyllabusEvent.ShowSuccessMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    AddSyllabusContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSyllabusContent(
    uiState: UiState<AddSyllabusUiState>,
    handleIntent: (AddSyllabusIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Add syllabus",
                    onNavigationClicked = { handleIntent(AddSyllabusIntent.OnBackClicked) }
                )
                if (uiState is UiState.Success) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = uiState.data.selectedTabIndex,
                        containerColor = White,
                        edgePadding = 0.dp,
                        minTabWidth = 70.dp
                    ) {
                        uiState.data.tabs.forEachIndexed { index, title ->
                            val isSelected = index == uiState.data.selectedTabIndex
                            val textStyle =
                                if (isSelected) MaterialTheme.appTypography.interSemiBold14px.copy(
                                    fontSize = 16.sp
                                )
                                else MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp)

                            Tab(
                                selected = uiState.data.selectedTabIndex == index,
                                onClick = { handleIntent(AddSyllabusIntent.OnTabSelected(index)) },
                                text = {
                                    Text(
                                        text = title,
                                        style = textStyle,
                                    )
                                },
                                selectedContentColor = MaterialTheme.appColors.primary,
                                unselectedContentColor = MaterialTheme.appColors.textPrimary,
                            )
                        }
                    }
                }
            }

        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) uiState.data.isLoading else false
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                if (data.selectedTabIndex == 1) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.appColors.error)) {
                                append("*")
                            }
                            append(stringResource(R.string.feature_syllabus_only_for_classes_with_section_wise_syllabus_differences))
                        },
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.appColors.background)
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {


                    if (data.selectedTabIndex == 0) {
                        DropdownField(
                            label = stringResource(R.string.feature_syllabus_select_class),
                            value = data.selectedClass,
                            placeholder = stringResource(R.string.feature_syllabus_class),
                            onClick = {
                                handleIntent(AddSyllabusIntent.OnClassSelectClicked)
                            }
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownField(
                                    label = stringResource(R.string.feature_syllabus_select_class),
                                    value = data.selectedClass,
                                    placeholder = stringResource(R.string.feature_syllabus_class),
                                    onClick = {
                                        handleIntent(AddSyllabusIntent.OnClassSelectClicked)
                                    }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownField(
                                    label = stringResource(R.string.feature_syllabus_select_section),
                                    value = data.selectedSection?.joinToString(),
                                    placeholder = stringResource(R.string.feature_syllabus_section),
                                    onClick = {
                                        handleIntent(AddSyllabusIntent.OnSectionSelectClicked)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    DropdownField(
                        label = stringResource(R.string.feature_syllabus_select_subject),
                        value = data.selectedSubject,
                        placeholder = stringResource(R.string.feature_syllabus_all_subjects),
                        onClick = { handleIntent(AddSyllabusIntent.OnSubjectSelectClicked) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    InputField(
                        label = stringResource(R.string.feature_syllabus_syllabus_title),
                        value = data.title,
                        placeholder = stringResource(R.string.feature_syllabus_add_your_title_here),
                        onValueChange = { handleIntent(AddSyllabusIntent.OnTitleChanged(it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))



                    EcareProFileAttachment(
                        selectedFile = data.selectedFile?.let { listOf(it) }?:emptyList(),
                        onClickPickFile = { handleIntent(AddSyllabusIntent.OnAddFileClicked) },
                        onClickDeleteFile = { handleIntent(AddSyllabusIntent.OnDeleteSelectedFile(it)) }
                    )

                    Spacer(modifier = Modifier.weight(1f))


                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.feature_syllabus_add_new_syllabus),
                        onClick = { handleIntent(AddSyllabusIntent.OnSubmitClicked) }
                    )
                }

                EcareProFileUploadBottomSheet(
                    isVisible = data.isFileUploadSheetVisible,
                    onDismiss = {
                        handleIntent(AddSyllabusIntent.OnDismissFileUploadSheet)
                    },
                    allowedOptions = listOf(
                        UploadOption.CAMERA,
                        UploadOption.GALLERY,
                        UploadOption.DOCUMENT
                    ),
                    onShowError = { handleIntent(AddSyllabusIntent.OnShowError(it)) },
                    onFilesSelected = { file ->
                        val attachment = file.firstOrNull()
                        if (attachment != null)
                            handleIntent(AddSyllabusIntent.OnFileSelected(attachment))
                    }
                )
            }

            EcareProSelectionBottomSheet(
                title = stringResource(R.string.feature_syllabus_select_class),
                isVisible = data.isClassSelectSheetVisible,
                onDismiss = { handleIntent(AddSyllabusIntent.OnDismissClassSelectSheet) },
                options = data.classes.map { it.className.orEmpty() },
                selectedOptions = listOf(data.selectedClass.orEmpty()),
                onOptionsSelected = {
                    handleIntent(
                        AddSyllabusIntent.OnClassChanged(
                            it.firstOrNull().orEmpty()
                        )
                    )
                }
            )

            EcareProSelectionBottomSheet(
                title = stringResource(R.string.feature_syllabus_select_section),
                isVisible = data.isSectionSelectSheetVisible,
                onDismiss = { handleIntent(AddSyllabusIntent.OnDismissSectionSelectSheet) },
                options = data.sections.map { it.secName.orEmpty() },
                selectedOptions = data.selectedSection ?: emptyList(),
                isMultiSelection = true,
                onOptionsSelected = {
                    handleIntent(
                        AddSyllabusIntent.OnSectionChanged(it)
                    )
                }
            )
            EcareProSelectionBottomSheet(
                stringResource(R.string.feature_syllabus_select_subject),
                isVisible = data.isSubjectSelectSheetVisible,
                onDismiss = { handleIntent(AddSyllabusIntent.OnDismissSubjectSelectSheet) },
                options = data.subject.map { it.subjectName.orEmpty() },
                selectedOptions = listOf(data.selectedSubject.orEmpty()),
                onOptionsSelected = {
                    handleIntent(
                        AddSyllabusIntent.OnSubjectChanged(
                            it.firstOrNull().orEmpty()
                        )
                    )
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddSyllabusPreview() {
    EcareProTheme {
        AddSyllabusContent(
            uiState = UiState.Success(AddSyllabusUiState()),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddSyllabusSectionWisePreview() {
    EcareProTheme {
        AddSyllabusContent(
            uiState = UiState.Success(AddSyllabusUiState(selectedTabIndex = 1)),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}
