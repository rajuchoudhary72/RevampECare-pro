package com.app.ecarepro.feature.conversationreport.conversation_list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.ConversationRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val repository: ConversationRepository,
) : BaseViewModel<ConversationListIntent, ConversationListEvent>() {

    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    override fun handleIntent(intent: ConversationListIntent) {
        when (intent) {
            is ConversationListIntent.OnBackClicked -> sendEvent(ConversationListEvent.NavigateBack)
            is ConversationListIntent.OnConversationTapped -> sendEvent(ConversationListEvent.NavigateToDetail(intent.msgId))
            is ConversationListIntent.OnDateRangeSelected -> {
                _uiState.update {
                    it.copy(
                        startDate = intent.startDate,
                        endDate = clampEndDate(intent.startDate, intent.endDate),
                    )
                }
                loadConversations()
            }
            is ConversationListIntent.OnFilterApplied -> {
                _uiState.update { it.copy(filterState = intent.filterState, showFilterSheet = false) }
                loadConversations()
            }
            is ConversationListIntent.OnFilterCleared -> {
                _uiState.update { it.copy(filterState = ConversationFilterState(), showFilterSheet = false) }
                loadConversations()
            }
            is ConversationListIntent.OnDeleteClicked -> {
                _uiState.update { it.copy(conversationToDelete = intent.conversation, showDeleteDialog = true) }
            }
            is ConversationListIntent.OnDeleteConfirmed -> {
                val id = _uiState.value.conversationToDelete?.id.orEmpty()
                _uiState.update { it.copy(showDeleteDialog = false) }
                deleteConversation(id)
            }
            is ConversationListIntent.OnDeleteDismissed -> {
                _uiState.update { it.copy(showDeleteDialog = false, conversationToDelete = null) }
            }
            is ConversationListIntent.OnRefreshClicked -> {
                _uiState.update {
                    it.copy(
                        startDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.time,
                        endDate = Date(),
                        filterState = ConversationFilterState(),
                    )
                }
                loadConversations()
            }
            is ConversationListIntent.ShowFilterSheet -> _uiState.update { it.copy(showFilterSheet = true) }
            is ConversationListIntent.DismissFilterSheet -> _uiState.update { it.copy(showFilterSheet = false) }
            is ConversationListIntent.Retry -> loadConversations()
        }
    }

    private fun clampEndDate(start: Date, end: Date): Date {
        val diffDays = TimeUnit.MILLISECONDS.toDays(end.time - start.time)
        return if (diffDays > 30) {
            Calendar.getInstance().apply {
                time = start
                add(Calendar.DAY_OF_MONTH, 30)
            }.time
        } else end
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val fromDate = dateFmt.format(state.startDate)
            val toDate = dateFmt.format(state.endDate)

            val resultFlow = if (state.filterState.isActive) {
                repository.getFilteredConversations(
                    fromDate, toDate,
                    state.filterState.toSenderParam(),
                    state.filterState.toHasWordParam(),
                ).map { result ->
                    result.map { it.map { c -> ConversationCardPresentation.from(c) } to state.canDelete }
                }
            } else {
                repository.getConversations(fromDate, toDate).map { result ->
                    result.map { (items, canDel) -> items.map { ConversationCardPresentation.from(it) } to canDel }
                }
            }

            resultFlow.collect { result ->
                result.fold(
                    onSuccess = { (presentations, canDel) ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                conversations = presentations,
                                canDelete = canDel,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    },
                )
            }
        }
    }

    private fun deleteConversation(id: String) {
        if (id.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, conversationToDelete = null) }
            repository.deleteConversation(id).collect { result ->
                result.fold(
                    onSuccess = { message ->
                        sendEvent(ConversationListEvent.ShowMessage(SnackbarMessage(message, MessageType.SUCCESS)))
                        loadConversations()
                    },
                    onFailure = { error ->
                        sendEvent(
                            ConversationListEvent.ShowMessage(
                                SnackbarMessage(error.message ?: "Delete failed", MessageType.ERROR)
                            )
                        )
                        _uiState.update { it.copy(isLoading = false) }
                    },
                )
            }
        }
    }
}

@Immutable
data class ConversationListUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val conversations: List<ConversationCardPresentation> = emptyList(),
    val canDelete: Boolean = false,
    val startDate: Date = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.time,
    val endDate: Date = Date(),
    val filterState: ConversationFilterState = ConversationFilterState(),
    val showFilterSheet: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val conversationToDelete: ConversationCardPresentation? = null,
)

sealed interface ConversationListIntent {
    data object OnBackClicked : ConversationListIntent
    data class OnConversationTapped(val msgId: String) : ConversationListIntent
    data class OnDateRangeSelected(val startDate: Date, val endDate: Date) : ConversationListIntent
    data class OnFilterApplied(val filterState: ConversationFilterState) : ConversationListIntent
    data object OnFilterCleared : ConversationListIntent
    data class OnDeleteClicked(val conversation: ConversationCardPresentation) : ConversationListIntent
    data object OnDeleteConfirmed : ConversationListIntent
    data object OnDeleteDismissed : ConversationListIntent
    data object OnRefreshClicked : ConversationListIntent
    data object ShowFilterSheet : ConversationListIntent
    data object DismissFilterSheet : ConversationListIntent
    data object Retry : ConversationListIntent
}

sealed interface ConversationListEvent {
    data object NavigateBack : ConversationListEvent
    data class NavigateToDetail(val msgId: String) : ConversationListEvent
    data class ShowMessage(val message: SnackbarMessage) : ConversationListEvent
}
