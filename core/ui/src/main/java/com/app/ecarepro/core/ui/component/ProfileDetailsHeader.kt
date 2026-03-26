package com.app.ecarepro.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * Data class representing the header information for a profile details screen.
 * Used by ProfileDetailsHeader component.
 *
 * @param photo URL or path to the profile photo
 * @param displayName The name to display (can include designation, e.g., "Alok Pant, PGT")
 * @param subtitle Additional information like DOJ, admission number, etc.
 */
@Immutable
data class ProfileDetailsHeaderData(
    val photo: String?,
    val displayName: String,
    val subtitle: String
)

/**
 * A common header component for profile details screens (Student/Staff).
 * Displays a compact header with:
 * - Small circular profile photo (40dp) on the left
 * - Name and subtitle in the middle
 * - Close button on the right
 *
 * Matches the Figma design for profile details screens.
 *
 * @param headerData The data to display in the header
 * @param onCloseClick Callback when close button is clicked
 * @param modifier Modifier to be applied to the header
 */
@Composable
fun ProfileDetailsHeader(
    headerData: ProfileDetailsHeaderData?,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (headerData != null) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile photo
                if (headerData.photo.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.appColors.textSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile photo",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.appColors.textSecondary
                        )
                    }
                } else {
                    AsyncImage(
                        model = headerData.photo,
                        contentDescription = "Profile photo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and info
                Column {
                    Text(
                        text = headerData.displayName,
                        style = MaterialTheme.appTypography.interMedium16px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = headerData.subtitle,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }
            }
        }

        // Close button
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Black
            )
        }
    }
}
