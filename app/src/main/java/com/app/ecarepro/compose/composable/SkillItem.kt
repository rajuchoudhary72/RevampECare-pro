package com.app.ecarepro.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.compose.theme.md_theme_light_primary
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement


@Composable
fun SkillItem(
    id: Int,
    title: String,
    description: String,
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    var dragAnchors by remember { mutableStateOf(DragAnchors.Start) }
    AnchoredDraggableBox(
        modifier = Modifier.fillMaxWidth().height(65.dp),
        dragAnchors = dragAnchors,
        firstContent = { modifier ->
            Row(
                modifier = modifier, verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(65.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("$id. $title", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(description)
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
        secondContent = { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .height(65.dp)
                        .width(70.dp)
                        .background(Color.Blue)
                        .clickable(onClick = onClickEdit),
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
                        .height(65.dp)
                        .width(70.dp)
                        .background(Color.Red)
                        .clickable(onClick = onClickDelete),
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
        })
}


@Preview(showBackground = true)
@Composable
fun SkillItemPreview() {
    ECareProTheme {
        SkillItem(
            id = 1,
            title = "Cognitive and Creative Skills",
            description = "Critical thinking | Problem-solving",
            onClickEdit = {},
            onClickDelete = {})
    }
}
