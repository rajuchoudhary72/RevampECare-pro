package com.app.ecarepro.designsystem.core.component.personlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

enum class ScholarType(val value: Int, val displayName: String) {
    DAY_SCHOLAR(0, "Day scholars"),
    BOARDING(1, "Boarding"),
    ALL(2, "All");

    companion object {
        fun fromValue(value: Int): ScholarType {
            return values().find { it.value == value } ?: DAY_SCHOLAR
        }
    }
}

/**
 * Scholar Type Dropdown for student list
 * Shows current scholar type with count and allows selection
 */
@Composable
fun ScholarTypeDropdown(
    selectedScholarType: ScholarType,
    count: Int,
    onScholarTypeSelected: (ScholarType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clickable { expanded = true }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${selectedScholarType.displayName} ($count)",
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textPrimary
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Select scholar type",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.appColors.textPrimary
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ScholarType.values().forEach { scholarType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = scholarType.displayName,
                            style = MaterialTheme.appTypography.interRegular14px
                        )
                    },
                    onClick = {
                        onScholarTypeSelected(scholarType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScholarTypeDropdownPreview() {
    EcareProTheme {
        ScholarTypeDropdown(
            selectedScholarType = ScholarType.DAY_SCHOLAR,
            count = 234,
            onScholarTypeSelected = {}
        )
    }
}
