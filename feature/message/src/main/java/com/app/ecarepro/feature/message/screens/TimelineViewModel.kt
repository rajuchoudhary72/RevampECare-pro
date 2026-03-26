package com.app.ecarepro.feature.message.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.message.SentMessage
import com.app.ecarepro.core.domain.repository.MessageRepository
import com.app.ecarepro.core.domain.util.ProfileUtils
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.pagination.Paginator
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.message.MessageItemData
import com.app.ecarepro.feature.message.common.updateSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
) : BaseViewModel<TimelineIntent, TimelineEvent>() {

    private val _uiState = MutableStateFlow<UiState<TimelineUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var loadedDateMillis: Long = System.currentTimeMillis()

    private val paginator = Paginator(
        scope = viewModelScope,
        onRequest = { page ->
            val date = loadedDateMillis.toApiDate()
            // Pass the selected date as both fromDate and TillDate —
            // same as Message/SentMessages?pg=1&fromDate=10 Dec 2025&TillDate=10 Dec 2025
            messageRepository.getSentMessages(page, fromDate = date, tillDate = date)
        },
        onSuccess = { newItems, isLastPage, isReset ->
            val presentationItems = newItems.map { it.toTimelineItem() }
            val existing = if (isReset) emptyList()
            else (_uiState.value as? UiState.Success)?.data?.allMessages ?: emptyList()
            val merged = (existing + presentationItems).distinctBy { it.id }
            _uiState.value = UiState.Success(
                TimelineUiState(
                    selectedDateMillis = loadedDateMillis,
                    allMessages = merged,
                    messageGroups = merged.groupByDate(),
                    isRefreshing = false,
                    showLoadMoreView = !isLastPage,
                )
            )
        },
        onError = { error, isReset ->
            if (isReset) {
                if (_uiState.value is UiState.Success) {
                    _uiState.updateSuccess { it.copy(isRefreshing = false) }
                } else {
                    _uiState.value = UiState.Error(error.message ?: UNKNOWN_ERROR)
                }
            } else {
                _uiState.updateSuccess { it.copy(showLoadMoreView = false) }
            }
        },
    )

    init {
        paginator.refresh()
    }

    override fun handleIntent(intent: TimelineIntent) {
        when (intent) {
            TimelineIntent.OnRetry -> {
                _uiState.value = UiState.Loading
                paginator.refresh()
            }
            TimelineIntent.OnRefresh -> {
                _uiState.updateSuccess { it.copy(isRefreshing = true) }
                paginator.refresh()
            }
            TimelineIntent.OnLoadMore -> paginator.loadNextPage()
            is TimelineIntent.OnDateSelected -> {
                if (isSameDayMillis(intent.dateMillis, loadedDateMillis)) return
                loadedDateMillis = intent.dateMillis
                _uiState.value = UiState.Loading
                paginator.refresh()
            }
        }
    }

    companion object {
        private const val UNKNOWN_ERROR = "Unknown error, please try again."
    }
}

// ============== Date helpers ==============

/** Formats epoch millis to "dd MMM yyyy" — the format the SentMessages API expects. */
private fun Long.toApiDate(): String =
    SimpleDateFormat("dd MMM yyyy", Locale.US).format(this)

private fun isSameDayMillis(a: Long, b: Long): Boolean {
    val ca = Calendar.getInstance().apply { timeInMillis = a }
    val cb = Calendar.getInstance().apply { timeInMillis = b }
    return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
            ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
}

// ============== Grouping ==============

private fun List<MessageItemData>.groupByDate(): List<TimelineMessageGroup> =
    groupBy { it.sentOnRaw.toDayKey() }
        .entries
        .sortedByDescending { it.key }
        .map { (_, msgs) ->
            val millis = ProfileUtils.parseDateMillis(msgs.first().sentOnRaw)
                ?: System.currentTimeMillis()
            TimelineMessageGroup(
                dateLabel = millis.toGroupLabel(),
                dateMillis = millis,
                messages = msgs,
            )
        }

/** Sortable "yyyy-MM-dd" key for grouping. */
private fun String.toDayKey(): String {
    val millis = ProfileUtils.parseDateMillis(this) ?: return this
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(millis)
}

/** Formats epoch millis to "THURSDAY, 21 JAN". */
private fun Long.toGroupLabel(): String {
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(this).uppercase()
    val dayMonth = SimpleDateFormat("d MMM", Locale.getDefault()).format(this).uppercase()
    return "$dayName, $dayMonth"
}

// ============== Domain → Presentation ==============

private fun SentMessage.toTimelineItem() = MessageItemData(
    id = id,
    title = subject ?: "",
    preview = recipients.firstOrNull()?.let { r ->
        when {
            !r.childName.isNullOrBlank() -> buildString {
                append(r.childName)
                if (!r.className.isNullOrBlank()) append(" (${r.className})")
            }
            !r.designation.isNullOrBlank() -> r.designation
            else -> r.name
        }
    } ?: "",
    timestamp = ProfileUtils.formatRelativeTime(sentOn),
    sentOnRaw = sentOn,
    senderName = recipients.firstOrNull()?.name ?: "",
    senderPhoto = recipients.firstOrNull()?.photo ?: "",
    unreadCount = 0,
    attachmentType = null,
)

// ============== UI State ==============

@Immutable
data class TimelineUiState(
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val allMessages: List<MessageItemData> = emptyList(),
    val messageGroups: List<TimelineMessageGroup> = emptyList(),
    val isRefreshing: Boolean = false,
    val showLoadMoreView: Boolean = false,
)

@Immutable
data class TimelineMessageGroup(
    val dateLabel: String,
    val dateMillis: Long,
    val messages: List<MessageItemData>,
)

// ============== Intents ==============

sealed interface TimelineIntent {
    data object OnRetry : TimelineIntent
    data object OnRefresh : TimelineIntent
    data object OnLoadMore : TimelineIntent
    data class OnDateSelected(val dateMillis: Long) : TimelineIntent
}

// ============== Events ==============

sealed interface TimelineEvent {
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : TimelineEvent
}

// ============== Shared helpers ==============

internal fun buildDateMillis(year: Int, month: Int, day: Int): Long {
    return Calendar.getInstance().apply {
        set(year, month - 1, day, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
