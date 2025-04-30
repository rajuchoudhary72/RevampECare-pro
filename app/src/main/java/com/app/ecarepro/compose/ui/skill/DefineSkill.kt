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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.R
import com.app.ecarepro.compose.composable.LoadingComposable
import com.app.ecarepro.compose.model.LoadState
import com.app.ecarepro.compose.model.messageOrNull
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.compose.theme.white
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefineSkillScreen(
    viewModel: DefineSkillViewModel,
    onClickBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.searchViewActive.collectAsStateWithLifecycle()

    val loadState by viewModel.loadState.collectAsStateWithLifecycle(LoadState.Nothing)

    LaunchedEffect(loadState) {
        /*loadState.messageOrNull()?.let{

        }*/
    }

    Scaffold(
        topBar = {
            DefineSkillTopAppBar(
                searchQuery = searchQuery,
                isSearchActive = isSearchActive,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearchActiveChange = viewModel::setSearchViewActiveState,
                onClickBack = onClickBack
            )
        }
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
                NoSkillFoundText(
                    selectedCategory = data.selectedSkillCategory?.category.orEmpty(),
                    searchQuery = data.searchQuery
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = buildAnnotatedString {
                append("No Skill found for this category: ")
                append(selectedCategory)
                addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold),
                    start = length - (selectedCategory?.length ?: 0) - 2,
                    end = length
                )

                if (!searchQuery.isNullOrEmpty()) {
                    append(" and search query: ")
                    append(searchQuery)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start = length - searchQuery.length - 2,
                        end = length
                    )
                }
            },
            textAlign = TextAlign.Center
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

/**
 * Composable function that creates a top app bar for the Define Skill screen.
 * It includes a search bar when activated and a title with a back button.
 *
 * @param searchQuery The current text in the search query.
 * @param isSearchActive Boolean indicating if the search bar is active (expanded).
 * @param onQueryChange Callback triggered when the search query text changes.
 * @param onSearchActiveChange Callback triggered when the search bar's active state changes.
 * @param onClickBack Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefineSkillTopAppBar(
    searchQuery: String,
    isSearchActive: Boolean = false,
    onQueryChange: (String) -> Unit = {},
    onSearchActiveChange: (Boolean) -> Unit = {},
    onClickBack: () -> Unit = {}
) {

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .clip(RoundedCornerShape(120.dp))
                        .background(Color.White),
                    value = searchQuery,
                    onValueChange = { newText -> onQueryChange(newText) },
                    placeholder = { Text("Search...") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isEmpty()) {
                                    onSearchActiveChange(false)
                                } else {
                                    onQueryChange("")
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear Search")
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = white,
                        unfocusedContainerColor = white,
                        focusedIndicatorColor = white,
                        unfocusedIndicatorColor = white,
                        disabledIndicatorColor = white,
                        cursorColor = Color.Black

                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )

            } else {
                Text("Define Skill")
            }
        },
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (isSearchActive) {
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
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
        horizontalArrangement = Arrangement.SpaceBetween
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
fun DefineSkillTopAppBarPreview() {
    ECareProTheme {
        DefineSkillTopAppBar(searchQuery = "")
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
    ECareProTheme {
        SkillCategorySelectionDropdown(
            selectedSkill,
            { selectedSkill = it },
            skills
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
    ECareProTheme {
        SkillItem(dummySkill, 1)
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
    ECareProTheme {
        DefineSkillContent(
            data = dummyUiState,
            onCategorySelected = {},
            onSkillDelete = {},
            onClickCreate = {},
            onClickImportFromDatabase = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun NoSkillFoundTextPreview() {
    ECareProTheme {
        NoSkillFoundText(
            selectedCategory = "Cognitive and Creative Skills",
            searchQuery = "Category"
        )
    }
}