package com.app.ecarepro.feature.survey.survey_questions.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.app.ecarepro.feature.survey.R
import com.app.ecarepro.core.domain.model.survey.SurveyQuestion
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.survey.survey_questions.SurveyQuestionsIntent
import com.app.ecarepro.feature.survey.survey_questions.SurveyQuestionsUiState

@Composable
fun QuestionCard(
    question: SurveyQuestion,
    uiState: SurveyQuestionsUiState,
    handleIntent: (SurveyQuestionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        val questionLabel = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.appColors.primary)) {
                append(question.question)
            }
            if (question.isAnsMandatory) {
                withStyle(SpanStyle(color = Color.Red)) {
                    append(" *")
                }
            }
        }
        Text(
            text = questionLabel,
            style = MaterialTheme.appTypography.interSemiBold14px,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (question.textBoxOnly) {
            EcareProInputField(
                label = "",
                value = uiState.textResponses[question.queID].orEmpty(),
                placeholder = stringResource(R.string.survey_enter_response),
                singleLine = false,
                minLines = 3,
                onValueChange = { text ->
                    handleIntent(SurveyQuestionsIntent.OnTextResponseChanged(question.queID, text))
                },
            )
        } else {
            question.options.forEach { option ->
                val isSelected = if (question.isMultiSelect) {
                    option.optID in (uiState.multiSelections[question.queID].orEmpty())
                } else {
                    uiState.singleSelections[question.queID] == option.optID
                }
                OptionRow(
                    optionText = option.option,
                    isSelected = isSelected,
                    isMultiSelect = question.isMultiSelect,
                    onClick = {
                        if (question.isMultiSelect) {
                            handleIntent(
                                SurveyQuestionsIntent.OnMultiSelectToggled(question.queID, option.optID)
                            )
                        } else {
                            handleIntent(
                                SurveyQuestionsIntent.OnSingleSelectChanged(question.queID, option.optID)
                            )
                        }
                    },
                )
            }
        }
    }
}
