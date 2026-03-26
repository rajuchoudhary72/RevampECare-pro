package com.app.ecarepro.feature.message.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.personlist.PersonRowView
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.message.common.MessageEmptyState

// ============== ENTRY POINT ==============

@Composable
internal fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UsersScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
    )
}

// ============== CONTENT ==============

@Composable
private fun UsersScreenContent(
    uiState: UiState<UsersUiState>,
    handleIntent: (UsersIntent) -> Unit,
) {
    UiStateHandler(
        state = uiState,
        onRetry = { handleIntent(UsersIntent.OnRetry) },
    ) { data ->

        // User type selection bottom sheet
        EcareProSelectionBottomSheet(
            title = "Select User Type",
            isVisible = data.showUserTypeSheet,
            options = MessageUserType.entries.map { it.displayName },
            selectedOptions = listOf(data.selectedUserType.displayName),
            isMultiSelection = false,
            onDismiss = { handleIntent(UsersIntent.OnUserTypeSheetDismiss) },
            onOptionsSelected = { selected ->
                val userType = MessageUserType.entries
                    .firstOrNull { it.displayName == selected.firstOrNull() }
                if (userType != null) {
                    handleIntent(UsersIntent.OnUserTypeSelected(userType))
                }
            },
        )

        // List or empty state
        if (data.displayedUsers.isEmpty()) {
            MessageEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(data.displayedUsers, key = { it.id }) { user ->
                    PersonRowView(
                        person = user,
                        onClick = { /* TODO: navigate to user profile */ },
                    )
                    HorizontalDivider(
                        color = MaterialTheme.appColors.border.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true, name = "Students")
@Composable
private fun UsersScreenStudentPreview() {
    EcareProTheme {
        UsersScreenContent(
            uiState = UiState.Success(
                UsersUiState(
                    selectedUserType = MessageUserType.STUDENT,
                    students = sampleStudentUsers,
                    staff = sampleStaffUsers,
                    parents = sampleParentUsers,
                )
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Staff")
@Composable
private fun UsersScreenStaffPreview() {
    EcareProTheme {
        UsersScreenContent(
            uiState = UiState.Success(
                UsersUiState(
                    selectedUserType = MessageUserType.STAFF,
                    students = sampleStudentUsers,
                    staff = sampleStaffUsers,
                    parents = sampleParentUsers,
                )
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Parents")
@Composable
private fun UsersScreenParentPreview() {
    EcareProTheme {
        UsersScreenContent(
            uiState = UiState.Success(
                UsersUiState(
                    selectedUserType = MessageUserType.PARENT,
                    students = sampleStudentUsers,
                    staff = sampleStaffUsers,
                    parents = sampleParentUsers,
                )
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Bottom Sheet Open")
@Composable
private fun UsersScreenSheetPreview() {
    EcareProTheme {
        UsersScreenContent(
            uiState = UiState.Success(
                UsersUiState(
                    selectedUserType = MessageUserType.STUDENT,
                    showUserTypeSheet = true,
                    students = sampleStudentUsers,
                    staff = sampleStaffUsers,
                    parents = sampleParentUsers,
                )
            ),
            handleIntent = {},
        )
    }
}
