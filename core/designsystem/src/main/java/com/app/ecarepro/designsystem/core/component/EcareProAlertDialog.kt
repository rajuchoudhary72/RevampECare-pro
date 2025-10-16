import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * A reusable custom alert dialog for the EcarePro app.
 *
 * This composable provides a consistent style for alerts, with a title, description,
 * and optional positive/negative action buttons.
 *
 * @param onDismissRequest The callback to be invoked when the user requests to dismiss the dialog
 *   (e.g., by clicking outside or pressing the back button).
 * @param title The title of the dialog.
 * @param description The main message of the dialog.
 * @param negativeButtonText The text for the negative action button. If null, the button is not shown.
 * @param positiveButtonText The text for the positive action button. If null, the button is not shown.
 * @param negativeButtonOnClick The lambda to be executed when the negative button is clicked.
 * @param positiveButtonOnClick The lambda to be executed when the positive button is clicked.
 */
@Composable
fun EcareProAlertDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    description: String,
    isDismissable: Boolean = true,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    negativeButtonOnClick: () -> Unit = {},
    positiveButtonOnClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                style = MaterialTheme.appTypography.interRegular14px
            )
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.appTypography.nunitoBold12px.copy(fontSize = 20.sp)
            )
        },
        confirmButton = {
            if (positiveButtonText.isNullOrEmpty().not())
                Button(
                    modifier = Modifier.height(35.dp),
                    title = positiveButtonText,
                    onClick = positiveButtonOnClick
                )
        },
        dismissButton = {
            if (negativeButtonText.isNullOrEmpty().not())
                TextButton(
                    modifier = Modifier.height(35.dp),
                    title = negativeButtonText,
                    onClick = negativeButtonOnClick,
                    titleColor = MaterialTheme.appColors.error
                )
        },
        containerColor = White,
        properties = DialogProperties(
            dismissOnBackPress = isDismissable,
            dismissOnClickOutside = isDismissable,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun EcareProInfoModalBottomSheetPreview() {
    EcareProTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            EcareProAlertDialog(
                title = "Location Permission Required!",
                description = "This app requires location access for security and to function correctly. Please grant the permission to continue.",
                negativeButtonText = "Cancel",
                positiveButtonText = "Confirm",
                negativeButtonOnClick = {},
                positiveButtonOnClick = {},
                onDismissRequest = {},
            )
        }

    }

}