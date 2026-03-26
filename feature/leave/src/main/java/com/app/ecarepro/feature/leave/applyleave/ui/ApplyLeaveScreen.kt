package com.app.ecarepro.feature.leave.applyleave.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.LeaveDetail
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.R
import com.app.ecarepro.feature.leave.applyleave.data.ApplyLeaveScreenType
import com.app.ecarepro.feature.leave.applyleave.data.LeaveReason
import com.app.ecarepro.feature.leave.applyleave.data.SessionType
import com.app.ecarepro.feature.leave.applyleave.domain.ApplyLeaveViewModel
import com.app.ecarepro.feature.leave.applyleave.domain.UIState
import com.app.ecarepro.feature.leave.applyleave.ui.components.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveScreen(
    screenType: ApplyLeaveScreenType,
    viewModel: ApplyLeaveViewModel = hiltViewModel(),
    navigateToBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedLeaveType by viewModel.selectedLeaveType.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val startSession by viewModel.startSession.collectAsStateWithLifecycle()
    val endSession by viewModel.endSession.collectAsStateWithLifecycle()
    val selectedReason by viewModel.selectedReason.collectAsStateWithLifecycle()
    val isTermsAccepted by viewModel.isTermsAccepted.collectAsStateWithLifecycle()
    val calculatedDuration by viewModel.calculatedDuration.collectAsStateWithLifecycle()
    val attachmentFileName by viewModel.attachmentFileName.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showTermsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initialize(screenType)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
            viewModel.clearErrorMessage()
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.feature_leave_apply_leave),
                onNavigationClicked = navigateToBack
            )
        },
        containerColor = White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                UIState.Loading, UIState.NotInitialized -> {
                    Loader()
                }
                UIState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Failed to load",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadSettings() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.appColors.primary
                            )
                        ) {
                            Text(stringResource(R.string.feature_leave_retry))
                        }
                    }
                }
                else -> {
                    ApplyLeaveForm(
                        screenType = screenType,
                        viewModel = viewModel,
                        selectedLeaveType = selectedLeaveType,
                        startDate = startDate,
                        endDate = endDate,
                        startSession = startSession,
                        endSession = endSession,
                        selectedReason = selectedReason,
                        isTermsAccepted = isTermsAccepted,
                        calculatedDuration = calculatedDuration,
                        attachmentFileName = attachmentFileName,
                        onTermsClick = { showTermsSheet = true },
                        onSubmit = { viewModel.submitLeaveRequest(navigateToBack) }
                    )
                }
            }

            if (uiState is UIState.SilentLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.appColors.primary
                )
            }

            // Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }

        // Terms sheet
        if (showTermsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTermsSheet = false },
                containerColor = White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_leave_terms_conditions),
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = viewModel.termsAndConditions.ifEmpty { stringResource(R.string.feature_leave_no_terms) },
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showTermsSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.appColors.primary
                        )
                    ) {
                        Text(stringResource(R.string.feature_leave_close))
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplyLeaveForm(
    screenType: ApplyLeaveScreenType,
    viewModel: ApplyLeaveViewModel,
    selectedLeaveType: LeaveDetail?,
    startDate: java.time.LocalDate,
    endDate: java.time.LocalDate,
    startSession: com.app.ecarepro.feature.leave.applyleave.data.SessionType,
    endSession: com.app.ecarepro.feature.leave.applyleave.data.SessionType,
    selectedReason: com.app.ecarepro.feature.leave.applyleave.data.LeaveReason?,
    isTermsAccepted: Boolean,
    calculatedDuration: Double,
    attachmentFileName: String?,
    onTermsClick: () -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Leave Type Selector (Staff Only)
        if (screenType.showLeaveTypeSelector) {
            item {
                LeaveTypeSelector(
                    selectedLeaveType = selectedLeaveType,
                    leaveTypes = viewModel.leaveTypes,
                    error = viewModel.leaveTypeError,
                    onSelect = { viewModel.onLeaveTypeSelected(it) }
                )
            }
        }

        // Leave Balance (Staff Only)
        if (screenType.showLeaveBalance) {
            viewModel.leaveBalance?.let { balance ->
                item {
                    LeaveBalanceView(
                        balance = balance,
                        error = viewModel.balanceError
                    )
                }
            }
        }

        // Date Section
        item {
            DateSection(
                startDate = startDate,
                endDate = endDate,
                startSession = startSession,
                endSession = endSession,
                minDate = viewModel.minSelectableDate,
                maxDate = viewModel.maxSelectableDate,
                showSessionSelector = screenType.showSessionSelector,
                calculatedDuration = calculatedDuration,
                error = viewModel.dateError,
                onStartDateChange = { viewModel.onStartDateChanged(it) },
                onEndDateChange = { viewModel.onEndDateChanged(it) },
                onStartSessionChange = { viewModel.onStartSessionChanged(it) },
                onEndSessionChange = { viewModel.onEndSessionChanged(it) }
            )
        }

        // Reason Selector
        item {
            ReasonSelector(
                selectedReason = selectedReason,
                error = viewModel.reasonError,
                onSelect = { viewModel.onReasonSelected(it) }
            )
        }

        // Document Upload
        item {
            DocumentUploadSection(
                attachmentFileName = attachmentFileName,
                isRequired = viewModel.isAttachmentRequired,
                error = viewModel.attachmentError,
                onFileSelected = { data, fileName ->
                    viewModel.handleSelectedFile(data, fileName)
                },
                onRemove = { viewModel.removeAttachment() }
            )
        }

        // Terms and Conditions
        item {
            TermsCheckbox(
                isAccepted = isTermsAccepted,
                error = viewModel.termsError,
                onToggle = { viewModel.toggleTermsAccepted() },
                onTermsClick = onTermsClick
            )
        }

        // Submit Button
        item {
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.appColors.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.feature_leave_submit_request),
                    style = MaterialTheme.appTypography.interSemiBold14px
                )
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

// Preview Functions
@Preview(showBackground = true, name = "Apply Leave - Student Form")
@Composable
private fun ApplyLeaveScreenStudentPreview() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ApplyLeaveForm(
                screenType = ApplyLeaveScreenType.STUDENT,
                viewModel = getMockViewModel(),
                selectedLeaveType = null,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(2),
                startSession = SessionType.FIRST_HALF,
                endSession = SessionType.SECOND_HALF,
                selectedReason = LeaveReason.MEDICAL,
                isTermsAccepted = false,
                calculatedDuration = 3.0,
                attachmentFileName = null,
                onTermsClick = {},
                onSubmit = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Apply Leave - Staff Form with Balance")
@Composable
private fun ApplyLeaveScreenStaffPreview() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ApplyLeaveForm(
                screenType = ApplyLeaveScreenType.STAFF,
                viewModel = getMockViewModelWithLeaveType(),
                selectedLeaveType = getMockLeaveDetail(),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(1),
                startSession = SessionType.FIRST_HALF,
                endSession = SessionType.SECOND_HALF,
                selectedReason = LeaveReason.VACATION,
                isTermsAccepted = true,
                calculatedDuration = 1.5,
                attachmentFileName = "medical_certificate.pdf",
                onTermsClick = {},
                onSubmit = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Apply Leave - With Validation Errors")
@Composable
private fun ApplyLeaveScreenWithErrorsPreview() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ApplyLeaveForm(
                screenType = ApplyLeaveScreenType.STAFF,
                viewModel = getMockViewModelWithErrors(),
                selectedLeaveType = null,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().minusDays(1), // Invalid: end before start
                startSession = SessionType.FIRST_HALF,
                endSession = SessionType.FIRST_HALF,
                selectedReason = null,
                isTermsAccepted = false,
                calculatedDuration = 0.0,
                attachmentFileName = null,
                onTermsClick = {},
                onSubmit = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Loading State")
@Composable
private fun ApplyLeaveScreenLoadingPreview() {
    EcareProTheme {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = stringResource(R.string.feature_leave_apply_leave),
                    onNavigationClicked = {}
                )
            },
            containerColor = White
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Loader()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Error State")
@Composable
private fun ApplyLeaveScreenErrorPreview() {
    EcareProTheme {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = stringResource(R.string.feature_leave_apply_leave),
                    onNavigationClicked = {}
                )
            },
            containerColor = White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Failed to load leave settings",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.primary
                    )
                ) {
                    Text(stringResource(R.string.feature_leave_retry))
                }
            }
        }
    }
}

// Mock Data for Previews
private val mockRepository = object : com.app.ecarepro.core.domain.repository.AppliedLeavesRepository {
    override fun getAppliedLeaves(status: Int, order: Int, applType: Int, page: Int, showAttendance: Boolean, duration: Int) = kotlinx.coroutines.flow.emptyFlow<Result<com.app.ecarepro.core.domain.model.LeaveReportResponse>>()
    override fun performLeaveAction(request: com.app.ecarepro.core.domain.model.LeaveActionRequest) = kotlinx.coroutines.flow.emptyFlow<Result<com.app.ecarepro.core.domain.model.LeaveActionResponse>>()
    override fun applyLeave(request: com.app.ecarepro.core.domain.model.ApplyLeaveRequest) = kotlinx.coroutines.flow.emptyFlow<Result<com.app.ecarepro.core.domain.model.ApplyLeaveResponse>>()
    override fun getLeaveSettings() = kotlinx.coroutines.flow.emptyFlow<Result<com.app.ecarepro.core.domain.model.LeaveSettingResponse>>()
}

private fun getMockViewModel(): ApplyLeaveViewModel {
    // This is just for preview structure, actual implementation would need proper mocking
    return object : ApplyLeaveViewModel(mockRepository) {
        override val leaveTypeError: String? = null
        override val reasonError: String? = null
        override val attachmentError: String? = null
        override val termsError: String? = null
        override val dateError: String? = null
        override val balanceError: String? = null
        override val leaveTypes: List<LeaveDetail> = emptyList()
        override val isAttachmentRequired: Boolean = false
        override val termsAndConditions: String = "Sample terms and conditions..."
    }
}

private fun getMockViewModelWithLeaveType(): ApplyLeaveViewModel {
    return object : ApplyLeaveViewModel(mockRepository) {
        override val leaveTypeError: String? = null
        override val reasonError: String? = null
        override val attachmentError: String? = null
        override val termsError: String? = null
        override val dateError: String? = null
        override val balanceError: String? = null
        override val leaveTypes: List<LeaveDetail> = listOf(getMockLeaveDetail())
        override val isAttachmentRequired: Boolean = true
        override val termsAndConditions: String = "Sample terms and conditions..."
    }
}

private fun getMockViewModelWithErrors(): ApplyLeaveViewModel {
    return object : ApplyLeaveViewModel(mockRepository) {
        override val leaveTypeError: String? = "Please select a leave type"
        override val reasonError: String? = "Please select a reason"
        override val attachmentError: String? = "Please attach supporting document"
        override val termsError: String? = "Please accept terms and conditions"
        override val dateError: String? = "End date cannot be before start date"
        override val balanceError: String? = "Insufficient leave balance. Available: 2.0"
        override val leaveTypes: List<LeaveDetail> = listOf(getMockLeaveDetail())
        override val isAttachmentRequired: Boolean = true
        override val termsAndConditions: String = "Sample terms and conditions..."
    }
}

private fun getMockLeaveDetail(): LeaveDetail {
    return LeaveDetail(
        leaveID = 1,
        leaveType = "Casual Leave",
        leaveAbbr = "CL",
        total = 10.0,
        taken = 3.0,
        inCurMonth = 1.0,
        minAcceptableLimit = 0.5,
        maxAcceptableLimit = 5.0,
        applyBeforeHours = 10,
        minimumLimit = 1,
        attachmentMandatory = true,
        sandwichEnable = true
    )
}
