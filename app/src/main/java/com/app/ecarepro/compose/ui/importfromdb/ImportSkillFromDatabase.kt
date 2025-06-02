package com.app.ecarepro.compose.ui.importfromdb

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.compose.composable.ECareTopAppBar
import com.app.ecarepro.compose.composable.LoadingComposable
import com.app.ecarepro.compose.composable.LoadingDialog
import com.app.ecarepro.compose.model.LoadState
import com.app.ecarepro.compose.model.messageOrNull
import com.app.ecarepro.data.network.model.MasterCategory
import com.app.ecarepro.data.network.model.Type
import com.app.ecarepro.data.network.model.TypeSkill

@Composable
fun ImportSkillFromDatabase(
    viewModel: ImportSkillFromDatabaseViewModel, onClickBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val loadState by viewModel.loadState.collectAsStateWithLifecycle(initialValue = LoadState.Nothing)
    val snackbarHostState = remember { SnackbarHostState() }

    val message = loadState.messageOrNull()

    LaunchedEffect(loadState) {
        message?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            topBar = {
                ECareTopAppBar(
                    title = "Manage Skill",
                    searchQuery = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onClickBack = onClickBack
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { innerPadding ->
            LoadingComposable(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                uiState = uiState,
            ) { data ->
                ImportSkillScreenContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    data = data,
                    onCategoryClick = viewModel::onCategoryClicked,
                    onSkillCheckedChange = viewModel::onSkillSelectionChanged
                )
            }
        }
        if (loadState.isLoading()) {
            LoadingDialog()
        }
    }
}

@Composable
private fun ImportSkillScreenContent(
    modifier: Modifier = Modifier,
    data: ImportSkillsUiState,
    onCategoryClick: (categoryId: Int?) -> Unit,
    onSkillCheckedChange: (skillId: Int?, isSelected: Boolean) -> Unit,
    onCancelClick: () -> Unit = {},
    onImportClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        SkillsExpandableList(
            modifier = Modifier.weight(1f),
            categories = data.categories,
            expandedCategoryIds = data.expandedCategoryIds,
            selectedSkillIds = data.selectedSkillIds,
            onCategoryClick = onCategoryClick,
            onSkillCheckedChange = onSkillCheckedChange
        )
        SelectedSkillsBottomBar(
            modifier = Modifier,
            selectedSkillNames = data.selectedSkillNames,
            onCancelClick = onCancelClick,
            onImportClick = onImportClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImportSkillScreenContentPreview() {
    val sampleData = ImportSkillsUiState(
        categories = listOf(
            MasterCategory(
                category = "Programming", sklCatID = 1, types = listOf(
                    Type(
                        type = "Backend", sklTypeID = 11, skills = listOf(
                            TypeSkill(skill = "Kotlin", sklID = 101),
                            TypeSkill(skill = "Java", sklID = 102)
                        )
                    )
                )
            )
        ),
        expandedCategoryIds = setOf(1),
        selectedSkillIds = setOf(101),
        selectedSkillNames = listOf("Kotlin")
    )
    ImportSkillScreenContent(
        modifier = Modifier.fillMaxSize(),
        data = sampleData,
        onCategoryClick = {},
        onSkillCheckedChange = { _, _ -> })
}

@Composable
fun SkillsExpandableList(
    modifier: Modifier = Modifier,
    categories: List<MasterCategory>,
    expandedCategoryIds: Set<Int>,
    selectedSkillIds: Set<Int>,
    onCategoryClick: (categoryId: Int?) -> Unit,
    onSkillCheckedChange: (skillId: Int?, isSelected: Boolean) -> Unit
) {

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(
            items = categories,
            key = { category -> category.sklCatID ?: category.hashCode() }) { category ->
            MasterCategoryItem(
                masterCategory = category,
                isExpanded = expandedCategoryIds.contains(category.sklCatID),
                selectedSkillIds = selectedSkillIds,
                onCategoryClick = { onCategoryClick(category.sklCatID) },
                onSkillCheckedChange = { skill, isSelected ->
                    onSkillCheckedChange(skill.sklID, isSelected)
                })
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SkillsExpandableListPreview() {
    val sampleCategories = listOf(
        MasterCategory(
            category = "Programming", sklCatID = 1, types = listOf(
                Type(
                    skills = listOf(
                        TypeSkill(skill = "Kotlin", sklID = 101),
                        TypeSkill(skill = "Java", sklID = 102)
                    ), sklTypeID = 11, type = "Backend"
                )
            )
        ), MasterCategory(
            category = "Programming", sklCatID = 2, types = listOf(
                Type(
                    skills = listOf(
                        TypeSkill(skill = "Kotlin", sklID = 103),
                        TypeSkill(skill = "Java", sklID = 104)
                    ), sklTypeID = 12, type = "Backend"
                )
            )
        )
    )
    SkillsExpandableList(
        categories = sampleCategories,
        expandedCategoryIds = setOf(1),
        selectedSkillIds = setOf(101),
        onCategoryClick = {},
        onSkillCheckedChange = { _, _ -> })
}

@Composable
fun MasterCategoryItem(
    modifier: Modifier = Modifier,
    masterCategory: MasterCategory,
    isExpanded: Boolean,
    selectedSkillIds: Set<Int>,
    onCategoryClick: () -> Unit,
    onSkillCheckedChange: (skill: TypeSkill, isSelected: Boolean) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCategoryClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.KeyboardArrowRight,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = masterCategory.category ?: "Unnamed Category",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp)
            ) {
                masterCategory.types?.forEach { type ->
                    TypeHeader(type = type)
                    type.skills?.forEach { skill ->
                        SkillCheckboxItem(
                            skill = skill,
                            isSelected = skill.sklID?.let { selectedSkillIds.contains(it) }
                                ?: false,
                            onCheckedChange = { isChecked ->
                                onSkillCheckedChange(
                                    skill, isChecked
                                )
                            })
                    }
                }
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
@Preview(showBackground = true)
fun MasterCategoryItemPreview() {
    val sampleMasterCategory = MasterCategory(
        category = "Design", sklCatID = 2, types = listOf(
            Type(
                skills = listOf(
                    TypeSkill(skill = "UI Design", sklID = 201),
                    TypeSkill(skill = "UX Design", sklID = 202)
                ), sklTypeID = 21, type = "Visual"
            )
        )
    )
    MasterCategoryItem(
        masterCategory = sampleMasterCategory,
        isExpanded = true,
        selectedSkillIds = setOf(201),
        onCategoryClick = {},
        onSkillCheckedChange = { _, _ -> })
}

@Composable
fun SkillCheckboxItem(
    modifier: Modifier = Modifier,
    skill: TypeSkill,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isSelected, onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = skill.skill ?: "Unnamed Skill", fontSize = 14.sp)
    }
}

@Composable
@Preview(showBackground = true)
fun SkillCheckboxItemPreview() {
    val sampleSkill = TypeSkill(skill = "Android Development", sklID = 301)
    SkillCheckboxItem(
        skill = sampleSkill, isSelected = true, onCheckedChange = {})
}

@Composable
fun TypeHeader(
    modifier: Modifier = Modifier, type: Type
) {
    Text(
        text = type.type ?: "Unnamed Type",
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun TypeHeaderPreview() {
    val sampleType = Type(
        skills = emptyList(), sklTypeID = 41, type = "Mobile Development"
    )
    TypeHeader(type = sampleType)
}

@Composable
fun SelectedSkillsBottomBar(
    modifier: Modifier = Modifier,
    selectedSkillNames: List<String>,
    onCancelClick: () -> Unit,
    onImportClick: () -> Unit
) {

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Selected Skills:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedCard( // Card to contain the selected skills text
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 70.dp), // Minimum height for the text area
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    contentAlignment = if (selectedSkillNames.isEmpty()) Alignment.Center else Alignment.TopStart
                ) {
                    if (selectedSkillNames.isEmpty()) {
                        Text(
                            text = "No skills selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = selectedSkillNames.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3 // Limit lines if many skills are selected, consider making it scrollable if needed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onImportClick,
                    enabled = selectedSkillNames.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text("Import Skills")
                }
            }
        }
    }
}


