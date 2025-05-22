package com.app.ecarepro.compose.ui.manage_skill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.compose.theme.ECareProTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSkillScreen(
    val manageSkillViewModel: ManageSkillViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Skill Category") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SearchBar()
            Spacer(modifier = Modifier.height(16.dp))
            SkillCategoryList(
                skillCategories = listOf(
                    SkillCategory("Critical Thinking"),
                    SkillCategory("Communication"),
                    SkillCategory("Problem-Solving"),
                    SkillCategory("Technical Skills")
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val searchText = remember { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            label = { Text("Search...") },
            modifier = Modifier.weight(1f),
            leadingIcon = {
                IconButton(onClick = { /* TODO: Implement search */ }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { /* TODO: Implement add new skill category */ },
        ) {
            Text("+ Add")
        }
    }
}

@Composable
fun SkillCategoryList(skillCategories: List<SkillCategory>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", modifier = Modifier, fontWeight = FontWeight.Bold)
                VerticalDivider(modifier = Modifier
                    .height(15.dp)
                    .padding(horizontal = 8.dp))
                Text(
                    "Skill Category",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                VerticalDivider(modifier = Modifier
                    .height(15.dp)
                    .padding(horizontal = 8.dp))
                Text("Actions", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }

            LazyColumn {
                itemsIndexed(skillCategories) { index, skillCategory ->
                    SkillCategoryItem(index + 1, skillCategory)
                }
            }
        }
    }
}

@Composable
fun SkillCategoryItem(index: Int, skillCategory: SkillCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$index.", modifier = Modifier, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = skillCategory.name, modifier = Modifier.weight(1f), fontSize = 14.sp)
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

data class SkillCategory(val name: String)

@Preview(showBackground = true)
@Composable
fun ManageSkillScreenPreview() {
    ECareProTheme {
        ManageSkillScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    ECareProTheme {
        SearchBar()
    }
}

@Preview(showBackground = true)
@Composable
fun SkillCategoryListPreview() {
    ECareProTheme {
        SkillCategoryList(
            skillCategories = listOf(
                SkillCategory("Critical Thinking"),
                SkillCategory("Communication"),
                SkillCategory("Problem-Solving"),
                SkillCategory("Technical Skills")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SkillCategoryItemPreview() {
    ECareProTheme {
        SkillCategoryItem(1, SkillCategory("Critical Thinking"))
    }
}

@Preview(showBackground = true)
@Composable
fun SkillCategoryActionsPreview() {
    ECareProTheme {
        SkillCategoryActions(Modifier)
    }
}