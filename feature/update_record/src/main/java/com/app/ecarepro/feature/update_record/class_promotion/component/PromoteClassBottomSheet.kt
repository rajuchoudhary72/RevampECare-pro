package com.app.ecarepro.feature.update_record.class_promotion.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.NextSessionClass
import com.app.ecarepro.core.domain.model.PromotionSection
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoteClassBottomSheet(
    nextSessionClasses: List<NextSessionClass>,
    selectedClass: NextSessionClass?,
    onClassSelected: (NextSessionClass) -> Unit,
    onPromoteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Select class to promote",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp),
                    color = MaterialTheme.appColors.textPrimary,
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDismiss() },
                    tint = MaterialTheme.appColors.textPrimary,
                )
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(nextSessionClasses, key = { it.classID }) { nextClass ->
                    val isSelected = selectedClass?.classID == nextClass.classID
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClassSelected(nextClass) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = nextClass.className,
                                style = if (isSelected)
                                    MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                                else
                                    MaterialTheme.appTypography.interRegular14px.copy(fontSize = 16.sp),
                                color = if (isSelected) MaterialTheme.appColors.primary else MaterialTheme.appColors.textPrimary,
                            )
                            if (nextClass.isSelected) {
                                Text(
                                    text = "Recommended",
                                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 11.sp),
                                    color = MaterialTheme.appColors.primary,
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.appColors.primary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp),
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { onClassSelected(nextClass) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.appColors.primary,
                                unselectedColor = MaterialTheme.appColors.textSecondary,
                            ),
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFEEEEEE),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPromoteClick,
                enabled = selectedClass != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.appColors.primary,
                    disabledContainerColor = MaterialTheme.appColors.primary.copy(alpha = 0.4f),
                ),
            ) {
                Text(
                    text = "Promote",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
                    color = White,
                )
            }
        }
    }
}

private val previewNextClasses = listOf(
    NextSessionClass(classID = 1, className = "LKG B", sections = listOf(PromotionSection(classID = 1, secID = 1, secName = "A", isSelected = false)), isSelected = true),
    NextSessionClass(classID = 2, className = "UKG A", sections = listOf(PromotionSection(classID = 2, secID = 2, secName = "A", isSelected = false)), isSelected = false),
    NextSessionClass(classID = 3, className = "UKG B", sections = emptyList(), isSelected = false),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PromoteClassBottomSheetPreview_NoneSelected() {
    EcareProTheme {
        PromoteClassBottomSheet(
            nextSessionClasses = previewNextClasses,
            selectedClass = null,
            onClassSelected = {},
            onPromoteClick = {},
            onDismiss = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PromoteClassBottomSheetPreview_ClassSelected() {
    EcareProTheme {
        PromoteClassBottomSheet(
            nextSessionClasses = previewNextClasses,
            selectedClass = previewNextClasses[0],
            onClassSelected = {},
            onPromoteClick = {},
            onDismiss = {},
        )
    }
}
