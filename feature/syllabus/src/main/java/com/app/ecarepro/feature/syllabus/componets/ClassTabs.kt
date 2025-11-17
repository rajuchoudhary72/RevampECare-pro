import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.ClassTab

@Composable
fun ClassTabs(
    selectedClassIndex: Int,
    classes: List<ClassTab>,
    onClickClassTabs: (index: Int) -> Unit,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedClassIndex,
        containerColor = White,
        edgePadding = 0.dp,
        minTabWidth = 70.dp
    ) {
        classes.forEachIndexed { index, data ->
            val isSelected = index == selectedClassIndex
            val textStyle =
                if (isSelected) MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                else MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp)
            Tab(
                selected = isSelected,
                onClick = { onClickClassTabs(index) },
                text = { Text(text = data.name, style = textStyle) },
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
            selectedClassIndex = 0,
            classes = listOf(ClassTab("All", "All"), ClassTab("LKG", "LKG")),
            onClickClassTabs = {}
        )
    }
}