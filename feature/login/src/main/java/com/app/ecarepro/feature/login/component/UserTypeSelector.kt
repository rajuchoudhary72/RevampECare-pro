package com.app.ecarepro.feature.login.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.UserType
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun UserTypeSelector(
    modifier: Modifier = Modifier,
    options: List<UserType>,
    selectedOption: UserType?,
    onOptionSelected: (UserType) -> Unit,
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            UserTypeOption(
                userType = option,
                isSelected = option == selectedOption,
                onSelected = { onOptionSelected(option) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserTypeOption(
    modifier: Modifier = Modifier,
    userType: UserType,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val backgroundColor = if (isSelected) MaterialTheme.appColors.primary else Color.White
    val contentColor = if (isSelected) Color.White else MaterialTheme.appColors.textSecondary
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.appColors.border)

    Card(
        onClick = onSelected,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border
    ) {
        Text(
            text = stringResource(userType.stringResId),
            style = MaterialTheme.appTypography.interMedium16px,
            color = contentColor,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F7F7)
@Composable
private fun UserTypeSelectorPreview() {
    EcareProTheme {
        UserTypeSelector(
            modifier = Modifier.padding(16.dp),
            options = UserType.entries.toList(),
            selectedOption = UserType.PARENT,
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F7F7)
@Composable
private fun UserTypeSelectorUnselectedPreview() {
    EcareProTheme {
        UserTypeSelector(
            modifier = Modifier.padding(16.dp),
            options = UserType.entries.toList(),
            selectedOption = null,
            onOptionSelected = {}
        )
    }
}