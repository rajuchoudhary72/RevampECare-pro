package com.app.ecarepro.feature.syllabus.componets

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DeleteBottomSheet(
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
        DeleteBottomSheetContent(
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
fun DeleteBottomSheetContent(
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
                text = "Delete",
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp)
            )
            IconButton(onClick = onDismiss) {
                Icon(painterResource(R.drawable.icon_close), contentDescription = "Close")
            }
        }

        Text(
            text = "Do you want to delete this syllabus?",
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            title = "Yeah, delete",
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onDeleteClick,
            backgroundColor = MaterialTheme.appColors.error

        )


    }
}


@Preview(showBackground = true)
@Composable
private fun DeleteBottomSheetPreview() {
    EcareProTheme {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sample Text")
            DeleteBottomSheet(onDismiss = {}, {})
        }

    }
}
