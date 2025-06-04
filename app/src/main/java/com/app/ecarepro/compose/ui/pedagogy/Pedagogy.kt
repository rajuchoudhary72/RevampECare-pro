package com.app.ecarepro.compose.ui.pedagogy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.ecarepro.R
import com.app.ecarepro.compose.composable.AnchoredDraggableBox
import com.app.ecarepro.compose.composable.DragAnchors
import com.app.ecarepro.compose.composable.ECareTopAppBar
import com.app.ecarepro.compose.composable.LoadingComposable
import com.app.ecarepro.compose.composable.LoadingDialog
import com.app.ecarepro.compose.model.LoadState
import com.app.ecarepro.compose.model.messageOrNull
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.compose.theme.md_theme_light_primary
import com.app.ecarepro.data.network.model.Pedagogy
import com.app.ecarepro.data.network.model.PedagogyStep
import com.app.ecarepro.data.network.model.UpdateRecord


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedagogyScreen(
    viewModel: PedagogyViewModel = viewModel(), onClickBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val loadState by viewModel.loadState.collectAsStateWithLifecycle(initialValue = LoadState.Nothing)
    val snackbarHostState = remember { SnackbarHostState() }

    var pedagogyActions by remember { mutableStateOf<PedagogyActions?>(null) }

    val message = loadState.messageOrNull()

    LaunchedEffect(loadState) {
        message?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(topBar = {
            ECareTopAppBar(
                title = "Pedagogy",
                searchQuery = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onClickBack = onClickBack
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    pedagogyActions = PedagogyActions.AddPedagogy
                }) {
                Icon(
                    Icons.Filled.Add, contentDescription = "Add"
                )
            }
        }, snackbarHost = {
            SnackbarHost(snackbarHostState)
        }) { innerPadding ->
            LoadingComposable(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                uiState = uiState,
                onRetry = { viewModel.refresh() }) { data ->
                PedagogyScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    data = data,
                    onToggleExpand = viewModel::toggleExpand,
                    onEditItem = {},
                    onDeleteItem = {
                        pedagogyActions = PedagogyActions.DeletePedagogy(it)
                    },
                    onEditStep = {},
                    onDeleteStep = {
                        pedagogyActions = PedagogyActions.DeletePedagogyStep(it)
                    }

                )
            }
        }
        if (loadState.isLoading()) {
            LoadingDialog()
        }

        pedagogyActions?.let { action ->
            when (action) {
                PedagogyActions.AddPedagogy -> {}
                else -> {
                    AlertDialog(
                        onDismissRequest = { pedagogyActions = null },
                        title = { Text(stringResource(R.string.confirm_delete)) },
                        text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete)) },
                        confirmButton = {
                            TextButton(onClick = {
                                if (action is PedagogyActions.DeletePedagogy) {
                                    viewModel.deletePedagogy(action.pedagogy.pdgID)
                                } else if (action is PedagogyActions.DeletePedagogyStep) {
                                    viewModel.deletePedagogyStep(action.pedagogyStep.pdgStpID)
                                }
                                pedagogyActions = null
                            }) { Text(stringResource(R.string.delete)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { pedagogyActions = null }) {
                                Text(
                                    stringResource(R.string.cancel)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedagogyScreenContent(
    modifier: Modifier,
    data: PedagogyUiState,
    onToggleExpand: (Pedagogy) -> Unit,
    onEditItem: (Pedagogy) -> Unit,
    onDeleteItem: (Pedagogy) -> Unit,
    onEditStep: (PedagogyStep) -> Unit,
    onDeleteStep: (PedagogyStep) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(
            items = data.pedagogies, key = { item: Pedagogy -> item.pdgID }) { item: Pedagogy ->
            ExpandablePedagogyCard(
                pedagogyItem = item,
                onToggleExpand = { onToggleExpand(item) },
                onEditItem = { onEditItem(item) },
                onDeleteItem = { onDeleteItem(item) },
                onEditStep = onEditStep,
                onDeleteStep = onDeleteStep
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PedagogyScreenPreview() {
    val pedagogyItems = listOf(
        Pedagogy(
            name = "Pedagogy 1", pdgID = 1, steps = listOf(
                PedagogyStep(
                    description = "Description 1",
                    instruction = "Instruction 1",
                    pdgID = 1,
                    pdgStpID = 1,
                    stepName = "Step 1",
                    stepNumber = 1
                ), PedagogyStep(
                    description = "Description 2",
                    instruction = "Instruction 2",
                    pdgID = 1,
                    pdgStpID = 2,
                    stepName = "Step 2",
                    stepNumber = 2
                )
            ), updateRecord = UpdateRecord(
                createdBy = "User",
                createdOn = "01/01/2023",
                modifiedBy = "User",
                modifiedOn = "01/01/2023"
            ), isExpanded = false
        ), Pedagogy(
            name = "Pedagogy 2", pdgID = 2, steps = listOf(
                PedagogyStep(
                    description = "Description 3",
                    instruction = "Instruction 3",
                    pdgID = 2,
                    pdgStpID = 3,
                    stepName = "Step 3",
                    stepNumber = 1
                )
            ), updateRecord = UpdateRecord(
                createdBy = "User",
                createdOn = "02/01/2023",
                modifiedBy = "User",
                modifiedOn = "02/01/2023"
            ), isExpanded = true
        )
    )
    ECareProTheme {
        PedagogyScreenContent(
            modifier = Modifier.fillMaxSize(),
            data = PedagogyUiState(pedagogies = pedagogyItems),
            onToggleExpand = {},
            onEditItem = {},
            onDeleteItem = {},
            onEditStep = {},
            onDeleteStep = {})
    }
}

@Composable
fun PedagogyStepRow(
    step: PedagogyStep,
    onEditStep: () -> Unit,
    onDeleteStep: () -> Unit
) {

    var dragAnchors by remember { mutableStateOf(DragAnchors.Start) }
    AnchoredDraggableBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        dragAnchors = dragAnchors,
        firstContent = { modifier ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Step Number and Name on one line
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f) // Allow this to take available space
                        ) {
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold, fontSize = 14.sp
                                        )
                                    ) {
                                        append("Step: ")
                                    }
                                    append(step.stepNumber.toString())
                                })
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold, fontSize = 14.sp
                                        )
                                    ) {
                                        append("Name: ")
                                    }
                                    append(step.stepName)
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Description: ")
                            }
                            append(step.description)
                        }, fontSize = 14.sp, style = MaterialTheme.typography.bodyMedium,  maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Instruction: ")
                            }
                            append(step.instruction)
                        }, fontSize = 14.sp, style = MaterialTheme.typography.bodyMedium,  maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                IconButton(onClick = {
                    dragAnchors =
                        if (dragAnchors == DragAnchors.Start) DragAnchors.End else DragAnchors.Start
                }) {
                    Icon(
                        Icons.Filled.MoreVert, contentDescription = "More", tint = md_theme_light_primary
                    )
                }
            }
        },
        secondContent = {  modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .height(85.dp)
                        .width(70.dp)
                        .background(Color.Blue)
                        .clickable(onClick = onEditStep),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Edit", color = Color.White)
                }
                Column(
                    modifier = Modifier
                        .height(85.dp)
                        .width(70.dp)
                        .background(Color.Red)
                        .clickable(onClick = onDeleteStep),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Delete", color = Color.White)
                }
            }
        }
    )


}

@Preview(showBackground = true)
@Composable
fun PedagogyStepRowPreview() {
    val step = PedagogyStep(
        description = "This is a detailed description of the step.",
        instruction = "Follow these instructions carefully.",
        pdgID = 1,
        pdgStpID = 1,
        stepName = "Introduction Step",
        stepNumber = 1
    )
    ECareProTheme {
        PedagogyStepRow(step = step, onEditStep = {}, onDeleteStep = {})
    }
}

@Composable
fun ExpandablePedagogyCard(
    pedagogyItem: Pedagogy,
    onToggleExpand: () -> Unit,
    onEditItem: () -> Unit,
    onDeleteItem: () -> Unit,
    onEditStep: (PedagogyStep) -> Unit,
    onDeleteStep: (PedagogyStep) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pedagogyItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditItem) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Pedagogy")
                }
                IconButton(onClick = onDeleteItem) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Pedagogy",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                val rotationAngle by animateFloatAsState(
                    targetValue = if (pedagogyItem.isExpanded) 180f else 0f, label = "ArrowRotation"
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (pedagogyItem.isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            AnimatedVisibility(visible = pedagogyItem.isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    pedagogyItem.steps.forEach { step ->
                        PedagogyStepRow(
                            step = step,
                            onEditStep = { onEditStep(step) },
                            onDeleteStep = { onDeleteStep(step) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandablePedagogyCardPreview() {
    val pedagogyItem = Pedagogy(
        name = "Sample Pedagogy", pdgID = 1, steps = listOf(
            PedagogyStep(
                description = "Research & Inquiry",
                instruction = "Students gather information, explore concepts, and analyze data.",
                pdgID = 1,
                pdgStpID = 1,
                stepName = "Research & Inquiry",
                stepNumber = 1
            ), PedagogyStep(
                description = "Plan & Design",
                instruction = "Develop a strategy or prototype to address the problem.",
                pdgID = 1,
                pdgStpID = 2,
                stepName = "Plan & Design",
                stepNumber = 2
            )
        ), updateRecord = UpdateRecord(
            createdBy = "Admin",
            createdOn = "2023-01-01",
            modifiedBy = "Admin",
            modifiedOn = "2023-01-01"
        ), isExpanded = true
    )
    ECareProTheme {
        ExpandablePedagogyCard(
            pedagogyItem = pedagogyItem,
            onToggleExpand = {},
            onEditItem = {},
            onDeleteItem = {},
            onEditStep = {},
            onDeleteStep = {})
    }
}

sealed interface PedagogyActions {
    data object AddPedagogy : PedagogyActions
    data class DeletePedagogy(val pedagogy: Pedagogy) : PedagogyActions
    data class DeletePedagogyStep(val pedagogyStep: PedagogyStep) : PedagogyActions
}