package com.app.ecarepro.feature.profile.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.SavedAccountItem
import com.app.ecarepro.designsystem.core.component.EcareConfirmationBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchAccountSheet(
    accounts: List<SavedAccountItem>,
    onDismiss: () -> Unit,
    onSwitchAccount: (Int) -> Unit,
    onDeleteAccount: (Int) -> Unit,
    onAddAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var pendingDeleteLocalId by remember { mutableStateOf<Int?>(null) }

    fun dismiss() {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    if (pendingDeleteLocalId != null) {
        EcareConfirmationBottomSheet(
            title = "Remove Account",
            description = "Are you sure you want to remove this account?",
            buttonText = "Remove",
            buttonColor = MaterialTheme.appColors.error,
            onDismiss = { pendingDeleteLocalId = null },
            onDeleteClick = {
                pendingDeleteLocalId?.let { onDeleteAccount(it) }
                pendingDeleteLocalId = null
            },
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Switch account",
                    style = MaterialTheme.appTypography.interSemiBold16px,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = ::dismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.appColors.textSecondary,
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(8.dp))

            if (accounts.isEmpty()) {
                Text(
                    text = "No other accounts added.",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                )
            } else {
                accounts.forEach { account ->
                    AccountRow(
                        account = account,
                        onTap = {
                            if (!account.isActive) {
                                onSwitchAccount(account.localId)
                            }
                        },
                        onDelete = { pendingDeleteLocalId = account.localId },
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add another account button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.appColors.primary,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAddAccount() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.appColors.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add another account",
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.primary,
                )
            }
        }
    }
}

@Composable
private fun AccountRow(
    account: SavedAccountItem,
    onTap: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !account.isActive, onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        Box(modifier = Modifier.size(44.dp)) {
            EcareProAsyncImage(
                imageUrl = account.userPhoto,
                contentDescription = account.name,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
            )
            // School logo badge
            if (!account.schoolLogo.isNullOrBlank()) {
                EcareProAsyncImage(
                    imageUrl = account.schoolLogo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = account.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = buildString {
                    when (account.userType) {
                        1 -> {
                            if (!account.className.isNullOrBlank()) {
                                append("Class: ")
                                append(account.className)
                                append(" · ")
                            }
                        }
                        2 -> {
                            if (!account.stName.isNullOrBlank()) {
                                append("Parent of ")
                                append(account.stName)
                                if (!account.className.isNullOrBlank()) {
                                    append(" (")
                                    append(account.className)
                                    append(")")
                                }
                                append(" · ")
                            }
                        }
                        else -> {
                            if (account.roleName.isNotBlank()) {
                                append(account.roleName)
                                append(" · ")
                            }
                        }
                    }
                    append(account.schoolName)
                },
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Active indicator or delete button
        if (account.isActive) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF4CAF50), CircleShape),
            )
        } else {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove account",
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSwitchAccountSheet() {
    EcareProTheme {
        SwitchAccountSheet(
            accounts = listOf(
                SavedAccountItem(
                    localId = 1,
                    name = "Mohit Singh Pawar",
                    roleName = "Management",
                    schoolCode = "ABC123",
                    schoolName = "Demo School",
                    schoolLogo = null,
                    userPhoto = null,
                    isActive = true,
                    userType = 1,
                    className = "10-A",
                    stName = null,
                ),
                SavedAccountItem(
                    localId = 2,
                    name = "Rahul Kumar",
                    roleName = "Parent",
                    schoolCode = "XYZ456",
                    schoolName = "Another School",
                    schoolLogo = null,
                    userPhoto = null,
                    isActive = false,
                    userType = 2,
                    className = "8-B",
                    stName = "Aryan Kumar",
                ),
            ),
            onDismiss = {},
            onSwitchAccount = {},
            onDeleteAccount = {},
            onAddAccount = {},
        )
    }
}
