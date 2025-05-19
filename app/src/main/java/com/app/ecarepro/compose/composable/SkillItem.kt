package com.app.ecarepro.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.R
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.data.network.model.Skill

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

   /* val dismissState = rememberSwipeToDismissBoxState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray.copy(alpha = 0.3f), // Background color
    ) {
        SwipeToDismissBox(
            state = dismissState, enableDismissFromStartToEnd = false, backgroundContent = {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd
                ) {
                    Row {
                        IconButton(onClick = onClickEdit) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = onClickDelete) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("$index. ${skill.category}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.skill_type, skill.type))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.skill_name, skill.skill))
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.MoreVert, contentDescription = "More", tint = Color(0xFF1976D2)
                    )
                }
            }
        }
    }*/


    AnchoredDraggableBox(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        firstContent = { modifier ->
            Box(modifier = modifier.height(45.dp).background(Color.Red))
        },
        secondContent = { modifier ->
            Box(modifier = modifier.height(45.dp).background(Color.Green))
        },
        returnInitialState = true
    )
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
        sklCatID = "1",
        sklTypeID = "1",
        type = "Critical thinking"
    )
    ECareProTheme {
        SkillItem(dummySkill, 1)
    }
}
