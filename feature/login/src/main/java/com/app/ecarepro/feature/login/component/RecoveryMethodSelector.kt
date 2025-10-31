import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.EcareProOutlinedTextField
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.screens.forgotpassword.RecoveryMethod

@Composable
fun RecoveryMethodSelector(
    modifier: Modifier = Modifier,
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    selectedMethod: RecoveryMethod?,
    onMethodSelected: (RecoveryMethod) -> Unit,
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecoveryOption(
                modifier = Modifier.weight(1f),
                label = "Mobile",
                isSelected = selectedMethod == RecoveryMethod.MOBILE,
                onClick = { onMethodSelected(RecoveryMethod.MOBILE) }
            )
            RecoveryOption(
                modifier = Modifier.weight(1f),
                label = "Email",
                isSelected = selectedMethod == RecoveryMethod.EMAIL,
                onClick = { onMethodSelected(RecoveryMethod.EMAIL) }
            )
        }

        // The text field will only be visible when a method is selected.
        AnimatedVisibility(visible = selectedMethod != null) {
            val (label, keyboardType) = when (selectedMethod) {
                RecoveryMethod.MOBILE -> "Enter your mobile number" to KeyboardType.Phone
                RecoveryMethod.EMAIL -> "Enter your email" to KeyboardType.Email
                else -> "" to KeyboardType.Text // Default, shouldn't be visible
            }

            EcareProOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                value = inputValue,
                onValueChange = onInputValueChange,
                isError = isError,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.appTypography.interRegular14px.copy(
                            color = if (isError) MaterialTheme.appColors.error else MaterialTheme.appColors.textSecondary
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                containerColor = White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecoveryOption(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) MaterialTheme.appColors.primary else Color.White
    val contentColor = if (isSelected) Color.White else MaterialTheme.appColors.textSecondary
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.appColors.border)

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border,
    ) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
            color = contentColor,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun RecoveryMethodSelectorMobilePreview() {
    EcareProTheme {
        RecoveryMethodSelector(
            modifier = Modifier.padding(16.dp),
            inputValue = "9876543210",
            onInputValueChange = {},
            selectedMethod = RecoveryMethod.MOBILE,
            onMethodSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun RecoveryMethodSelectorEmailPreview() {
    EcareProTheme {
        RecoveryMethodSelector(
            modifier = Modifier.padding(16.dp),
            inputValue = "",
            onInputValueChange = {},
            selectedMethod = RecoveryMethod.EMAIL,
            onMethodSelected = {}
        )
    }
}