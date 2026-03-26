package com.app.ecarepro.feature.update_record.update_profile_picture

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ProfilePictureStudent
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortBottomSheet
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.component.UploadOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun UpdateProfilePictureScreen(
    viewModel: UpdateProfilePictureViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is UpdateProfilePictureEvent.NavigateBack -> navigateBack()
                is UpdateProfilePictureEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    Content(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: UpdateProfilePictureUiState,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    handleIntent: (UpdateProfilePictureIntent) -> Unit,
) {
    val filteredStudents by remember(uiState.students, uiState.searchQuery) {
        derivedStateOf {
            if (uiState.searchQuery.isBlank()) uiState.students
            else uiState.students.filter {
                it.name.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.admissionNumber.contains(uiState.searchQuery, ignoreCase = true)
            }
        }
    }

    val selectedClassName = uiState.classes.getOrNull(uiState.selectedClassIndex)?.className ?: ""

    EcareProFileUploadBottomSheet(
        isVisible = uiState.pickerForStID != null,
        onDismiss = { handleIntent(UpdateProfilePictureIntent.DismissPicker) },
        allowedOptions = listOf(UploadOption.CAMERA, UploadOption.GALLERY),
        maxFileSizeInMb = 5,
        onFilesSelected = { files ->
            val stID = uiState.pickerForStID ?: return@EcareProFileUploadBottomSheet
            files.firstOrNull()?.let { handleIntent(UpdateProfilePictureIntent.OnImageSelected(stID, it)) }
        },
    )

    if (uiState.showSortSheet) {
        SortBottomSheet(
            currentSortConfig = uiState.sortConfig,
            onSortSelected = { handleIntent(UpdateProfilePictureIntent.OnSortSelected(it)) },
            onDismiss = { handleIntent(UpdateProfilePictureIntent.DismissSortSheet) },
            availableOptions = listOf(SortOption.ROLL_NUMBER, SortOption.ADMISSION_NUMBER, SortOption.NAME),
        )
    }

    EcareProScaffold(
        topBar = {
            Column {
                if (uiState.uploadingStID != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.appColors.primary)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = White,
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Updating profile image. Please wait!",
                            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 13.sp),
                            color = White,
                        )
                    }
                }
                EcareProTopAppBar(
                    title = "Update profile picture",
                    onNavigationClicked = { handleIntent(UpdateProfilePictureIntent.OnBackClicked) },
                )
            }
        },
        bottomBar = {
            BottomSearchBarView(
                searchText = uiState.searchQuery,
                onSearchTextChange = { handleIntent(UpdateProfilePictureIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by name, admission number...",
                showSortButton = true,
                onSortClick = { handleIntent(UpdateProfilePictureIntent.OnSortClick) },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (uiState.isLoadingClasses) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
                return@EcareProScaffold
            }

            if (uiState.classes.isNotEmpty()) {
                EcareProClassTabs(
                    selectedTabIndex = uiState.selectedClassIndex,
                    tabs = uiState.classes.map { it.className ?: "" },
                    onTabClick = { handleIntent(UpdateProfilePictureIntent.SelectClass(it)) },
                    applyOrdinalTransform = false,
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            when {
                uiState.isLoadingStudents -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                }
                filteredStudents.isEmpty() -> {
                    EcareProEmptyState(message = "No students found")
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(filteredStudents, key = { _, s -> s.stID }) { _, student ->
                            ProfilePictureStudentItem(
                                student = student,
                                className = selectedClassName,
                                isUploading = uiState.uploadingStID == student.stID,
                                onEditClicked = { handleIntent(UpdateProfilePictureIntent.OnEditClicked(student.stID)) },
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFFEEEEEE),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilePictureStudentItem(
    student: ProfilePictureStudent,
    className: String,
    isUploading: Boolean,
    onEditClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.Center) {
            EcareProAsyncImage(
                imageUrl = student.photo,
                contentDescription = student.name,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (className.isNotBlank()) "${student.name}, $className" else student.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                Text(
                    text = "Admission No: ${student.admissionNumber}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
                if (student.rollNumber.isNotBlank()) {
                    Text(
                        text = " · Roll No: ${student.rollNumber}",
                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
            if (student.fatherName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "D/O: ${student.fatherName}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(
            onClick = onEditClicked,
            enabled = !isUploading,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Update photo",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

// region Previews

private val previewClasses = listOf(
    Class(
        id = "1", className = "10-A",
        classID = 1,
        isSelect = false
    ),
    Class(
        id = "2", className = "10-B",
        classID = 2,
        isSelect = false
    ),
)

private fun makeStudent(
    stID: Int,
    name: String,
    rollNumber: String = "",
    fatherName: String = "",
) = ProfilePictureStudent(
    stID = stID,
    name = name,
    rollNumber = rollNumber,
    admissionNumber = "ADM00$stID",
    photo = "",
    fatherName = fatherName,
)

@Preview(showBackground = true, name = "Student List")
@Composable
private fun PreviewStudentList() {
    EcareProTheme {
        Content(
            uiState = UpdateProfilePictureUiState(
                classes = previewClasses,
                selectedClassIndex = 0,
                students = listOf(
                    makeStudent(1, "Aarav Sharma", rollNumber = "1", fatherName = "Rajesh Sharma"),
                    makeStudent(2, "Priya Patel", rollNumber = "2", fatherName = "Suresh Patel"),
                    makeStudent(3, "Karan Mehta", rollNumber = "3"),
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun PreviewLoading() {
    EcareProTheme {
        Content(
            uiState = UpdateProfilePictureUiState(
                classes = previewClasses,
                isLoadingStudents = true,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Uploading Banner")
@Composable
private fun PreviewUploading() {
    EcareProTheme {
        Content(
            uiState = UpdateProfilePictureUiState(
                classes = previewClasses,
                students = listOf(
                    makeStudent(1, "Aarav Sharma", rollNumber = "1", fatherName = "Rajesh Sharma"),
                    makeStudent(2, "Priya Patel", rollNumber = "2"),
                ),
                uploadingStID = 1,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Student Item - Normal")
@Composable
private fun PreviewStudentItem() {
    EcareProTheme {
        ProfilePictureStudentItem(
            student = makeStudent(1, "Aarav Sharma", rollNumber = "1", fatherName = "Rajesh Sharma"),
            className = "10-A",
            isUploading = false,
            onEditClicked = {},
        )
    }
}

// endregion
