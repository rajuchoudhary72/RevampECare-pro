package com.app.ecarepro.feature.transport_att

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.transport.TransportConstants
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProDatePicker
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.transport_att.components.RouteBottomSheet
import com.app.ecarepro.feature.transport_att.components.TripTypeBottomSheet
import com.app.ecarepro.feature.transport_att.screens.MarkAttendanceScreenContent

@Composable
fun TransportAttScreen(
    viewModel: TransportAttViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToViewAttendance: () -> Unit,
    navigateToOutPass: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                TransportAttEvent.NavigateBack -> navigateToBack()
                TransportAttEvent.NavigateToViewAttendance -> navigateToViewAttendance()
                TransportAttEvent.NavigateToOutPass -> navigateToOutPass()
                is TransportAttEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    TransportAttScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransportAttScreenContent(
    uiState: com.app.ecarepro.core.ui.UiState<TransportAttUiState>,
    handleIntent: (TransportAttIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    val showStudentList = (uiState as? com.app.ecarepro.core.ui.UiState.Success)?.data?.showStudentList == true

    if (showStudentList) {
        MarkAttendanceScreenContent(
            uiState = uiState,
            handleIntent = handleIntent,
            snackbarHostState = snackbarHostState,
            snackbarMessage = snackbarMessage,
            onSnackbarDismissed = onSnackbarDismissed,
            navigateToBack = { handleIntent(TransportAttIntent.OnDismissStudentList) },
        )
        return
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Transport attendance",
                onNavigationClicked = { handleIntent(TransportAttIntent.OnBackClicked) },
                actions = {
                    TextButton(onClick = { handleIntent(TransportAttIntent.OnViewOutPassClicked) }) {
                        Text(
                            text = "View Out Pass",
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = MaterialTheme.appColors.primary,
                        )
                    }
                },
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is com.app.ecarepro.core.ui.UiState.Success) uiState.data.isLoading else false,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Date & Trip type row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Date selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Select date of attendance",
                            style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                            color = MaterialTheme.appColors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        DropdownSelector(
                            text = data.selectedDate.ifEmpty { "Select date" },
                            onClick = { handleIntent(TransportAttIntent.ShowDatePicker) },
                        )
                    }

                    // Trip type selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Select trip type",
                            style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                            color = MaterialTheme.appColors.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        DropdownSelector(
                            text = tripTypeLabel(data.tripType),
                            onClick = { handleIntent(TransportAttIntent.ShowTripTypeSheet) },
                        )
                    }
                }

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
                    onClick = { handleIntent(TransportAttIntent.ShowRouteSheet) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stop list
                if (data.stopList.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (data.tripType != TransportConstants.DROP_STUDENT_TRIP) {
                            Checkbox(
                                checked = data.selectAllStops,
                                onCheckedChange = { handleIntent(TransportAttIntent.OnSelectAllStops) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.appColors.primary,
                                ),
                            )
                        }
                        Text(
                            text = "Select stop",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                    ) {
                        items(data.stopList) { stop ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { handleIntent(TransportAttIntent.OnStopToggled(stop)) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = stop.checked,
                                    onCheckedChange = { handleIntent(TransportAttIntent.OnStopToggled(stop)) },
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

                // Bottom buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        title = "Mark attendance",
                        onClick = {
                            handleIntent(TransportAttIntent.OnMarkAttendanceClicked)
                            // Navigation will happen after student list is loaded via the screen
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    androidx.compose.material3.OutlinedButton(
                        onClick = { handleIntent(TransportAttIntent.OnViewAttendanceClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.appColors.primary),
                    ) {
                        Text(
                            text = "View attendance",
                            color = MaterialTheme.appColors.primary,
                        )
                    }
                }
            }

            // Date Picker
            EcareProDatePicker(
                isVisible = data.isDatePickerVisible,
                onDismiss = { handleIntent(TransportAttIntent.DismissDatePicker) },
                onDateSelected = { dateStr ->
                    try {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val date = sdf.parse(dateStr)
                        date?.let {
                            handleIntent(TransportAttIntent.OnDateSelected(it.time))
                        }
                    } catch (_: Exception) {}
                },
            )

            // Trip type bottom sheet
            if (data.isTripTypeSheetVisible) {
                TripTypeBottomSheet(
                    selectedTripType = data.tripType,
                    onTripTypeSelected = { handleIntent(TransportAttIntent.OnTripTypeSelected(it)) },
                    onDismiss = { handleIntent(TransportAttIntent.DismissTripTypeSheet) },
                )
            }

            // Route bottom sheet
            if (data.isRouteSheetVisible) {
                RouteBottomSheet(
                    routes = data.routeList,
                    selectedRoute = data.selectedRoute,
                    onRouteSelected = { handleIntent(TransportAttIntent.OnRouteSelected(it)) },
                    onDismiss = { handleIntent(TransportAttIntent.DismissRouteSheet) },
                )
            }
        }
    }
}

@Composable
fun DropdownSelector(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

private fun tripTypeLabel(tripType: Int): String {
    return when (tripType) {
        TransportConstants.UP_TRIP -> "To the school"
        TransportConstants.DOWN_TRIP -> "From the school"
        TransportConstants.DROP_STUDENT_TRIP -> "Drop student"
        else -> "Select trip type"
    }
}

@Preview
@Composable
private fun TransportAttScreenPreview() {
    EcareProTheme {
        TransportAttScreenContent(
            uiState = com.app.ecarepro.core.ui.UiState.Success(
                TransportAttUiState(selectedDate = "09 Feb 2026")
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}
