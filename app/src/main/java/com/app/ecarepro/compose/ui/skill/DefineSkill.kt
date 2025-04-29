package com.app.ecarepro.compose.ui.skill

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.ecarepro.R
import com.app.ecarepro.compose.composable.LoadingComposable
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefineSkillScreen(
    viewModel: DefineSkillViewModel = viewModel(),
    onClickBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { DefineSkillTopAppBar(onClickBack) }
    ) { innerPadding ->

        LoadingComposable(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            onRetry = { viewModel.retry() }
        ) { data ->
            DefineSkillContent(
                modifier = Modifier.padding(innerPadding),
                data = data,
                onCategorySelected = viewModel::onCategorySelected,
                onSkillDelete = viewModel::deleteSkill,
                onClickCreate = {
                    // TODO("implement the action of the button Create new skill")
                },
                onClickImportFromDatabase = {
                    // TODO("implement the action of the button Import From Database")
                }
            )
        }
    }
}


/**
 * Composable that represents the content of the Define Skill screen.
 *
 * @param data The data to display in the content area.
 * @param onCategorySelected Callback when a skill category is selected.
 * @param onSkillDelete Callback when a skill item is deleted.
 * @param onClickCreate Callback when the "Create New Skill" button is clicked.
 * @param onClickImportFromDatabase Callback when the "Import From Database" button is clicked.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefineSkillContent(
    modifier: Modifier = Modifier,
    data: DefineSkillSuccessData,
    onCategorySelected: (Int) -> Unit,
    onSkillDelete: (Skill) -> Unit,
    onClickCreate: () -> Unit,
    onClickImportFromDatabase: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        stickyHeader {
            DefineSkillHeader(
                data = data,
                onCategorySelected = onCategorySelected,
                onClickCreate = onClickCreate,
                onClickImportFromDatabase = onClickImportFromDatabase
            )
        }

        if (data.skills.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = "No Skill found for this category: \"${data.selectedSkillCategory?.category}\"" + if (data.searchQuery.isNotEmpty()) " and search query \"${data.searchQuery}\"" else "",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            itemsIndexed(data.skills) { index, skill ->
                SkillItem(
                    skill = skill,
                    index = index + 1,
                    onClickEdit = {
                        // TODO("handle edit action")
                    },
                    onClickDelete = {
                        onSkillDelete(skill)
                    }
                )
            }
        }
    }
}

@Composable
private fun DefineSkillHeader(
    data: DefineSkillSuccessData,
    onCategorySelected: (Int) -> Unit,
    onClickCreate: () -> Unit,
    onClickImportFromDatabase: () -> Unit
) {
    Surface {
        Column {
            SkillCategorySelectionDropdown(
                selectedSkill = data.selectedSkillCategory?.category.orEmpty(),
                onSkillSelected = { selectedCategory ->
                    onCategorySelected(
                        data.skillCategory.first { it.category == selectedCategory }.sklCatID
                    )
                },
                skills = data.skillCategory.map { it.category.orEmpty() }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CreateImportButtons(
                onClickCreate = onClickCreate,
                onClickImportFromDatabase = onClickImportFromDatabase
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefineSkillTopAppBar(
    onClickBack: () -> Unit = {}
) {
    TopAppBar(
        title = { Text("Define Skill") },
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* Handle search action */ }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefineSkillTopAppBarPreview() {
    DefineSkillTopAppBar()
}

/**
 * Composable that displays a dropdown to select a skill category.
 *
 * @param selectedSkill The currently selected skill category.
 * @param onSkillSelected Callback function when a skill category is selected.
 * @param skills List of available skill categories to display in the dropdown.
 */
@Composable
fun SkillCategorySelectionDropdown(
    selectedSkill: String,
    onSkillSelected: (String) -> Unit,
    skills: List<String>
) {

    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            value = selectedSkill,
            onValueChange = { /* Not editable here */ },

            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    expanded = focusState.isFocused
                },
            label = { Text("Select Skill") },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            trailingIcon = {
                AnimatedContent(
                    targetState = expanded,
                    transitionSpec = {
                        if (targetState) {
                            (slideInVertically { height -> height } + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )) togetherWith
                                    slideOutVertically { height -> -height } + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )
                        } else {
                            (slideInVertically { height -> -height } + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )) togetherWith
                                    slideOutVertically { height -> height } + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )
                        }
                    },
                    label = "Animated Icon"
                ) { targetExpanded ->
                    if (targetExpanded) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Open")
                    } else {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Close")
                    }
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                focusManager.clearFocus()
                expanded = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            skills.forEach { skill ->
                DropdownMenuItem(
                    text = { Text(skill) },
                    onClick = {
                        onSkillSelected(skill)
                        expanded = false
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillSelectionDropdownPreview() {
    var selectedSkill by remember { mutableStateOf("Cognitive and Creative Skills") }
    val skills = listOf(
        "Cognitive and Creative Skills",
        "Technical Skills",
        "Soft Skills"
    )
    SkillCategorySelectionDropdown(
        selectedSkill,
        { selectedSkill = it },
        skills
    )
}

/**
 * Composable that displays the buttons for creating a new skill or importing from the database.
 *
 * @param onClickCreate Callback when the "Create New Skill" button is clicked.
 * @param onClickImportFromDatabase Callback when the "Import From Database" button is clicked.
 */
@Composable
fun CreateImportButtons(
    onClickCreate: () -> Unit = {},
    onClickImportFromDatabase: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(
            modifier = Modifier
                .padding(end = 4.dp),
            onClick = onClickCreate,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(
                "Create new skill",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
        OutlinedButton(
            modifier = Modifier
                .padding(start = 4.dp),
            onClick = onClickImportFromDatabase,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFF4CAF50))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_file_download_24),
                    contentDescription = "Search",
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Import from DB",
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateImportButtonsPreview() {
    CreateImportButtons()
}

/**
 * Composable that displays a single skill item in a list.
 *
 * @param skill The skill data to display.
 * @param index The index of the skill in the list.
 * @param onClickEdit Callback when the "Edit" icon is clicked.
 * @param onClickDelete Callback when the "Delete" icon is clicked.
 */
@Composable
fun SkillItem(
    skill: Skill,
    index: Int,
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray.copy(alpha = 0.3f), // Background color
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("$index. ${skill.category}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Skill Type: ${skill.type}")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Skill Name: ${skill.skill}")
            }
            IconButton(onClick = onClickEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color(0xFF1976D2))
            }
            IconButton(onClick = onClickDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillItemPreview() {
    val dummySkill = Skill(
        category = "Cognitive and Creative Skills",
        createdBy = "John Doe",
        createdOn = "2023-09-01",
        id = "1",
        modifiedBy = "Jane Smith",
        modifiedOn = "2023-09-02",
        skill = "Analysis",
        sklCatID = 1,
        sklTypeID = 1,
        type = "Critical thinking"
    )
    SkillItem(dummySkill, 1)
}


@Preview(showBackground = true)
@Composable
fun DefineSkillContentPreview() {
    // create a mock data to test the preview
    val dummySkills = listOf(
        Skill(
            category = "Cognitive and Creative Skills",
            createdBy = "John Doe",
            createdOn = "2023-09-01",
            id = "1",
            modifiedBy = "Jane Smith",
            modifiedOn = "2023-09-02",
            skill = "Analysis",
            sklCatID = 1,
            sklTypeID = 1,
            type = "Critical thinking"
        ),
        Skill(
            category = "Cognitive and Creative Skills",
            createdBy = "John Doe",
            createdOn = "2023-09-01",
            id = "2",
            modifiedBy = "Jane Smith",
            modifiedOn = "2023-09-02",
            skill = "Problem Solving",
            sklCatID = 1,
            sklTypeID = 2,
            type = "Critical thinking"
        ),
    )

    val dummySkillCategories = listOf(
        Category(category = "Cognitive and Creative Skills", sklCatID = 1),
        Category(category = "Technical Skills", sklCatID = 2),
        Category(category = "Soft Skills", sklCatID = 3),
    )
    val dummyUiState = DefineSkillSuccessData(
        skills = dummySkills,
        skillCategory = dummySkillCategories,
        selectedSkillCategory = dummySkillCategories.first()
    )
    DefineSkillContent(
        data = dummyUiState,
        onCategorySelected = {},
        onSkillDelete = {},
        onClickCreate = {},
        onClickImportFromDatabase = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DefineSkillScreenPreview() {
    DefineSkillScreen()
}