package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.ext.IntentUtils
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

enum class InfoItemType {
    TEXT,
    EMAIL,
    PHONE,
    AUTO
}

data class InfoGridItem(
    val label: String,
    val value: String,
    val type: InfoItemType = InfoItemType.TEXT
)

@Composable
fun InfoGridView(
    items: List<InfoGridItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    InfoGridItemView(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number of items
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InfoGridItemView(
    item: InfoGridItem,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val value = item.value.ifBlank { "NA" }

    // Determine if the item is clickable
    val isClickable = when (item.type) {
        InfoItemType.EMAIL -> IntentUtils.isEmail(value)
        InfoItemType.PHONE -> IntentUtils.isPhoneNumber(value)
        InfoItemType.AUTO -> IntentUtils.isEmail(value) || IntentUtils.isPhoneNumber(value)
        InfoItemType.TEXT -> false
    }

    // Determine text color and decoration
    val textColor = if (isClickable) MaterialTheme.appColors.primary else MaterialTheme.appColors.textPrimary
    val textDecoration = if (isClickable) TextDecoration.Underline else null

    Column(modifier = modifier) {
        Text(
            text = item.label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.appTypography.interMedium14px,
            color = textColor,
            textDecoration = textDecoration,
            modifier = if (isClickable) {
                Modifier.clickable {
                    when (item.type) {
                        InfoItemType.EMAIL -> IntentUtils.openEmail(context, value)
                        InfoItemType.PHONE -> IntentUtils.openDialer(context, value)
                        InfoItemType.AUTO -> IntentUtils.handleAutoLaunch(context, value)
                        InfoItemType.TEXT -> { /* No action */ }
                    }
                }
            } else {
                Modifier
            }
        )
    }
}
