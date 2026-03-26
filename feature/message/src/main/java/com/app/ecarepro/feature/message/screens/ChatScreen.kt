package com.app.ecarepro.feature.message.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

// ============== DATA MODELS ==============

enum class ChatMessageType {
    TEXT, AUDIO, IMAGE_WITH_TEXT
}

enum class ChatMessageSide {
    SENT, RECEIVED
}

data class ChatMessage(
    val id: String,
    val type: ChatMessageType,
    val side: ChatMessageSide,
    val text: String = "",
    val timestamp: String = "",
    val audioDuration: String = "",
    val imageUrl: String = "",
)

// ============== SCREEN ==============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationTitle: String = "Upcoming Unit test schedule",
    participantName: String = "Mr. Alok Pant",
    participantRole: String = "Music teacher",
    participantPhotoUrl: String = "",
    messages: List<ChatMessage> = sampleChatMessages(),
    onNavigateBack: () -> Unit = {},
) {
    var replyText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    EcareProScaffold(
        containerColor = White,
        topBar = {
            EcareProTopAppBar(
                title = "",
                onNavigationClicked = onNavigateBack,
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.appColors.textPrimary,
                            )
                        }
                        ChatAvatar(
                            name = participantName,
                            photoUrl = participantPhotoUrl,
                            size = 40,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = conversationTitle,
                                style = MaterialTheme.appTypography.interSemiBold14px,
                                color = MaterialTheme.appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "$participantName • $participantRole",
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = MaterialTheme.appColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: more options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.appColors.textPrimary,
                        )
                    }
                },
            )
        },
        bottomBar = {
            ChatInputBar(
                text = replyText,
                onTextChange = { replyText = it },
                onSendClick = { replyText = "" },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageItem(message = message)
            }
        }
    }
}


// ============== AVATAR ==============

@Composable
private fun ChatAvatar(name: String, photoUrl: String, size: Int = 40) {
    if (photoUrl.isNotEmpty()) {
        EcareProAsyncImage(
            imageUrl = photoUrl,
            contentDescription = name,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(MaterialTheme.appColors.accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = White,
            )
        }
    }
}

// ============== MESSAGE ITEM ==============

private val ReceivedBubbleColor = Color(0xFFDCF8C6)
private val SentBubbleColor = White

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val isSent = message.side == ChatMessageSide.SENT
    val bubbleColor = if (isSent) SentBubbleColor else ReceivedBubbleColor
    val bubbleShape = if (isSent) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 4.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bubbleColor, bubbleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            when (message.type) {
                ChatMessageType.TEXT -> {
                    Text(
                        text = message.text,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }

                ChatMessageType.AUDIO -> {
                    AudioMessageBubble(duration = message.audioDuration)
                }

                ChatMessageType.IMAGE_WITH_TEXT -> {
                    Column {
                        if (message.text.isNotEmpty()) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.appTypography.interRegular14px,
                                color = MaterialTheme.appColors.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        ImageMessageBubble(imageUrl = message.imageUrl)
                    }
                }
            }
        }

        // Timestamp
        if (message.timestamp.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

// ============== AUDIO BUBBLE ==============

@Composable
private fun AudioMessageBubble(duration: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.widthIn(min = 200.dp),
    ) {
        // Play button
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.appColors.textPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play audio",
                tint = White,
                modifier = Modifier.size(18.dp),
            )
        }

        // Waveform (simulated with bars)
        AudioWaveform(
            modifier = Modifier.weight(1f),
        )

        // Duration
        Text(
            text = duration,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}

@Composable
private fun AudioWaveform(modifier: Modifier = Modifier) {
    val barHeights = remember {
        listOf(4, 8, 12, 16, 10, 6, 14, 18, 12, 8, 16, 10, 6, 14, 10, 8, 4, 12, 16, 8)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        barHeights.forEach { barHeight ->
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(barHeight.dp)
                    .background(
                        MaterialTheme.appColors.textPrimary.copy(alpha = 0.7f),
                        RoundedCornerShape(1.dp),
                    ),
            )
        }
    }
}

// ============== IMAGE BUBBLE ==============

@Composable
private fun ImageMessageBubble(imageUrl: String) {
    EcareProAsyncImage(
        imageUrl = imageUrl,
        contentDescription = "Attached image",
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 180.dp)
            .clip(RoundedCornerShape(8.dp)),
    )
}

// ============== INPUT BAR ==============

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White),
    ) {
        // Top border of the input section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFDDDDDD)),
        )

        // Text input area — tall, multi-line capable
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 90.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            textStyle = MaterialTheme.appTypography.interRegular14px.copy(
                color = MaterialTheme.appColors.textPrimary,
            ),
            cursorBrush = SolidColor(MaterialTheme.appColors.primary),
            maxLines = 5,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = "Reply to principal",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
                innerTextField()
            },
        )

        // Divider between text area and toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFDDDDDD)),
        )

        // Formatting toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Dark filled circle + button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF1C1C1C), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Attach",
                    tint = White,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // B — Bold
            FormattingLabel(
                text = "B",
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.width(10.dp))

            // U — Underline
            FormattingLabel(
                text = "U",
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
            )

            Spacer(modifier = Modifier.width(10.dp))

            // I — Italic
            FormattingLabel(
                text = "I",
                fontStyle = FontStyle.Italic,
            )

            Spacer(modifier = Modifier.width(10.dp))

            // S — Strikethrough
            FormattingLabel(
                text = "S",
                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Mic icon — outline, no background
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice message",
                tint = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.size(22.dp),
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Send arrow — green icon, no background container
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = MaterialTheme.appColors.primary,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onSendClick() },
            )
        }
    }
}

@Composable
private fun FormattingLabel(
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: androidx.compose.ui.text.style.TextDecoration = androidx.compose.ui.text.style.TextDecoration.None,
) {
    Text(
        text = text,
        style = MaterialTheme.appTypography.interMedium14px.copy(
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            fontSize = 15.sp,
        ),
        color = MaterialTheme.appColors.textPrimary,
    )
}

// ============== SAMPLE DATA ==============

fun sampleChatMessages(): List<ChatMessage> = listOf(
    ChatMessage(
        id = "1",
        type = ChatMessageType.TEXT,
        side = ChatMessageSide.SENT,
        text = "Good morning, Sir. I wanted to update you regarding the homework submission for Class 11 A. A few students have not yet submitted their assignments.",
        timestamp = "04:45 PM",
    ),
    ChatMessage(
        id = "2",
        type = ChatMessageType.TEXT,
        side = ChatMessageSide.RECEIVED,
        text = "Thank you for informing me. Have the students been reminded about the deadline?",
        timestamp = "04:45 PM",
    ),
    ChatMessage(
        id = "3",
        type = ChatMessageType.AUDIO,
        side = ChatMessageSide.RECEIVED,
        audioDuration = "01:24",
        timestamp = "04:45 PM",
    ),
    ChatMessage(
        id = "4",
        type = ChatMessageType.TEXT,
        side = ChatMessageSide.SENT,
        text = "Yes, Sir. I have already reminded them in class and shared a note with the students. Most submissions are complete, but a small number are still pending.",
        timestamp = "04:45 PM",
    ),
    ChatMessage(
        id = "5",
        type = ChatMessageType.IMAGE_WITH_TEXT,
        side = ChatMessageSide.RECEIVED,
        text = "Alright. Please give them a final reminder and keep me informed if there are continued delays.",
        imageUrl = "",
        timestamp = "04:45 PM",
    ),
)

// ============== PREVIEW ==============

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatScreenPreview() {
    EcareProTheme {
        ChatScreen()
    }
}
