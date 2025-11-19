package com.app.ecarepro.feature.syllabus.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProLottieAnimation
import com.app.ecarepro.designsystem.core.component.EcareProOutlinedTextField
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.FileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.SelectedFileType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.UploadOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R

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
                AddSyllabusEvent.ShowSuccessMessage -> {

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
        onSnackbarDismissed = onSnackbarDismissed
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
                            append("Only for classes with section-wise syllabus differences")
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
                            label = "Select class",
                            value = data.selectedClass,
                            placeholder = "Class X",
                            onClick = { }
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownField(
                                    label = "Select class",
                                    value = data.selectedClass,
                                    placeholder = "Class X",
                                    onClick = { }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DropdownField(
                                    label = "Select section",
                                    value = data.selectedSection,
                                    placeholder = "Section A",
                                    onClick = { }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    DropdownField(
                        label = "Select subject",
                        value = data.selectedSubject,
                        placeholder = "All subjects",
                        onClick = { /* Open Dropdown */ }
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    InputField(
                        label = "Syllabus title",
                        value = data.title,
                        placeholder = "Add your title here",
                        onValueChange = { handleIntent(AddSyllabusIntent.OnTitleChanged(it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Text(
                        text = "Add file",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FileUploadBox(
                        selectedFileUri = data.selectedFileUri,
                        selectedFileType = data.selectedFileType,
                        onClick = { handleIntent(AddSyllabusIntent.OnAddFileClicked) }
                    )

                    Spacer(modifier = Modifier.weight(1f))


                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Add new syllabus",
                        onClick = { handleIntent(AddSyllabusIntent.OnSubmitClicked) }
                    )
                }

                FileUploadBottomSheet(
                    isVisible = data.isFileUploadSheetVisible, // From UiState
                    onDismiss = {
                        handleIntent(AddSyllabusIntent.OnDismissFileUploadSheet)
                    },
                    allowedOptions = listOf(
                        UploadOption.CAMERA,
                        UploadOption.GALLERY,
                        UploadOption.DOCUMENT
                    ),
                    onFileSelected = { uri, type ->
                        handleIntent(AddSyllabusIntent.OnFileSelected(uri, type))
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        EcareProOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            value = value,
            onValueChange = {},
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textPrimary
                )
            },
            enabled = false,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_down),
                    contentDescription = "Select",
                    tint = MaterialTheme.appColors.textSecondary
                )
            },
        )
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        EcareProOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun FileUploadBox(
    onClick: () -> Unit,
    selectedFileUri: Uri?,
    selectedFileType: SelectedFileType?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.appColors.background, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .drawBehindBorder(
                strokeWidth = 1.dp,
                color = MaterialTheme.appColors.border,
                cornerRadius = 8.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selectedFileUri != null) {
            EcareProAsyncImage(
                imageUrl = selectedFileUri.toString(),
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                EcareProLottieAnimation(
                    modifier = Modifier
                        .height(49.dp)
                        .width(56.dp),
                    lottieRawId = R.raw.upload_to_cloud
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Click here to add file",
                    style = MaterialTheme.appTypography.nunitoBold12px.copy(fontSize = 16.sp),
                    color = MaterialTheme.appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Max 10 MB files are allowed",
                    style = MaterialTheme.appTypography.nunitoMedium12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }

    }
}

fun Modifier.drawBehindBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp,
    dashLength: Dp = 16.dp,
    gapLength: Dp = 10.dp,
) = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f
        )
    )

    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = size.toRect(),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        )
    }
    drawPath(
        path = path,
        color = color,
        style = stroke
    )
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
