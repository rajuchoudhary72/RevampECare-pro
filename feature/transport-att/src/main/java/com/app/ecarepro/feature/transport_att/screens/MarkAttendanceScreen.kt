package com.app.ecarepro.feature.transport_att.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.TransportConstants
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.transport_att.TransportAttEvent
import com.app.ecarepro.feature.transport_att.TransportAttIntent
import com.app.ecarepro.feature.transport_att.TransportAttUiState
import com.app.ecarepro.feature.transport_att.TransportAttViewModel
import com.app.ecarepro.feature.transport_att.components.ConfirmAttendanceDialog
import com.app.ecarepro.feature.transport_att.components.DropConfirmDialog
import com.app.ecarepro.feature.transport_att.components.StudentAttendanceItem

@Composable
fun MarkAttendanceScreen(
    viewModel: TransportAttViewModel,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is TransportAttEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
                else -> {}
            }
        }
    }

    MarkAttendanceScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        navigateToBack = navigateToBack,
    )
}

@Composable
internal fun MarkAttendanceScreenContent(
    uiState: UiState<TransportAttUiState>,
    handleIntent: (TransportAttIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    navigateToBack: () -> Unit,
) {
    EcareProScaffold(
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) (uiState as UiState.Success).data.isLoading else false,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            if (data.showStudentList) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                ) {
                    // Header
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Bus count badge
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.appColors.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = data.busCount.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                                )
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Column {
                                Text(
                                    text = data.selectedRoute?.routeName ?: "",
                                    style = MaterialTheme.appTypography.interSemiBold14px,
                                )
                                Text(
                                    text = "${data.selectedDate} \u00B7 ${tripTypeLabel(data.tripType)}",
                                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                                    color = MaterialTheme.appColors.textSecondary,
                                )
                            }
                        }
                        IconButton(onClick = {
                            handleIntent(TransportAttIntent.OnDismissStudentList)
                            navigateToBack()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Student list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                    ) {
                        itemsIndexed(data.studentList) { index, student ->
                            StudentAttendanceItem(
                                student = student,
                                tripType = data.tripType,
                                freezDrop = data.freezDrop,
                                freezPickup = data.freezPickup,
                                onPresentClick = {
                                    handleIntent(
                                        TransportAttIntent.OnStudentAttendanceChanged(index, TransportConstants.PRESENT)
                                    )
                                },
                                onAbsentClick = {
                                    handleIntent(
                                        TransportAttIntent.OnStudentAttendanceChanged(index, TransportConstants.ABSENT)
                                    )
                                },
                                onLeaveClick = {
                                    handleIntent(
                                        TransportAttIntent.OnStudentAttendanceChanged(index, TransportConstants.OP)
                                    )
                                },
                                onDropClick = {
                                    handleIntent(
                                        TransportAttIntent.OnDropStudentClicked(index)
                                    )
                                },
                            )
                        }
                    }

                    // Save button (not shown for drop trip)
                    if (data.tripType != TransportConstants.DROP_STUDENT_TRIP) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            title = "Mark attendance",
                            onClick = { handleIntent(TransportAttIntent.OnSaveAttendanceClicked) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                        )
                    }
                }

                // Confirm attendance dialog
                if (data.isConfirmDialogVisible) {
                    ConfirmAttendanceDialog(
                        tripType = data.tripType,
                        presentCount = data.presentCount,
                        absentCount = data.absentCount,
                        leaveCount = data.leaveCount,
                        date = data.selectedDate,
                        onDismiss = { handleIntent(TransportAttIntent.OnDismissConfirmDialog) },
                        onConfirm = { handleIntent(TransportAttIntent.OnConfirmSaveAttendance) },
                    )
                }

                // Drop confirm dialog
                if (data.isDropConfirmVisible && data.droppingStudentIndex != null) {
                    val student = data.studentList.getOrNull(data.droppingStudentIndex)
                    if (student != null) {
                        DropConfirmDialog(
                            studentName = student.stName,
                            date = data.selectedDate,
                            onDismiss = {
                                handleIntent(
                                    TransportAttIntent.OnCancelDropStudent(data.droppingStudentIndex)
                                )
                            },
                            onConfirm = {
                                handleIntent(
                                    TransportAttIntent.OnConfirmDropStudent(data.droppingStudentIndex)
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun tripTypeLabel(tripType: Int): String {
    return when (tripType) {
        TransportConstants.UP_TRIP -> "To the school"
        TransportConstants.DOWN_TRIP -> "From the school"
        TransportConstants.DROP_STUDENT_TRIP -> "Drop student"
        else -> ""
    }
}

@Preview
@Composable
private fun MarkAttendanceScreenPreview() {
    EcareProTheme {
        MarkAttendanceScreenContent(
            uiState = UiState.Success(
                TransportAttUiState(
                    showStudentList = true,
                    selectedDate = "09 Feb 2026",
                    selectedRoute = Route(1, "Route A"),
                    tripType = TransportConstants.UP_TRIP,
                    busCount = 3,
                    studentList = listOf(
                        TransportStudent(
                            admissionNo = "ADM001", className = "Class 5A",
                            dropAtt = "", dropStatus = 0, dropTime = "",
                            isConstant = false, isDropped = false, photo = "",
                            pickupAtt = "Present", pickupStatus = TransportConstants.PRESENT,
                            pickupTime = "07:30", rollNo = "1", route = "Route A",
                            stID = 1, stName = "John Doe", stop = "Stop 1", stopID = 1,
                        ),
                        TransportStudent(
                            admissionNo = "ADM002", className = "Class 5B",
                            dropAtt = "", dropStatus = 0, dropTime = "",
                            isConstant = false, isDropped = false, photo = "",
                            pickupAtt = "Absent", pickupStatus = TransportConstants.ABSENT,
                            pickupTime = "", rollNo = "2", route = "Route A",
                            stID = 2, stName = "Jane Smith", stop = "Stop 2", stopID = 2,
                        ),
                    ),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
            navigateToBack = {},
        )
    }
}
