package com.app.ecarepro.feature.assignment.screens

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
import androidx.compose.ui.Alignment
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
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProCheckboxWithText
import com.app.ecarepro.designsystem.core.component.EcareProDatePicker
import com.app.ecarepro.designsystem.core.component.EcareProDropdownField
import com.app.ecarepro.designsystem.core.component.EcareProFileAttachment
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProSwitch
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.UploadOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.components.StudentSelectionBottomSheet

@Composable
fun AddAssignmentScreen(
    viewModel: AddAssignmentViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AddAssignmentEvent.NavigateBack -> navigateToBack()
                is AddAssignmentEvent.ShowSuccessMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    AddAssignmentContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAssignmentContent(
    uiState: UiState<AddAssignmentUiState>,
    handleIntent: (AddAssignmentIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Add assignment",
                    onNavigationClicked = { handleIntent(AddAssignmentIntent.OnBackClicked) })
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
                                onClick = { handleIntent(AddAssignmentIntent.OnTabSelected(index)) },
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
            modifier = Modifier.padding(paddingValues), state = uiState
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {

                if (data.selectedTabIndex == 1) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.appColors.error)) {
                                append("*")
                            }
                            append(stringResource(R.string.core_designsystem_only_for_classes_with_section_wise_syllabus_differences))
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
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Box(modifier = Modifier.weight(1f)) {
                            EcareProDropdownField(
                                label = stringResource(R.string.core_designsystem_select_subject),
                                value = data.selectedSubject,
                                placeholder = stringResource(R.string.core_designsystem_subjects),
                                onClick = {
                                    handleIntent(AddAssignmentIntent.OnSubjectSelectClicked)
                                })
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            EcareProDropdownField(
                                label = stringResource(R.string.core_designsystem_select_class),
                                value = data.selectedClass.joinToString { it.className.orEmpty() },
                                placeholder = stringResource(R.string.core_designsystem_class),
                                onClick = {
                                    handleIntent(AddAssignmentIntent.OnClassSelectClicked)
                                })
                        }

                    }

                    if (data.selectedTabIndex == 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        EcareProDropdownField(
                            label = stringResource(R.string.core_designsystem_select_student),
                            value = data.selectedStudents.joinToString { it.studentName.orEmpty() },
                            placeholder = stringResource(R.string.core_designsystem_student),
                            onClick = { handleIntent(AddAssignmentIntent.OnStudentSelectClicked) })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EcareProDropdownField(
                        label = "Assignment date",
                        value = data.assignmentDate,
                        placeholder = "Click here to add date",
                        onClick = { handleIntent(AddAssignmentIntent.OnAssignmentDateSelectClicked) })

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Submission Date",
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary
                        )

                        EcareProSwitch(
                            checked = data.isSubmissionDateVisible, onCheckedChange = {
                                handleIntent(AddAssignmentIntent.ToggleSubmissionDateVisibility)
                            }, width = 24.dp, height = 16.dp, thumbSize = 10.dp
                        )
                    }

                    if (data.isSubmissionDateVisible) {
                        Spacer(modifier = Modifier.height(8.dp))
                        EcareProDropdownField(
                            value = data.submissionDate,
                            placeholder = "Click here to add date",
                            onClick = { handleIntent(AddAssignmentIntent.OnSubmissionDateSelectClicked) })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EcareProInputField(
                        label = stringResource(R.string.core_designsystem_syllabus_title),
                        value = data.title,
                        placeholder = stringResource(R.string.core_designsystem_add_your_title_here),
                        onValueChange = { handleIntent(AddAssignmentIntent.OnTitleChanged(it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EcareProInputField(
                        label = "Type your assignment",
                        value = data.type,
                        placeholder = "Add your type here",
                        minLines = 3,
                        maxLines = 3,
                        singleLine = false,
                        onValueChange = { handleIntent(AddAssignmentIntent.OnTypeChanged(it)) })

                    Spacer(modifier = Modifier.height(16.dp))

                    EcareProFileAttachment(
                        selectedFile = data.selectedFiles,
                        onClickPickFile = { handleIntent(AddAssignmentIntent.OnAddFileClicked) },
                        onClickPickMoreFile = { handleIntent(AddAssignmentIntent.OnAddFileClicked) },
                        onClickDeleteFile = {
                            handleIntent(
                                AddAssignmentIntent.OnDeleteSelectedFile(
                                    it
                                )
                            )
                        })
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    EcareProCheckboxWithText(
                        text = "Active", isChecked = data.isActive, onCheckedChange = {
                            handleIntent(AddAssignmentIntent.ToggleIsActive)
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                    EcareProCheckboxWithText(
                        text = "Allow students for multiple submission",
                        isChecked = data.isAllowedForMultipleSubmission,
                        onCheckedChange = {
                            handleIntent(AddAssignmentIntent.ToggleIsAllowedForMultipleSubmission)
                        })
                    Spacer(modifier = Modifier.height(4.dp))

                    EcareProCheckboxWithText(
                        text = "Allow students for late submission",
                        isChecked = data.isAllowedForLateSubmission,
                        onCheckedChange = {
                            handleIntent(AddAssignmentIntent.ToggleIsAllowedForLateSubmission)
                        })
                }


                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    title = "Add new assignment",
                    onClick = { handleIntent(AddAssignmentIntent.OnSubmitClicked) })


            }


            EcareProFileUploadBottomSheet(
                isVisible = data.isFileUploadSheetVisible,
                onDismiss = {
                    handleIntent(AddAssignmentIntent.OnDismissFileUploadSheet)
                },
                allowedOptions = listOf(
                    UploadOption.CAMERA, UploadOption.GALLERY, UploadOption.DOCUMENT
                ),
                allowMultiple = true,
                onShowError = { handleIntent(AddAssignmentIntent.OnShowError(it)) },
                onFilesSelected = { files ->
                    handleIntent(AddAssignmentIntent.OnFileSelected(files))
                })

            EcareProSelectionBottomSheet(
                title = stringResource(R.string.core_designsystem_select_class),
                isVisible = data.isClassSelectSheetVisible,
                onDismiss = { handleIntent(AddAssignmentIntent.OnDismissClassSelectSheet) },
                options = data.classes.map { it.className.orEmpty() },
                isMultiSelection = true,
                selectedOptions = data.selectedClass.map { it.className.orEmpty() },
                onOptionsSelected = {
                    handleIntent(
                        AddAssignmentIntent.OnClassChanged(
                            it
                        )
                    )
                })

            StudentSelectionBottomSheet(
                title = "Select Student",
                isVisible = data.isStudentSelectSheetVisible,
                onDismiss = { handleIntent(AddAssignmentIntent.OnDismissStudentSelectSheet) },
                options = data.students,
                isMultiSelection = true,
                selectedOptions = data.selectedStudents,
                onOptionsSelected = {
                    handleIntent(
                        AddAssignmentIntent.OnStudentChanged(
                            it
                        )
                    )
                })

            EcareProSelectionBottomSheet(
                stringResource(R.string.core_designsystem_select_subject),
                isVisible = data.isSubjectSelectSheetVisible,
                onDismiss = { handleIntent(AddAssignmentIntent.OnDismissSubjectSelectSheet) },
                options = data.subject.map { it.subjectName.orEmpty() },
                selectedOptions = listOf(data.selectedSubject.orEmpty()),
                onOptionsSelected = {
                    handleIntent(
                        AddAssignmentIntent.OnSubjectChanged(
                            it.firstOrNull().orEmpty()
                        )
                    )
                })

            EcareProDatePicker(
                isVisible = data.isAssignmentDatePickerVisible,
                onDismiss = { handleIntent(AddAssignmentIntent.OnDismissAssignmentDateSheet) },
                onDateSelected = { date ->
                    handleIntent(
                        AddAssignmentIntent.OnSelectAssignmentDate(
                            date
                        )
                    )
                })

            EcareProDatePicker(
                isVisible = data.isSubmissionDatePickerVisible,
                onDismiss = { handleIntent(AddAssignmentIntent.OnDismissSubmissionDateSheet) },
                onDateSelected = { date ->
                    handleIntent(
                        AddAssignmentIntent.OnSelectSubmissionDate(
                            date
                        )
                    )
                })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddSyllabusPreview() {
    EcareProTheme {
        AddAssignmentContent(
            uiState = UiState.Success(AddAssignmentUiState()),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddSyllabusSectionWisePreview() {
    EcareProTheme {
        AddAssignmentContent(
            uiState = UiState.Success(AddAssignmentUiState(selectedTabIndex = 1)),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {})
    }
}


