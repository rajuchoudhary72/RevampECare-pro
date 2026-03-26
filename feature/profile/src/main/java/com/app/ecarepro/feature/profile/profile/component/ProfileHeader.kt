package com.app.ecarepro.feature.profile.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.profile.MyProfileData
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun ProfileHeader(
    profileData: MyProfileData,
    canEdit: Boolean,
    onEditClicked: () -> Unit,
    onManageAccountsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Cover image area
            if (!profileData.coverImg.isNullOrBlank()) {
                EcareProAsyncImage(
                    imageUrl = profileData.coverImg,
                    contentDescription = "Cover image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                )
            } else {
                val gradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(brush = gradient),
                )
            }

            // Profile photo overlapping bottom of cover
            EcareProAsyncImage(
                imageUrl = profileData.photo,
                contentDescription = profileData.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .align(Alignment.BottomCenter)
                    .offset(y = 45.dp),
            )
        }

        Spacer(modifier = Modifier.height(52.dp)) // account for avatar overlap

        Text(
            text = profileData.name,
            style = MaterialTheme.appTypography.interMedium18px,
            color = MaterialTheme.appColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = profileData.designation.ifBlank { profileData.roleName },
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            if (canEdit) {
                Button(
                    onClick = onEditClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.primary,
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Edit profile",
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
            }

            OutlinedButton(
                onClick = onManageAccountsClicked,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.appColors.primary,
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.appColors.primary,
                ),
            ) {
                Text(
                    text = "Manage accounts",
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
