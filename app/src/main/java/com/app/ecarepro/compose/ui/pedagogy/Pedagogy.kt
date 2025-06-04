package com.app.ecarepro.compose.ui.pedagogy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.compose.composable.ECareTopAppBar
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.compose.theme.md_theme_light_primary
import com.app.ecarepro.data.network.model.Pedagogy
import com.app.ecarepro.data.network.model.PedagogyStep
import com.app.ecarepro.data.network.model.UpdateRecord


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedagogyScreen(pedagogyItems: List<Pedagogy>) {
    var items by remember { mutableStateOf(pedagogyItems) }

    Scaffold(
        topBar = {
            ECareTopAppBar(
                title = "Pedagogy",
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.pdgID }) { item ->
                ExpandablePedagogyCard(
                    pedagogyItem = item,
                    onToggleExpand = {
                        items = items.map {
                            if (it.pdgID == item.pdgID) {
                                it.copy(isExpanded = !it.isExpanded)
                            } else {
                                it
                            }
                        }
                    },
                    onEditItem = { },
                    onDeleteItem = { },
                    onEditStep = { step -> },
                    onDeleteStep = { step -> }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PedagogyScreenPreview() {
    val pedagogyItems = listOf(
        Pedagogy(
            name = "Pedagogy 1",
            pdgID = 1,
            steps = listOf(
                PedagogyStep(
                    description = "Description 1",
                    instruction = "Instruction 1",
                    pdgID = 1,
                    pdgStpID = 1,
                    stepName = "Step 1",
                    stepNumber = 1
                ),
                PedagogyStep(
                    description = "Description 2",
                    instruction = "Instruction 2",
                    pdgID = 1,
                    pdgStpID = 2,
                    stepName = "Step 2",
                    stepNumber = 2
                )
            ),
            updateRecord = UpdateRecord(
                createdBy = "User",
                createdOn = "01/01/2023",
                modifiedBy = "User",
                modifiedOn = "01/01/2023"
            ),
            isExpanded = false
        ),
        Pedagogy(
            name = "Pedagogy 2",
            pdgID = 2,
            steps = listOf(
                PedagogyStep(
                    description = "Description 3",
                    instruction = "Instruction 3",
                    pdgID = 2,
                    pdgStpID = 3,
                    stepName = "Step 3",
                    stepNumber = 1
                )
            ),
            updateRecord = UpdateRecord(
                createdBy = "User",
                createdOn = "02/01/2023",
                modifiedBy = "User",
                modifiedOn = "02/01/2023"
            ),
            isExpanded = true
        )
    )
    ECareProTheme {
        PedagogyScreen(pedagogyItems = pedagogyItems)
    }
}

@Composable
fun PedagogyStepRow(
    step: PedagogyStep,
    onEditStep: () -> Unit,
    onDeleteStep: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            ) {
                                append("Step: ")
                            }
                            append(step.stepNumber.toString())
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
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
                },
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Instruction: ")
                    }
                    append(step.instruction)
                },
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        IconButton(onClick = {
        }) {
            Icon(
                Icons.Filled.MoreVert, contentDescription = "More", tint = md_theme_light_primary
            )
        }
    }
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
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    targetValue = if (pedagogyItem.isExpanded) 180f else 0f,
                    label = "ArrowRotation"
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
                            onDeleteStep = { onDeleteStep(step) }
                        )
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
        name = "Sample Pedagogy",
        pdgID = 1,
        steps = listOf(
            PedagogyStep(
                description = "Description 1",
                instruction = "Instruction 1",
                pdgID = 1,
                pdgStpID = 1,
                stepName = "Step 1",
                stepNumber = 1
            ),
            PedagogyStep(
                description = "Description 2",
                instruction = "Instruction 2",
                pdgID = 1,
                pdgStpID = 2,
                stepName = "Step 2",
                stepNumber = 2
            )
        ),
        updateRecord = UpdateRecord(
            createdBy = "Admin",
            createdOn = "2023-01-01",
            modifiedBy = "Admin",
            modifiedOn = "2023-01-01"
        ),
        isExpanded = true
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