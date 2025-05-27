package com.app.ecarepro.compose.ui.manage_skill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.app.ecarepro.data.network.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSkillScreen(
    viewModel: ManageSkillViewModel = viewModel(), onClickBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val loadState by viewModel.loadState.collectAsStateWithLifecycle(LoadState.Nothing)
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteConfirmationDialog by remember { mutableStateOf<String?>(null) }

    var showAddEditBottomSheet by remember { mutableStateOf<Triple<String, String?, Boolean>?>(null) }

    val message = loadState.messageOrNull()
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showAddEditBottomSheet = Triple("Add Skill Category", null, false)
                    }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
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
                onRetry = { viewModel.refresh() }
            ) { data ->
                ManageSkillScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(top = 8.dp),
                    data = data,
                    onClickAdd = {
                        showAddEditBottomSheet = Triple("Add Skill Type", it, true)
                    },
                    onClickEdit = {
                        showAddEditBottomSheet = Triple("Edit Skill Category", it, false)
                    },
                    onClickDelete = {
                        showDeleteConfirmationDialog = it
                    }
                )
            }
        }
        if (loadState.isLoading()) {
            LoadingDialog()
        }

        showDeleteConfirmationDialog?.let { skillId ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmationDialog = null },
                title = { Text(stringResource(R.string.confirm_delete)) },
                text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_skill_category)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteSkillCategory(skillId)
                        showDeleteConfirmationDialog = null
                    }) { Text(stringResource(R.string.delete)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmationDialog = null }) {
                        Text(
                            stringResource(R.string.cancel)
                        )
                    }
                }
            )
        }

        showAddEditBottomSheet?.let { (title, skillId, isTypeAdd) ->
            CreateSkillBottomSheet(
                title = title,
                onSaveSkill = {
                    showAddEditBottomSheet = null
                    if (isTypeAdd) {
                        viewModel.saveSkillType(skillId.orEmpty(), it)
                    } else {
                        viewModel.saveSkillCategory(skillId, it)
                    }
                },
                onDismissRequest = { showAddEditBottomSheet = null },
                onCancel = { showAddEditBottomSheet = null }
            )
        }

    }
}

@Composable
private fun ManageSkillScreenContent(
    modifier: Modifier = Modifier,
    data: ManageSkillSuccessData,
    onClickAdd: (String) -> Unit = {},
    onClickEdit: (String) -> Unit = {},
    onClickDelete: (String) -> Unit = {}
) {
    SkillCategoryList(
        modifier = modifier,
        categories = data.skillCategory,
        onClickAdd = onClickAdd,
        onClickEdit = onClickEdit,
        onClickDelete = onClickDelete
    )
}


@Composable
fun SkillCategoryList(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    onClickAdd: (String) -> Unit = {},
    onClickEdit: (String) -> Unit = {},
    onClickDelete: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(categories) { index, category ->
            SkillCategoryItem(
                index = index + 1,
                skillCategory = category,
                onClickAdd = onClickAdd,
                onClickEdit = onClickEdit,
                onClickDelete = onClickDelete
            )
            Divider()
        }
    }
}

@Composable
fun SkillCategoryItem(
    index: Int,
    skillCategory: Category,
    onClickAdd: (String) -> Unit = {},
    onClickEdit: (String) -> Unit = {},
    onClickDelete: (String) -> Unit = {}
) {
    var dragAnchors by remember { mutableStateOf(DragAnchors.Start) }

    AnchoredDraggableBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        dragAnchors = dragAnchors,
        offsetSize = 245.dp,
        firstContent = { modifier ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = "$index.",
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )
                Text(
                    text = skillCategory.category.orEmpty(),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )
                IconButton(onClick = {
                    dragAnchors =
                        if (dragAnchors == DragAnchors.Start) DragAnchors.End else DragAnchors.Start
                }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More",
                        tint = md_theme_light_primary
                    )
                }

            }
        },
        secondContent = { modifier ->
            SkillCategoryActions(
                modifier = modifier
                    .height(50.dp),
                onClickAdd = {
                    dragAnchors = DragAnchors.Start
                    onClickAdd(skillCategory.sklCatID)
                },
                onClickEdit = {
                    dragAnchors = DragAnchors.Start
                    onClickEdit(skillCategory.sklCatID)
                },
                onClickDelete = {
                    dragAnchors = DragAnchors.Start
                    onClickDelete(skillCategory.sklCatID)
                }
            )
        }
    )

}

@Composable
fun SkillCategoryActions(
    modifier: Modifier,
    onClickAdd: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    Row(modifier = modifier) {
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = onClickAdd,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("Add", color = Color.White)
        }
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = onClickEdit,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("Edit", color = Color.White)
        }
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = onClickDelete,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("Delete", color = Color.White)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSkillBottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    title: String,
    onSaveSkill: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit
) {
    var value by remember { mutableStateOf("") }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                value = value,
                onValueChange = { value = it },
                label = { Text("Enter here") },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onCancel,
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSaveSkill(value)
                    },
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageSkillScreenContentPreview() {
    val data = ManageSkillSuccessData(
        searchQuery = "some search query",
        skillCategory = listOf(
            Category(
                category = "some category",
                sklCatID = "some sklCatID",
                types = "some types"
            )
        )
    )
    ECareProTheme {
        ManageSkillScreenContent(modifier = Modifier.fillMaxSize(), data = data)
    }

}

@Preview(showBackground = true)
@Composable
fun SkillCategoryListPreview() {
    ECareProTheme {
        SkillCategoryList(
            categories = listOf<Category>(
                Category("Critical Thinking", "1"),
                Category("Critical Thinking", "2"),
                Category("Critical Thinking", "3"),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SkillCategoryItemPreview() {
    ECareProTheme {
        SkillCategoryItem(1, Category("Critical Thinking", "1"))
    }
}

@Preview(showBackground = true)
@Composable
fun SkillCategoryActionsPreview() {
    ECareProTheme {
        SkillCategoryActions(Modifier.height(50.dp))
    }
}