package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch

/**
 * Data model for a person item in the selection bottom sheet.
 * Reusable across modules (task manager, messaging, etc.)
 */
@Immutable
data class PersonSelectionItem(
    val id: String,
    val name: String,
    val photo: String = "",
    val subtitle: String = "",
    val isSelected: Boolean = false,
)

/**
 * A reusable bottom sheet that displays a searchable list of persons with
 * avatar, name, and checkbox for multi-selection.
 *
 * @param title The title displayed at the top of the sheet (e.g. "Select assignee").
 * @param isVisible Controls the visibility of the bottom sheet.
 * @param persons The list of persons to display.
 * @param searchPlaceholder Placeholder text for the search field.
 * @param onDismiss Called when the sheet is dismissed.
 * @param onSelectionChanged Called when selection changes, returns updated list of selected person IDs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProPersonSelectionBottomSheet(
    title: String,
    isVisible: Boolean,
    persons: List<PersonSelectionItem>,
    searchPlaceholder: String = "Search by name",
    onDismiss: () -> Unit,
    onSelectionChanged: (List<String>) -> Unit,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var searchQuery by remember(isVisible) { mutableStateOf("") }

    val filteredPersons = remember(persons, searchQuery) {
        if (searchQuery.isBlank()) {
            persons
        } else {
            persons.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
        ) {
            // Header: Title + Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    tint = MaterialTheme.appColors.textPrimary
                )
            }

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = searchPlaceholder,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.appColors.border,
                    unfocusedBorderColor = MaterialTheme.appColors.border,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.appTypography.interRegular14px.copy(
                    color = MaterialTheme.appColors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Person List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                itemsIndexed(filteredPersons) { index, person ->
                    PersonSelectionRow(
                        person = person,
                        onClick = {
                            val currentSelected = persons.filter { it.isSelected }.map { it.id }.toMutableList()
                            if (person.isSelected) {
                                currentSelected.remove(person.id)
                            } else {
                                currentSelected.add(person.id)
                            }
                            onSelectionChanged(currentSelected)
                        }
                    )

                    if (index < filteredPersons.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFEEEEEE)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PersonSelectionRow(
    person: PersonSelectionItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        if (person.photo.isNotEmpty()) {
            EcareProAsyncImage(
                imageUrl = person.photo,
                contentDescription = person.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8EAF6), CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = person.name.take(1).uppercase(),
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
                    color = Color(0xFF5C6BC0)
                )
            }
        }

        // Name
        Text(
            text = person.name,
            style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 15.sp),
            color = MaterialTheme.appColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Checkbox
        Checkbox(
            checked = person.isSelected,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.appColors.primary,
                uncheckedColor = MaterialTheme.appColors.textSecondary,
                checkmarkColor = White
            )
        )
    }
}

@Preview(name = "Person Selection Bottom Sheet", showBackground = true)
@Composable
private fun PreviewPersonSelectionBottomSheet() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            EcareProPersonSelectionBottomSheet(
                title = "Select assignee",
                isVisible = true,
                persons = listOf(
                    PersonSelectionItem(id = "1", name = "Aastha Sharma"),
                    PersonSelectionItem(id = "2", name = "Rohan Verma", isSelected = true),
                    PersonSelectionItem(id = "3", name = "Ananya Gupta"),
                    PersonSelectionItem(id = "4", name = "Aarav Mehta"),
                    PersonSelectionItem(id = "5", name = "Pooja Singh"),
                    PersonSelectionItem(id = "6", name = "Kunal Mishra", isSelected = true),
                ),
                searchPlaceholder = "Search by assignee name",
                onDismiss = {},
                onSelectionChanged = {}
            )
        }
    }
}
