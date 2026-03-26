package com.app.ecarepro.feature.questionner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.Answer
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

/**
 * Test screen with static data to verify UI rendering
 * Use this to test if the UI components are working correctly in the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerListScreenTest(
    onBackClick: () -> Unit = {}
) {
    val mockQuestion = TestMockData.question
    val mockAnswers = TestMockData.answers
    var currentAnswerText by remember { mutableStateOf("") }
    var answersList by remember { mutableStateOf(mockAnswers) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        EcareProBackground(
            overlayColor = MaterialTheme.appColors.background
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("Answers") },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.appColors.surface,
                            titleContentColor = MaterialTheme.appColors.textPrimary,
                            navigationIconContentColor = MaterialTheme.appColors.textPrimary
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Question header
                    QuestionHeaderTest(
                        questionText = mockQuestion.questionText,
                        userName = mockQuestion.userName,
                        userPhoto = mockQuestion.userPhoto,
                        timestamp = mockQuestion.timestamp
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.appColors.divider
                    )

                    // Answer list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.appColors.background)
                    ) {
                        items(
                            items = answersList,
                            key = { it.anID }
                        ) { answer ->
                            AnswerItemTest(
                                answer = answer,
                                onDeleteClick = {
                                    // Remove from list when delete is clicked
                                    answersList = answersList.filter { it.anID != answer.anID }
                                }
                            )
                        }
                    }

                    // Answer input
                    AnswerInputTest(
                        currentText = currentAnswerText,
                        onTextChanged = { currentAnswerText = it },
                        onSendClick = {
                            // Add new answer to the list
                            val newAnswer = Answer(
                                anID = answersList.size + 1,
                                answer = currentAnswerText,
                                answeredBy = "Current User",
                                answeredOn = "Just now",
                                photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
                                userID = 2885,
                                userType = 2,
                                isMine = true
                            )
                            answersList = answersList + newAnswer
                            currentAnswerText = ""
                        },
                        isPosting = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionHeaderTest(
    questionText: String,
    userName: String,
    userPhoto: String,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.appColors.surface)
            .padding(16.dp)
    ) {
        // User info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            EcareProAsyncImage(
                imageUrl = userPhoto,
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
                    text = userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Question text
        Text(
            text = questionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

@Composable
private fun AnswerItemTest(
    answer: Answer,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.appColors.surface)
            .padding(16.dp)
    ) {
        // User info and delete button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            EcareProAsyncImage(
                imageUrl = answer.photo,
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(36.dp)
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
                    text = answer.answeredBy,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = answer.answeredOn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                    fontSize = 12.sp
                )
            }

            // Delete button - only show for self answers
            if (answer.isMine) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.appColors.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Answer text
        Text(
            text = answer.answer,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = MaterialTheme.appColors.divider)
    }
}

@Composable
private fun AnswerInputTest(
    currentText: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    isPosting: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.appColors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = currentText,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Write your answer...",
                    color = MaterialTheme.appColors.textSecondary
                )
            },
            enabled = !isPosting,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.appColors.primary,
                unfocusedBorderColor = MaterialTheme.appColors.divider,
                focusedTextColor = MaterialTheme.appColors.textPrimary,
                unfocusedTextColor = MaterialTheme.appColors.textPrimary,
                cursorColor = MaterialTheme.appColors.primary,
                disabledBorderColor = MaterialTheme.appColors.divider,
                disabledTextColor = MaterialTheme.appColors.textSecondary
            ),
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send button
        IconButton(
            onClick = onSendClick,
            enabled = currentText.isNotBlank() && !isPosting,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (currentText.isNotBlank() && !isPosting) {
                        MaterialTheme.appColors.primary
                    } else {
                        MaterialTheme.appColors.divider
                    },
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = if (currentText.isNotBlank()) {
                    Color.White
                } else {
                    MaterialTheme.appColors.textSecondary
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Mock data for testing
private object TestMockData {
    data class QuestionData(
        val questionText: String,
        val userName: String,
        val userPhoto: String,
        val timestamp: String
    )

    val question = QuestionData(
        questionText = "How does the communication between teachers, students, and parents feel through the app?",
        userName = "Harsimrat Kaur",
        userPhoto = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
        timestamp = "05 Apr, 25 • 11:26 AM"
    )

    val answers = listOf(
        Answer(
            anID = 1,
            answer = "Sometimes the pages take time to load; a faster refresh would help.",
            answeredBy = "Harsimrat Kaur",
            answeredOn = "05 Apr, 25 • 11:26 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            userID = 2885,
            userType = 2,
            isMine = true // This answer has delete button
        ),
        Answer(
            anID = 2,
            answer = "It would be nice to get dark mode support for night use.",
            answeredBy = "Nivedita Chauhan",
            answeredOn = "05 Apr, 25 • 11:26 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            userID = 1,
            userType = 3,
            isMine = false
        ),
        Answer(
            anID = 3,
            answer = "Dashboards could be merged; separate attendance, assignments, and notices together.",
            answeredBy = "Pankaj Chauhan",
            answeredOn = "05 Apr, 25 • 11:26 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            userID = 2,
            userType = 1,
            isMine = false
        ),
        Answer(
            anID = 4,
            answer = "The notification system is great! I always stay updated with school activities.",
            answeredBy = "Rajesh Kumar",
            answeredOn = "05 Apr, 25 • 10:15 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            userID = 3,
            userType = 2,
            isMine = false
        ),
        Answer(
            anID = 5,
            answer = "I love the attendance tracking feature. Very helpful for parents!",
            answeredBy = "Priya Sharma",
            answeredOn = "05 Apr, 25 • 09:30 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            userID = 4,
            userType = 2,
            isMine = false
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AnswerListScreenTestPreview() {
    EcareProTheme {
        AnswerListScreenTest()
    }
}
