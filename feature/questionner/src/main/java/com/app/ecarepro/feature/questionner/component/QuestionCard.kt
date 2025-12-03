package com.app.ecarepro.feature.questionner.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.AppAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.core.domain.model.Question

@Composable
fun QuestionCard(
    question: Question,
    onLikeClick: () -> Unit,
    onQuestionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.appColors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onQuestionClick)
                .padding(16.dp)
        ) {
            // User info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar
                AppAsyncImage(
                    imageUrl = question.photo,
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.appColors.surface),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // User name and date
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = question.updatedBy,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = question.updatedOn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.appColors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question text
            Text(
                text = question.que,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (question.isILike) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            tint = if (question.isILike) MaterialTheme.appColors.primary else MaterialTheme.appColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (question.likes > 0) {
                        Text(
                            text = question.likes.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.appColors.textSecondary,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "Like",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.appColors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Answers button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Answers",
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (question.totalAnswer > 0) {
                            "Answers • ${question.totalAnswer}"
                        } else {
                            "Answers"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.appColors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionCardPreview() {
    EcareProTheme {
        QuestionCard(
            question = Question(
                qid = 175,
                qType = 1,
                que = "How does the communication between teachers, students, and parents feel through the app?",
                queImg = null,
                updatedBy = "Harsimrat Kaur",
                updatedOn = "05 Apr, 25 • 11:26 AM",
                photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
                likes = 0,
                isILike = false,
                totalAnswer = 0,
                isAnswered = false,
                userID = 2885,
                userType = 2,
                isVerified = true,
                status = null,
                isSelected = false
            ),
            onLikeClick = {},
            onQuestionClick = {},
            modifier = Modifier
        )
    }
}
