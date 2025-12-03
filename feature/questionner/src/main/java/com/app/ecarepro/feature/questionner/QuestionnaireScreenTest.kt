package com.app.ecarepro.feature.questionner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.questionner.component.QuestionCard
import com.app.ecarepro.feature.questionner.component.QuestionnaireHeader
import com.app.ecarepro.core.domain.model.Question

/**
 * Test screen with static data to verify UI rendering
 * Use this to test if the UI components are working correctly
 */
@Composable
fun QuestionnaireScreenTest(
    onBackClick: () -> Unit = {},
    onQuestionClick: (Int) -> Unit = {},
) {
    val mockQuestions = listOf(
        Question(
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
        Question(
            qid = 170,
            qType = 1,
            que = "Which feature do you find most useful, and why?",
            queImg = null,
            updatedBy = "Nivedita Chauhan",
            updatedOn = "05 Apr, 25 • 11:26 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            likes = 456,
            isILike = false,
            totalAnswer = 23,
            isAnswered = true,
            userID = 1,
            userType = 3,
            isVerified = true,
            status = null,
            isSelected = false
        ),
        Question(
            qid = 165,
            qType = 1,
            que = "What challenges do you face while using the app, and how can we improve it for you?",
            queImg = null,
            updatedBy = "Pankaj Chauhan",
            updatedOn = "05 Apr, 25 • 11:26 AM",
            photo = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/Parent/Thumb/NoImage.jpg",
            likes = 456,
            isILike = true,
            totalAnswer = 23,
            isAnswered = false,
            userID = 1,
            userType = 3,
            isVerified = true,
            status = null,
            isSelected = false
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        EcareProBackground(
            overlayColor = MaterialTheme.appColors.background
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    QuestionnaireHeader(
                        selectedTab = QuestionnaireTab.ALL,
                        onTabSelected = {},
                        onBackClick = onBackClick,
                        onCreateNewClick = {}
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.appColors.background)
                    ) {
                        items(
                            items = mockQuestions,
                            key = { it.qid }
                        ) { question ->
                            QuestionCard(
                                question = question,
                                onLikeClick = {},
                                onQuestionClick = { onQuestionClick(question.qid) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionnaireScreenTestPreview() {
    EcareProTheme {
        QuestionnaireScreenTest()
    }
}
