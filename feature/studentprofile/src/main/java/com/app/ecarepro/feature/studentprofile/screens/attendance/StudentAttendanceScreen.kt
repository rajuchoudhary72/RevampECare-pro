package com.app.ecarepro.feature.studentprofile.screens.attendance

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.feature.studentprofile.navigation.StudentProfileNavigationGraph

@OptIn(ExperimentalMaterial3Api::class) // Add this line
@Composable
fun StudentAttendanceScreen(
    navKey: StudentProfileNavigationGraph.StudentAttendance,
    navigateToBack: () -> Unit,
) {
    val viewModel: StudentAttendanceViewModel = navKeyViewModel(navKey)

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                StudentAttendanceEvent.NavigateBack -> navigateToBack()
            }
        }
    }

    StudentAttendanceContent(
        viewModel = viewModel,
        navigateToBack = navigateToBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentAttendanceContent(
    viewModel: StudentAttendanceViewModel,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val title = if (uiState is com.app.ecarepro.core.ui.UiState.Success) {
        (uiState as com.app.ecarepro.core.ui.UiState.Success<StudentAttendanceUiState>).data.studentName
            .ifBlank { "Attendance" }
    } else "Attendance"

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = title,
                onNavigationClicked = { viewModel.handleIntent(StudentAttendanceIntent.OnBackClicked) }
            )
        }
    ) { _ ->
        UiStateHandler(state = uiState) { data ->
            AttendanceTab(
                attendanceDTL = data.attendanceDTL,
                academicYears = data.academicYears,
                selectedYearId = data.selectedAttendanceYearId,
                expandedMonthIds = data.expandedMonthIds,
                monthlyDetailCache = data.monthlyDetailCache,
                loadingMonthIds = data.loadingMonthIds,
                isLoadingYear = data.isLoadingAttendanceYear,
                onYearSelected = { yearId ->
                    viewModel.handleIntent(StudentAttendanceIntent.OnAttendanceYearSelected(yearId))
                },
                onToggleMonth = { monthId ->
                    viewModel.handleIntent(StudentAttendanceIntent.OnToggleAttendanceMonth(monthId))
                }
            )
        }
    }
}
