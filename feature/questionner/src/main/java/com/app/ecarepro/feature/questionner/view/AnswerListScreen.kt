package com.app.ecarepro.feature.questionner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.ecarepro.core.domain.model.Answer
import com.app.ecarepro.designsystem.core.component.AppAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerListScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnswerListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error/success messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Answers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.appColors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Question header
            uiState.question?.let { question ->
                QuestionHeader(
                    questionText = question.que,
                    userName = question.updatedBy,
                    userPhoto = question.photo,
                    timestamp = question.updatedOn
                )
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.appColors.divider
            )

            // Answer list
            if (uiState.isLoading && uiState.answers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.appColors.primary
                    )
                }
            } else if (uiState.answers.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No answers yet. Be the first to answer!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(
                        items = uiState.answers,
                        key = { it.anID }
                    ) { answer ->
                        AnswerItem(
                            answer = answer,
                            onDeleteClick = {
                                viewModel.handleIntent(AnswerListIntent.DeleteAnswer(answer.anID))
                            }
                        )
                    }
                }
            }

            // Answer input
            AnswerInput(
                currentText = uiState.currentAnswerText,
                onTextChanged = { viewModel.handleIntent(AnswerListIntent.OnAnswerTextChanged(it)) },
                onSendClick = { viewModel.handleIntent(AnswerListIntent.PostAnswer(uiState.currentAnswerText)) },
                isPosting = uiState.isPostingAnswer,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            )
        }
    }
}

@Composable
private fun QuestionHeader(
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
            AppAsyncImage(
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
private fun AnswerItem(
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
            AppAsyncImage(
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
private fun AnswerInput(
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
            if (isPosting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
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
}

// ============================================
// Preview Section
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerListScreenPreview(
    onNavigateBack: () -> Unit = {}
) {
    val mockQuestion = PreviewMockData.question
    val mockAnswers = PreviewMockData.answers
    var currentAnswerText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Answers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        },
        containerColor = MaterialTheme.appColors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Question header
            QuestionHeader(
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
            ) {
                items(
                    items = mockAnswers,
                    key = { it.aid }
                ) { answer ->
                    AnswerItem(
                        answer = answer,
                        onDeleteClick = {}
                    )
                }
            }

            // Answer input
            AnswerInput(
                currentText = currentAnswerText,
                onTextChanged = { currentAnswerText = it },
                onSendClick = { currentAnswerText = "" },
                isPosting = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            )
        }
    }
}

// Mock data for preview
private object PreviewMockData {
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
        )
    )
}

@Preview(showBackground = true, name = "Answer List Screen - Light")
@Composable
private fun AnswerListScreenPreviewLight() {
    EcareProTheme {
        AnswerListScreenPreview()
    }
}

@Preview(showBackground = true, name = "Answer List Screen - Dark")
@Composable
private fun AnswerListScreenPreviewDark() {
    EcareProTheme(darkTheme = true) {
        AnswerListScreenPreview()
    }
}

@Preview(showBackground = true, name = "Answer Item with Delete")
@Composable
private fun AnswerItemWithDeletePreview() {
    EcareProTheme {
        AnswerItem(
            answer = Answer(
                anID = 1,
                answer = "Sometimes the pages take time to load; a faster refresh would help.",
                answeredBy = "Harsimrat Kaur",
                answeredOn = "05 Apr, 25 • 11:26 AM",
                photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
                userID = 2885,
                userType = 2,
                isMine = true
            ),
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Answer Item without Delete")
@Composable
private fun AnswerItemWithoutDeletePreview() {
    EcareProTheme {
        AnswerItem(
            answer = Answer(
                anID = 2,
                answer = "It would be nice to get dark mode support for night use.",
                answeredBy = "Nivedita Chauhan",
                answeredOn = "05 Apr, 25 • 11:26 AM",
                photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
                userID = 1,
                userType = 3,
                isMine = false
            ),
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Question Header")
@Composable
private fun QuestionHeaderPreview() {
    EcareProTheme {
        QuestionHeader(
            questionText = "How does the communication between teachers, students, and parents feel through the app?",
            userName = "Harsimrat Kaur",
            userPhoto = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            timestamp = "05 Apr, 25 • 11:26 AM"
        )
    }
}

@Preview(showBackground = true, name = "Answer Input - Empty")
@Composable
private fun AnswerInputEmptyPreview() {
    EcareProTheme {
        AnswerInput(
            currentText = "",
            onTextChanged = {},
            onSendClick = {},
            isPosting = false
        )
    }
}

@Preview(showBackground = true, name = "Answer Input - With Text")
@Composable
private fun AnswerInputWithTextPreview() {
    EcareProTheme {
        AnswerInput(
            currentText = "This is my answer to the question",
            onTextChanged = {},
            onSendClick = {},
            isPosting = false
        )
    }
}

@Preview(showBackground = true, name = "Answer Input - Posting")
@Composable
private fun AnswerInputPostingPreview() {
    EcareProTheme {
        AnswerInput(
            currentText = "This is my answer",
            onTextChanged = {},
            onSendClick = {},
            isPosting = true
        )
    }
}
