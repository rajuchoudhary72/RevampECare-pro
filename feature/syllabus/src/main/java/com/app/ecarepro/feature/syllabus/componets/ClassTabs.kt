import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.ext.toOrdinal
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun ClassTabs(
    selectedClassIndex: Int,
    classes: List<String>,
    onClickClassTabs: (index: Int) -> Unit,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedClassIndex,
        containerColor = White,
        edgePadding = 0.dp,
        minTabWidth = 70.dp
    ) {
        classes.forEachIndexed { index, classStd ->
            val isSelected = index == selectedClassIndex
            val textStyle =
                if (isSelected) MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                else MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp)
            val displayText = classStd.toOrdinal()
            Tab(
                selected = isSelected,
                onClick = { onClickClassTabs(index) },
                text = { Text(text = displayText, style = textStyle) },
                selectedContentColor = MaterialTheme.appColors.primary,
                unselectedContentColor = MaterialTheme.appColors.textPrimary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClassTabsPreview() {
    EcareProTheme {
        ClassTabs(
            selectedClassIndex = 2,
            classes = listOf("All", "1", "2", "3", "4", "5", "UKG", "LKG"),
            onClickClassTabs = {}
        )
    }
}