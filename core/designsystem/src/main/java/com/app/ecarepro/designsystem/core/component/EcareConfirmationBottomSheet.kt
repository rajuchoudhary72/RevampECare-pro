package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EcareConfirmationBottomSheet(
    title: String,
    description: String,
    buttonText: String,
    buttonColor: Color = MaterialTheme.appColors.primary,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = {},
    ) {
        ConfirmationBottomSheetContent(
            title = title,
            description = description,
            buttonText = buttonText,
            buttonColor = buttonColor,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismiss()
                    }
                }
            },
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
private fun ConfirmationBottomSheetContent(
    title: String,
    description: String,
    buttonText: String,
    buttonColor: Color,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp)
            )
            IconButton(onClick = onDismiss) {
                Icon(painterResource(R.drawable.icon_close), contentDescription = "Close")
            }
        }

        Text(
            text = description,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            title = buttonText,
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onDeleteClick,
            backgroundColor = buttonColor
        )


    }
}


@Preview(showBackground = true)
@Composable
private fun DeleteBottomSheetPreview() {
    EcareProTheme {
        EcareConfirmationBottomSheet(
            title = "Delete",
            description = "Do you want to delete this syllabus?",
            buttonText = "Yeah, delete",
            onDismiss = {},
            onDeleteClick = {}
        )
    }
}
