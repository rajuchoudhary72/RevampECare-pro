package com.app.ecarepro.feature.transport_att.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.Stop
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProDatePicker
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.transport_att.DropdownSelector
import com.app.ecarepro.feature.transport_att.components.RouteBottomSheet
import com.app.ecarepro.feature.transport_att.components.StudentReportItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAttendanceScreen(
    viewModel: ViewAttendanceViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                ViewAttendanceEvent.NavigateBack -> navigateToBack()
                is ViewAttendanceEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    ViewAttendanceScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewAttendanceScreenContent(
    uiState: UiState<ViewAttendanceUiState>,
    handleIntent: (ViewAttendanceIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "View Attendance",
                onNavigationClicked = { handleIntent(ViewAttendanceIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success)
            (uiState as UiState.Success).data.isLoading else false,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            if (data.showReport) {
                // Report view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.appColors.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = data.reportData.sumOf { it.students.size }.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = data.selectedRoute?.routeName ?: "",
                                    style = MaterialTheme.appTypography.interSemiBold14px,
                                )
                                Text(
                                    text = "${data.selectedDate} \u00B7 To the school",
                                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                                    color = MaterialTheme.appColors.textSecondary,
                                )
                            }
                        }
                        IconButton(onClick = { handleIntent(ViewAttendanceIntent.DismissReportView) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        data.reportData.forEach { stop ->
                            // Stop header
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFFEF5350)),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stop.stopName,
                                        style = MaterialTheme.appTypography.interSemiBold14px,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = "PICKUP",
                                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 11.sp),
                                        color = MaterialTheme.appColors.textSecondary,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "DROP",
                                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 11.sp),
                                        color = MaterialTheme.appColors.textSecondary,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }

                            // Students under this stop
                            items(stop.students) { student ->
                                StudentReportItem(student = student)
                            }
                        }
                    }
                }
            } else {
                // Selection view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Date selector
                    Text(
                        text = "Select date",
                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        text = data.selectedDate.ifEmpty { "Select date" },
                        onClick = { handleIntent(ViewAttendanceIntent.ShowDatePicker) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Route selector
                    Text(
                        text = "Select route",
                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        text = data.selectedRoute?.routeName ?: "Select route",
                        onClick = { handleIntent(ViewAttendanceIntent.ShowRouteSheet) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stop list
                    if (data.stopList.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = data.selectAllStops,
                                onCheckedChange = { handleIntent(ViewAttendanceIntent.OnSelectAllStops) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.appColors.primary,
                                ),
                            )
                            Text(
                                text = "Select stop",
                                style = MaterialTheme.appTypography.interRegular14px,
                                color = MaterialTheme.appColors.textSecondary,
                            )
                        }

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(data.stopList) { stop ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { handleIntent(ViewAttendanceIntent.OnStopToggled(stop)) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(
                                        checked = stop.checked,
                                        onCheckedChange = { handleIntent(ViewAttendanceIntent.OnStopToggled(stop)) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.appColors.primary,
                                        ),
                                    )
                                    Text(
                                        text = stop.stopName,
                                        style = MaterialTheme.appTypography.interRegular14px,
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Fetch button
                    Button(
                        title = "View attendance",
                        onClick = { handleIntent(ViewAttendanceIntent.OnFetchReport) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                    )
                }

                // Date Picker
                EcareProDatePicker(
                    isVisible = data.isDatePickerVisible,
                    onDismiss = { handleIntent(ViewAttendanceIntent.DismissDatePicker) },
                    onDateSelected = { dateStr ->
                        try {
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val date = sdf.parse(dateStr)
                            date?.let {
                                handleIntent(ViewAttendanceIntent.OnDateSelected(it.time))
                            }
                        } catch (_: Exception) {}
                    },
                )

                // Route bottom sheet
                if (data.isRouteSheetVisible) {
                    RouteBottomSheet(
                        routes = data.routeList,
                        selectedRoute = data.selectedRoute,
                        onRouteSelected = { handleIntent(ViewAttendanceIntent.OnRouteSelected(it)) },
                        onDismiss = { handleIntent(ViewAttendanceIntent.DismissRouteSheet) },
                    )
                }
            }
        }
    }
}

private val previewStudents = listOf(
    TransportStudent(
        admissionNo = "6736", className = "4-C", dropAtt = "Absent", dropStatus = 0,
        dropTime = "", isConstant = false, isDropped = false, photo = "",
        pickupAtt = "Present", pickupStatus = 1, pickupTime = "02:13 PM",
        rollNo = "20", route = null, stID = 2805, stName = "VIDAAD KHAN",
        stop = "Kashipur", stopID = 484,
    ),
    TransportStudent(
        admissionNo = "7039", className = "3-A", dropAtt = "Absent", dropStatus = 0,
        dropTime = "", isConstant = false, isDropped = false, photo = "",
        pickupAtt = "Present", pickupStatus = 1, pickupTime = "02:13 PM",
        rollNo = "8", route = null, stID = 3062, stName = "ATHARV SINGH",
        stop = "Kashipur", stopID = 484,
    ),
    TransportStudent(
        admissionNo = "8000", className = "6-D", dropAtt = "Absent", dropStatus = 0,
        dropTime = "", isConstant = false, isDropped = false, photo = "",
        pickupAtt = "Absent", pickupStatus = 0, pickupTime = "",
        rollNo = "8", route = null, stID = 3129, stName = "DAKSHITH SINGH SAINI",
        stop = "Corbett Sun City", stopID = 483,
    ),
)

@Preview(showBackground = true)
@Composable
private fun ViewAttendanceSelectionPreview() {
    EcareProTheme {
        ViewAttendanceScreenContent(
            uiState = UiState.Success(
                ViewAttendanceUiState(
                    selectedDate = "13 Feb 2026",
                    routeList = listOf(
                        Route(1, "Route A"),
                        Route(2, "Route B"),
                    ),
                    stopList = listOf(
                        Stop(1, "Corbett Sun City", checked = true),
                        Stop(2, "Kashipur", checked = false),
                    ),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ViewAttendanceReportPreview() {
    EcareProTheme {
        ViewAttendanceScreenContent(
            uiState = UiState.Success(
                ViewAttendanceUiState(
                    selectedDate = "13 Feb 2026",
                    selectedRoute = Route(239, "Route 1 - Kashipur"),
                    showReport = true,
                    reportData = listOf(
                        Stop(
                            stopID = 483,
                            stopName = "Corbett Sun City",
                            students = listOf(previewStudents[2]),
                        ),
                        Stop(
                            stopID = 484,
                            stopName = "Kashipur",
                            students = listOf(previewStudents[0], previewStudents[1]),
                        ),
                    ),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}
