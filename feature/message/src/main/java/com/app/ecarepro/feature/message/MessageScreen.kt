package com.app.ecarepro.feature.message

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.ui.component.EcareProPullToRefresh
import com.app.ecarepro.core.ui.component.LazyListLoadMoreHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.message.common.InboxMessageListItem
import com.app.ecarepro.feature.message.common.MessageEmptyState
import com.app.ecarepro.feature.message.screens.InboxIntent
import com.app.ecarepro.feature.message.screens.InboxListUiState
import com.app.ecarepro.feature.message.screens.InboxViewModel
import com.app.ecarepro.feature.message.screens.SentScreen
import com.app.ecarepro.feature.message.screens.TimelineScreen
import com.app.ecarepro.feature.message.navigation.MessageNavigationGraph
import com.app.ecarepro.feature.message.screens.UsersIntent
import com.app.ecarepro.feature.message.screens.UsersScreen
import com.app.ecarepro.feature.message.screens.UsersViewModel

// ============== MAIN SCREEN ==============

@Composable
fun MessageScreen(
    viewModel: MessageViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MessageScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        onChatClick = onChatClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageScreenContent(
    uiState: UiState<MessageUiState>,
    handleIntent: (MessageIntent) -> Unit,
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit = {},
) {
    var showFabMenu by remember { mutableStateOf(false) }
    val usersViewModel: UsersViewModel = hiltViewModel()
    // Hoisted here so it survives tab switches but is recreated when the screen is reopened
    val inboxViewModel: InboxViewModel = hiltViewModel()
    val inboxUiState by inboxViewModel.uiState.collectAsStateWithLifecycle()

    EcareProScaffold(
        containerColor = MaterialTheme.appColors.primary,
        bottomBar = {
            if (uiState is UiState.Success) {
                val isUsersTab = uiState.data.selectedTab == MessageTab.USERS
                BottomSearchBarView(
                    searchText = uiState.data.searchQuery,
                    onSearchTextChange = { handleIntent(MessageIntent.OnSearchQueryChanged(it)) },
                    placeholder = "Search by name, designation",
                    showSortButton = isUsersTab,
                    onSortClick = { usersViewModel.handleIntent(UsersIntent.OnSortClick) },
                    showFilterButton = true,
                    onFilterClick = { /* TODO: filter */ },
                )
            }
        },
    ) { paddingValues ->
        UiStateHandler(
            state = uiState,
            onRetry = { handleIntent(MessageIntent.OnRetry) },
        ) { data ->
            Box(
                modifier = Modifier
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .fillMaxSize(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Green header + tab bar — extends behind status bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.appColors.primary)
                            .statusBarsPadding(),
                    ) {
                        MessageHeader(onAddClick = { /* TODO */ })
                        MessageTabBar(
                            selectedTab = data.selectedTab,
                            onTabSelected = { handleIntent(MessageIntent.OnTabSelected(it)) },
                        )
                    }

                    // Body — delegates to per-tab screens
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.appColors.background),
                    ) {
                        when (data.selectedTab) {
                            MessageTab.INBOX -> InboxContent(
                                uiState = inboxUiState,
                                onRetry = { inboxViewModel.handleIntent(InboxIntent.OnRetry) },
                                onRefresh = { inboxViewModel.handleIntent(InboxIntent.OnRefresh) },
                                onLoadMore = { inboxViewModel.handleIntent(InboxIntent.OnLoadMore) },
                                onChatClick = onChatClick,
                            )
                            MessageTab.TIMELINE -> TimelineScreen(onChatClick = onChatClick)
                            MessageTab.SENT -> SentScreen(onChatClick = onChatClick)
                            MessageTab.USERS -> UsersScreen()
                        }

                        // Transparent scrim — dismiss FAB menu on outside tap
                        if (showFabMenu) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures { showFabMenu = false }
                                    },
                            )
                        }

                        // FAB + expandable compose-message menu
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                AnimatedVisibility(
                                    visible = showFabMenu,
                                    enter = fadeIn(tween(200)) +
                                            slideInVertically(tween(200)) { it / 2 },
                                    exit = fadeOut(tween(150)) +
                                            slideOutVertically(tween(150)) { it / 2 },
                                ) {
                                    ComposeMessageMenu(
                                        onItemSelected = { showFabMenu = false },
                                    )
                                }
                                FloatingActionButton(
                                    onClick = { showFabMenu = !showFabMenu },
                                    containerColor = MaterialTheme.appColors.primary,
                                    shape = CircleShape,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "New message",
                                        tint = White,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============== INBOX CONTENT ==============

@Composable
private fun InboxContent(
    uiState: UiState<InboxListUiState>,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit,
) {
    UiStateHandler(
        state = uiState,
        onRetry = onRetry,
    ) { data ->
        EcareProPullToRefresh(
            isRefreshing = data.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (data.messages.isEmpty()) {
                MessageEmptyState(modifier = Modifier.fillMaxSize())
            } else {
                val listState = rememberLazyListState()

                LazyListLoadMoreHandler(
                    listState = listState,
                    enabled = data.showLoadMoreView,
                    onLoadMore = onLoadMore,
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(data.messages, key = { it.id }) { message ->
                        InboxMessageListItem(
                            message = message,
                            onClick = {
                                onChatClick(
                                    MessageNavigationGraph.ChatDetail(
                                        messageId = message.id,
                                        title = message.title,
                                        participantName = message.senderName,
                                        participantRole = message.preview,
                                        participantPhotoUrl = message.senderPhoto,
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

// ============== HEADER ==============

@Composable
private fun MessageHeader(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
        .padding(
            start = 8.dp, top = 8.dp, bottom = 6.dp,
            end = 1.dp
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Messages",
            style = MaterialTheme.appTypography.interSemiBold17px.copy(fontSize = 22.sp),
            color = White,
        )
    }
}

// ============== TAB BAR ==============

private data class TabItem(
    val tab: MessageTab,
    @DrawableRes val icon: Int,
)

private val tabItems = listOf(
    TabItem(MessageTab.INBOX, R.drawable.ic_tab_inbox),
    TabItem(MessageTab.TIMELINE, R.drawable.ic_tab_timeline),
    TabItem(MessageTab.SENT, R.drawable.ic_tab_sent),
    TabItem(MessageTab.USERS, R.drawable.ic_tab_users),
)

@Composable
private fun MessageTabBar(
    selectedTab: MessageTab,
    onTabSelected: (MessageTab) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        tabItems.forEach { item ->
            val isSelected = item.tab == selectedTab
            val color = if (isSelected) White else White.copy(alpha = 0.55f)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(item.tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.tab.displayName,
                        tint = color,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.tab.displayName,
                        style = MaterialTheme.appTypography.interMedium12px,
                        color = color,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(if (isSelected) White else Color.Transparent),
                )
            }
        }
    }
}

// ============== COMPOSE MESSAGE MENU ==============

@Composable
private fun ComposeMessageMenu(onItemSelected: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Column(modifier = Modifier.width(230.dp).padding(vertical = 6.dp)) {
            ComposeMessageMenuItem(
                icon = Icons.Outlined.Chat,
                label = "In app message",
                badge = "FREE",
                onClick = { onItemSelected() /* TODO: navigate */ },
            )
            ComposeMessageMenuItem(
                icon = Icons.Outlined.Message,
                label = "Text message (₹)",
                onClick = { onItemSelected() /* TODO: navigate */ },
            )
            WhatsAppMenuItem(
                label = "Whatsapp (₹)",
                onClick = { onItemSelected() /* TODO: navigate */ },
            )
        }
    }
}

@Composable
private fun ComposeMessageMenuItem(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.appColors.textPrimary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.appTypography.interMedium12px,
            color = MaterialTheme.appColors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        badge?.let { badgeText ->
            Box(
                modifier = Modifier
                    .background(MaterialTheme.appColors.primary, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.appTypography.interMedium12px.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = White,
                )
            }
        }
    }
}

@Composable
private fun WhatsAppMenuItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // WhatsApp green circle icon
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFF25D366), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Chat,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(13.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.appTypography.interMedium12px,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true)
@Composable
private fun MessageScreenPreview() {
    EcareProTheme {
        MessageScreenContent(
            uiState = UiState.Success(MessageUiState(selectedTab = MessageTab.INBOX)),
            handleIntent = {},
        )
    }
}
