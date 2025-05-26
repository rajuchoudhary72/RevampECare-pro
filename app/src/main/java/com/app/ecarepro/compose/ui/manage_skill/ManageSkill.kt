package com.app.ecarepro.compose.ui.manage_skill

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.ecarepro.compose.composable.ECareTopAppBar
import com.app.ecarepro.compose.composable.LoadingComposable
import com.app.ecarepro.compose.composable.LoadingDialog
import com.app.ecarepro.compose.model.LoadState
import com.app.ecarepro.compose.model.messageOrNull
import com.app.ecarepro.compose.theme.ECareProTheme
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
                        .padding(innerPadding),
                    data = data
                )
            }
        }

        if (loadState.isLoading()) {
            LoadingDialog()
        }
    }
}

@Composable
private fun ManageSkillScreenContent(
    modifier: Modifier = Modifier,
    data: ManageSkillSuccessData
) {
    SkillCategoryList(
        modifier = modifier,
        categories = data.skillCategory
    )
}


@Composable
fun SkillCategoryList(
    modifier: Modifier = Modifier,
    categories: List<Category>
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", fontWeight = FontWeight.Bold)
                VerticalDivider(
                    modifier = Modifier
                        .height(15.dp)
                        .padding(horizontal = 8.dp)
                )
                Text(
                    "Skill Category", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold
                )
                VerticalDivider(
                    modifier = Modifier
                        .height(15.dp)
                        .padding(horizontal = 8.dp)
                )
                Text("Actions", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }

            LazyColumn {
                itemsIndexed(categories) { index, category ->
                    SkillCategoryItem(index + 1, category)
                }
            }
        }
    }
}

@Composable
fun SkillCategoryItem(index: Int, skillCategory: Category) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$index.", modifier = Modifier, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = skillCategory.category.orEmpty(),
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        SkillCategoryActions(modifier = Modifier)
    }
}

@Composable
fun SkillCategoryActions(modifier: Modifier) {
    Row(modifier = modifier) {
        Button(
            onClick = { /* TODO: Implement add skill type */ },
            modifier = Modifier.padding(end = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add", color = Color.White, fontSize = 10.sp)
        }
        Button(
            onClick = { /* TODO: Implement edit skill category */ },
            modifier = Modifier.padding(end = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Edit", color = Color.Black, fontSize = 10.sp)
        }
        Button(
            onClick = { /* TODO: Implement delete skill category */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Delete", color = Color.White, fontSize = 10.sp)
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
        SkillCategoryActions(Modifier)
    }
}