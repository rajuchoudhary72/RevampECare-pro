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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.R
import com.app.ecarepro.compose.composable.ECareExposedDropdownMenuBox
import com.app.ecarepro.compose.composable.ECareTopAppBar
import com.app.ecarepro.compose.composable.ItemDropdown
import com.app.ecarepro.compose.composable.LoadingComposable
import com.app.ecarepro.compose.composable.LoadingDialog
import com.app.ecarepro.compose.composable.SkillItem
import com.app.ecarepro.compose.model.LoadState
import com.app.ecarepro.compose.model.messageOrNull
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.data.network.model.SkillType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefineSkillScreen(
    viewModel: DefineSkillViewModel,
    onClickBack: () -> Unit = {},
    onClickManageSkill: () -> Unit = {},

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val loadState by viewModel.loadState.collectAsStateWithLifecycle(LoadState.Nothing)
    val snackbarHostState = remember { SnackbarHostState() }

    val message = loadState.messageOrNull()
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    var skillToEdit by remember { mutableStateOf<Skill?>(null) }
    val skillsTypes by viewModel.skillTypes.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            topBar = {
                ECareTopAppBar(
                    title = "Define Skill",
                    searchQuery = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onClickBack = onClickBack
                )
            },

            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }) { innerPadding ->
            LoadingComposable(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onRetry = { viewModel.refresh() }) { data ->
                DefineSkillContent(
                    modifier = Modifier.padding(innerPadding),
                    data = data,
                    onCategorySelected = viewModel::onCategorySelected,
                    onSkillDelete = viewModel::deleteSkill,
                    onClickCreate = {
                        skillToEdit = null
                        showBottomSheet = true
                    },
                    editSkillRequest = { skill ->
                        skillToEdit = skill
                        viewModel.loadSkillTypes(skill.sklCatID)
                        showBottomSheet = true
                    },
                    onClickImportFromDatabase = {},
                    onClickManageSkill = onClickManageSkill,
                )

                if (showBottomSheet) {
                    CreateSkillBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        categories = data.skillCategory,
                        skillToEdit = skillToEdit,
                        skillTypes = skillsTypes,
                        skillTypesLoadRequest = viewModel::loadSkillTypes,
                        onSaveSkill = { id: String?, skill: String, sklCatID: String, sklTypeID: String ->
                            showBottomSheet = false
                            viewModel.saveSkill(
                                id = id, skill = skill, sklCatID = sklCatID, sklTypeID = sklTypeID
                            )
                        },
                        onCancel = { showBottomSheet = false })
                }
            }
        }

        if (loadState.isLoading()) {
            LoadingDialog()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSkillBottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    skillToEdit: Skill? = null,
    categories: List<Category>,
    skillTypes: List<SkillType>,
    skillTypesLoadRequest: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveSkill: (id: String?, skill: String, sklCatID: String, sklTypeID: String) -> Unit,
    onCancel: () -> Unit
) {
    var category by remember { mutableStateOf(skillToEdit?.category ?: "") }
    var type by remember { mutableStateOf(skillToEdit?.type ?: "") }
    var skillName by remember { mutableStateOf(skillToEdit?.skill ?: "") }

    val categoriesItem = categories.map { ItemDropdown(it.sklCatID, it.category.orEmpty()) }
    val typesItems = skillTypes.map { ItemDropdown(it.sklTypeID, it.type.orEmpty()) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.create_new_skill),
                style = MaterialTheme.typography.headlineSmall,
            )

            // Category Dropdown
            ECareExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                value = category,
                onValueChange = { value ->
                    category = value.name
                    skillTypesLoadRequest(value.id)
                },
                label = stringResource(R.string.select_a_category),
                items = categoriesItem,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Type Dropdown
            ECareExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                value = type,
                onValueChange = { type = it.name },
                label = "Select a Type",
                items = typesItems,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Skill Name Text Field
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = skillName,
                onValueChange = { skillName = it },
                label = { Text(stringResource(R.string.enter_skill_name)) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSaveSkill(
                            skillToEdit?.id,
                            skillName,
                            categories.first { it.category == category }.sklCatID,
                            skillTypes.first { it.type == type }.sklTypeID,

                            )
                    },
                ) {
                    Text(stringResource(R.string.save_skill))
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onCancel,
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
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
    onCategorySelected: (String) -> Unit,
    onSkillDelete: (Skill) -> Unit,
    editSkillRequest: (Skill) -> Unit,
    onClickCreate: () -> Unit,
    onClickImportFromDatabase: () -> Unit,
    onClickManageSkill: () -> Unit,
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
                onClickImportFromDatabase = onClickImportFromDatabase,
                onClickManageSkill = onClickManageSkill
            )
        }

        if (data.skills.isEmpty()) {
            item {
                NoSkillFoundText(
                    selectedCategory = data.selectedSkillCategory?.category.orEmpty(),
                    searchQuery = data.searchQuery
                )
            }
        } else {
            itemsIndexed(
                items = data.skills,
                key = { _, skill -> skill.id }
            ) { index, skill ->
                SkillItem(
                    id = index + 1,
                    title = skill.category.orEmpty(),
                    description = skill.skill + " | " + skill.type,
                    onClickEdit = {
                        editSkillRequest(skill)
                    }, onClickDelete = {
                        onSkillDelete(skill)
                    })
            }
        }
    }
}

/**
 * Composable that displays a message when no skills are found.
 *
 * This composable shows a text indicating that no skills were found for the given
 * selected category and/or search query. It highlights the category and query in bold.
 *
 * @param selectedCategory The category for which no skills were found.
 * @param searchQuery The search query for which no skills were found.
 */
@Composable
private fun NoSkillFoundText(
    selectedCategory: String?,
    searchQuery: String?,
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center

    ) {
        Text(
            modifier = Modifier.padding(16.dp), text = buildAnnotatedString {
                append(stringResource(R.string.no_skill_found_for_this_category))
                append(selectedCategory)
                addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold),
                    start = length - (selectedCategory?.length ?: 0) - 2,
                    end = length
                )

                if (!searchQuery.isNullOrEmpty()) {
                    append(stringResource(R.string.and_search_query))
                    append(searchQuery)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start = length - searchQuery.length - 2,
                        end = length
                    )
                }
            }, textAlign = TextAlign.Center
        )
    }

}


/**
 * Composable that displays the header section for defining skills.
 *
 * This header includes a dropdown for selecting a skill category and buttons
 * for creating a new skill or importing skills from a database.
 *
 * @param data The data containing the list of skill categories and the currently selected category.
 * @param onCategorySelected Callback function triggered when a skill category is selected from the dropdown.
 *                           It passes the ID of the selected category.
 * @param onClickCreate Callback function triggered when the "Create New Skill" button is clicked.
 * @param onClickImportFromDatabase Callback function triggered when the "Import From Database" button is clicked.
 */
@Composable
private fun DefineSkillHeader(
    data: DefineSkillSuccessData,
    onCategorySelected: (String) -> Unit,
    onClickCreate: () -> Unit,
    onClickImportFromDatabase: () -> Unit,
    onClickManageSkill: () -> Unit,
) {
    Surface {
        Column {
            Button(
                onClick = onClickManageSkill
            ) {
                Text("Manage Skill")
            }
            SkillCategorySelectionDropdown(
                selectedSkill = data.selectedSkillCategory?.category.orEmpty(),
                onSkillSelected = { selectedCategory ->
                    onCategorySelected(
                        data.skillCategory.first { it.category == selectedCategory }.sklCatID
                    )
                },
                skills = data.skillCategory.map { it.category.orEmpty() })
            Spacer(modifier = Modifier.height(16.dp))
            CreateImportButtons(
                onClickCreate = onClickCreate, onClickImportFromDatabase = onClickImportFromDatabase
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
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
    selectedSkill: String, onSkillSelected: (String) -> Unit, skills: List<String>
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
            label = { Text(stringResource(R.string.select_skill)) },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                AnimatedContent(
                    targetState = expanded, transitionSpec = {
                        if (targetState) {
                            (slideInVertically { height -> height } + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )) togetherWith slideOutVertically { height -> -height } + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )
                        } else {
                            (slideInVertically { height -> -height } + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )) togetherWith slideOutVertically { height -> height } + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200
                                )
                            )
                        }
                    }, label = "Animated Icon"
                ) { targetExpanded ->
                    if (targetExpanded) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.open)
                        )
                    } else {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            })
        DropdownMenu(
            expanded = expanded, onDismissRequest = {
                focusManager.clearFocus()
                expanded = false
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            skills.forEach { skill ->
                DropdownMenuItem(text = { Text(skill) }, onClick = {
                    onSkillSelected(skill)
                    expanded = false
                    focusManager.clearFocus()
                })
            }
        }
    }
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
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            onClick = onClickCreate,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(
                "Create new skill", color = Color.White, style = MaterialTheme.typography.bodySmall
            )
        }
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
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
                    text = stringResource(R.string.import_from_db),
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
fun SkillSelectionDropdownPreview() {
    var selectedSkill by remember { mutableStateOf("Cognitive and Creative Skills") }
    val skills = listOf(
        "Cognitive and Creative Skills", "Technical Skills", "Soft Skills"
    )
    ECareProTheme {
        SkillCategorySelectionDropdown(
            selectedSkill, { selectedSkill = it }, skills
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CreateImportButtonsPreview() {
    ECareProTheme {
        CreateImportButtons()
    }
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
            sklCatID = "1",
            sklTypeID = "1",
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
            sklCatID = "1",
            sklTypeID = "2",
            type = "Critical thinking"
        ),
    )

    val dummySkillCategories = listOf(
        Category(category = "Cognitive and Creative Skills", sklCatID = "1"),
        Category(category = "Technical Skills", sklCatID = "2"),
        Category(category = "Soft Skills", sklCatID = "3"),
    )
    val dummyUiState = DefineSkillSuccessData(
        skills = dummySkills,
        skillCategory = dummySkillCategories,
        selectedSkillCategory = dummySkillCategories.first()
    )
    ECareProTheme {
        DefineSkillContent(
            data = dummyUiState,
            onCategorySelected = {},
            onSkillDelete = {},
            onClickCreate = {},
            onClickImportFromDatabase = {},
            editSkillRequest = {},
            onClickManageSkill = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun NoSkillFoundTextPreview() {
    ECareProTheme {
        NoSkillFoundText(
            selectedCategory = "Cognitive and Creative Skills", searchQuery = "Category"
        )
    }
}