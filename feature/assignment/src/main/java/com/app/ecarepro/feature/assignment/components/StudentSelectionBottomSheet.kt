package com.app.ecarepro.feature.assignment.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.Student
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProCheckbox
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSelectionBottomSheet(
    title: String,
    isVisible: Boolean,
    options: List<Student>,
    selectedOptions: List<Student>,
    isMultiSelection: Boolean = false,
    onDismiss: () -> Unit,
    onOptionsSelected: (List<Student>) -> Unit,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

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

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(options) { index, option ->
                    val isSelected = selectedOptions.contains(option)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isMultiSelection) {
                                    val newList = selectedOptions.toMutableList()
                                    if (isSelected) newList.remove(option) else newList.add(option)
                                    onOptionsSelected(newList)
                                } else {
                                    onOptionsSelected(listOf(option))
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion { onDismiss() }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Student Image / Placeholder
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            EcareProAsyncImage(
                                imageUrl = option.recipientPhoto ?: "",
                                contentDescription = "Student Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Student Details
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = option.studentName ?: "",
                                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 14.sp),
                                color = MaterialTheme.appColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Class: ${option.className ?: "-"} | Roll No: ${option.rollNumber ?: "-"}",
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = MaterialTheme.appColors.textSecondary
                            )
                        }


                        if (isMultiSelection) {
                            EcareProCheckbox(
                                checked = isSelected,
                                onCheckedChange = null,
                            )
                        } else {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.appColors.primary,
                                    unselectedColor = MaterialTheme.appColors.textSecondary
                                )
                            )
                        }
                    }

                    if (index < options.lastIndex) {
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

private fun createDummyStudent(name: String): Student {
    return Student(
        stID = (0..1000).random(),
        classID = 0,
        recipientName = null,
        recipientPhoto = null,
        studentName = name,
        className = "V",
        rollNumber = "12",
        admissionNumber = null,
        dob = null,
        fatherName = null,
        contactPerson = null,
        mobileNumber = null,
        emailID = null,
        msUser = null,
        msPassword = null
    )
}

@Preview(name = "Single Selection - Student", showBackground = true)
@Composable
private fun PreviewSingleSelectionBottomSheet() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            StudentSelectionBottomSheet(
                title = "Select student",
                isVisible = true,
                options = listOf(
                    createDummyStudent("Student 1"),
                    createDummyStudent("Student 2"),
                    createDummyStudent("Student 3"),
                ),
                selectedOptions = listOf(),
                isMultiSelection = false,
                onDismiss = {},
                onOptionsSelected = {}
            )
        }
    }
}

@Preview(name = "Multi Selection - Students", showBackground = true)
@Composable
private fun PreviewMultiSelectionBottomSheet() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            val s1 = createDummyStudent("Student A")
            val s2 = createDummyStudent("Student B")
            val s3 = createDummyStudent("Student C")
            val s4 = createDummyStudent("Student D")

            StudentSelectionBottomSheet(
                title = "Select student",
                isVisible = true,
                options = listOf(s1, s2, s3, s4),
                selectedOptions = listOf(s1, s3),
                isMultiSelection = true,
                onDismiss = {},
                onOptionsSelected = {}
            )
        }
    }
}
