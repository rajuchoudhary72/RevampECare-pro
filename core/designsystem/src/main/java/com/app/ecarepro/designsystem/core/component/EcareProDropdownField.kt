package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun EcareProDropdownField(
    label: String ? = null,
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
) {
    Column {
        if(label.isNullOrEmpty().not()){
            Text(
                text = label,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        EcareProOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            value = value.orEmpty(),
            onValueChange = {},
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textPrimary
                )
            },
            enabled = false,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_down),
                    contentDescription = "Select",
                    tint = MaterialTheme.appColors.textSecondary
                )
            },
        )
    }
}