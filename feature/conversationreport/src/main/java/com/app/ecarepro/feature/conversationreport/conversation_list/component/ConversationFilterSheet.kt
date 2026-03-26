package com.app.ecarepro.feature.conversationreport.conversation_list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProOutlineButton
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.conversationreport.R
import com.app.ecarepro.feature.conversationreport.conversation_list.ConversationFilterState
import com.app.ecarepro.feature.conversationreport.conversation_list.HasWordMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationFilterSheet(
    currentFilter: ConversationFilterState,
    onApply: (ConversationFilterState) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selectedSender by remember { mutableStateOf(currentFilter.selectedSender) }
    var selectedRecipient by remember { mutableStateOf(currentFilter.selectedRecipient) }
    var hasWordMode by remember { mutableStateOf(currentFilter.hasWordMode) }
    var hasWordText by remember { mutableStateOf(currentFilter.hasWordText) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.conversation_filter_title),
                    style = MaterialTheme.appTypography.interSemiBold16px,
                    color = MaterialTheme.appColors.textPrimary,
                )
                IconButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                }) {
                    Icon(
                        painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_close),
                        contentDescription = null,
                        tint = MaterialTheme.appColors.textPrimary,
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Date range (display only — driven by main screen)
            Text(
                text = stringResource(R.string.conversation_sender),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            FilterRadioGroup(
                options = listOf(
                    stringResource(R.string.conversation_filter_all),
                    stringResource(R.string.conversation_filter_staff),
                    stringResource(R.string.conversation_filter_parents),
                    stringResource(R.string.conversation_filter_student),
                ),
                selected = selectedSender,
                onSelect = { selectedSender = it },
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.conversation_recipients_label),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            FilterRadioGroup(
                options = listOf(
                    stringResource(R.string.conversation_filter_all),
                    stringResource(R.string.conversation_filter_staff),
                    stringResource(R.string.conversation_filter_parents),
                    stringResource(R.string.conversation_filter_student),
                ),
                selected = selectedRecipient,
                onSelect = { selectedRecipient = it },
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.conversation_has_word),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            FilterRadioGroup(
                options = listOf(
                    stringResource(R.string.conversation_any),
                    stringResource(R.string.conversation_specific),
                ),
                selected = if (hasWordMode == HasWordMode.ANY) 0 else 1,
                onSelect = { hasWordMode = if (it == 0) HasWordMode.ANY else HasWordMode.SPECIFIC },
            )
            AnimatedVisibility(visible = hasWordMode == HasWordMode.SPECIFIC) {
                EcareProInputField(
                    label = stringResource(R.string.conversation_has_word),
                    value = hasWordText,
                    onValueChange = { hasWordText = it },
                    placeholder = stringResource(R.string.conversation_type_word),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    title = stringResource(R.string.conversation_apply),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onApply(
                                ConversationFilterState(
                                    selectedSender = selectedSender,
                                    selectedRecipient = selectedRecipient,
                                    hasWordMode = hasWordMode,
                                    hasWordText = hasWordText,
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                EcareProOutlineButton(
                    title = stringResource(R.string.conversation_clear_all),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onClearAll() }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FilterRadioGroup(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    options.forEachIndexed { index, label ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            RadioButton(
                selected = selected == index,
                onClick = { onSelect(index) },
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.appColors.primary),
            )
            Text(
                text = label,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
            )
        }
    }
}
