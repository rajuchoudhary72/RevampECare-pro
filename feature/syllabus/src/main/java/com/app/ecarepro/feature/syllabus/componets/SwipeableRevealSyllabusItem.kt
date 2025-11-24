package com.app.ecarepro.feature.syllabus.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R
import kotlin.math.roundToInt

@Composable
fun SwipeableRevealSyllabusItem(
    syllabus: Syllabus,
    isRevealed: Boolean,
    onViewClick: () -> Unit,
    onMenuClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Action buttons (Edit and Delete) - Behind the content
        AnimatedVisibility(
            visible = isRevealed,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                // Edit Button
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .width(100.dp)
                        .height(56.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Edit",
                        color = Color.White,
                        style = MaterialTheme.appTypography.interSemiBold14px
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Delete Button
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .width(100.dp)
                        .height(56.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Delete",
                        color = Color.White,
                        style = MaterialTheme.appTypography.interSemiBold14px
                    )
                }
            }
        }

        // Main content - Slides to reveal buttons
        val offsetX by animateFloatAsState(
            targetValue = if (isRevealed) -216f else 0f, // 2 buttons * 100dp + 2 gaps * 8dp
            animationSpec = tween(300),
            label = "contentOffset"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                )
                {
                    Text(
                        text = syllabus.title.orEmpty(),
                        style = MaterialTheme.appTypography.interSemiBold14px,
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = syllabus.classSTD,
                            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
                            color = MaterialTheme.appColors.primary
                        )

                        Indicator()
                        Text(
                            text = syllabus.subject.orEmpty(),
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textPrimary
                        )
                        Indicator()
                        Text(
                            text = syllabus.updatedOn.orEmpty(),
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary
                        )
                    }
                }

                IconButton(onClick = onMenuClick) {
                    Icon(
                        painterResource(R.drawable.ic_hori_menu),
                        contentDescription = "More options"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier
                        .height(24.dp)
                        .padding(horizontal = 2.dp),
                    title = "View",
                    onClick = onViewClick,
                    leadingIcon = R.drawable.ic_eye,
                    contentPadding = PaddingValues(),
                    titleColor = MaterialTheme.appColors.textSecondary
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(16.dp)
                        .padding(horizontal = 20.dp),
                )

                TextButton(
                    modifier = Modifier
                        .height(24.dp)
                        .padding(horizontal = 2.dp),
                    title = "Download",
                    onClick = onDownloadClick,
                    leadingIcon = R.drawable.ic_download,
                    titleColor = MaterialTheme.appColors.textSecondary,
                    contentPadding = PaddingValues(),
                )
            }
        }
    }
}

@Composable
private fun Indicator() {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(4.dp)
            .background(
                MaterialTheme.appColors.border,
                CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun SwipeableRevealSyllabusItemPreview() {
    EcareProTheme {
        Column {
            SwipeableRevealSyllabusItem(
                syllabus = Syllabus(
                    browsedFile = null,
                    classID = null,
                    classIDs = null,
                    classSTD = "UKG",
                    id = "",
                    fileName = null,
                    filePath = null,
                    fileSize = null,
                    sections = null,
                    subID = null,
                    subject = "English",
                    title = "English",
                    updatedOn = "13 Aug 2024"
                ),
                isRevealed = false,
                onViewClick = {},
                onMenuClick = {},
                onDownloadClick = {},
                onEditClick = {},
                onDeleteClick = {}
            )

            Spacer(Modifier.height(16.dp))

            SwipeableRevealSyllabusItem(
                syllabus = Syllabus(
                    browsedFile = null,
                    classID = null,
                    classIDs = null,
                    classSTD = "UKG",
                    id = "",
                    fileName = null,
                    filePath = null,
                    fileSize = null,
                    sections = null,
                    subID = null,
                    subject = "English",
                    title = "English Revealed",
                    updatedOn = "13 Aug 2024"
                ),
                isRevealed = true,
                onViewClick = {},
                onMenuClick = {},
                onDownloadClick = {},
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}
