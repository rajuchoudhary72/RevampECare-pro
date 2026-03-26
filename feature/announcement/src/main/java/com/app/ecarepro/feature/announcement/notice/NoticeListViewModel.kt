package com.app.ecarepro.feature.announcement.notice

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.Notice
import com.app.ecarepro.core.domain.repository.AnnouncementRepository
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.announcement.common.NoticeFilter
import com.app.ecarepro.feature.announcement.navigation.AnnouncementNavGraph
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = NoticeListViewModel.Factory::class)
class NoticeListViewModel @AssistedInject constructor(
    @Assisted val navKey: AnnouncementNavGraph.NoticeList,
    private val announcementRepository: AnnouncementRepository,
    private val syllabusRepository: SyllabusRepository,
) : BaseViewModel<NoticeListIntent, NoticeListEvent>() {

    private val noticeType: NoticeType = NoticeType.entries[navKey.noticeTypeOrdinal]

    private val _uiState = MutableStateFlow<UiState<NoticeListUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        if (noticeType == NoticeType.CLASS) {
            fetchClasses()
        } else {
            fetchNotices()
        }
    }

    private fun fetchClasses() {
        viewModelScope.launch {
            syllabusRepository.getAssignmentClasses()
                .onStart { _uiState.update { UiState.Loading } }
                .collect { result ->
                    result
                        .onSuccess { classes ->
                            val selectedClass = classes.firstOrNull()
                            _uiState.update {
                                UiState.Success(
                                    NoticeListUiState(
                                        classes = classes,
                                        selectedClass = selectedClass
                                    )
                                )
                            }
                            selectedClass?.classID?.let { fetchClassNotices(it) }
                        }
                        .onFailure { error ->
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                }
        }
    }

    private fun fetchNotices(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            val flow = when (noticeType) {
                NoticeType.SCHOOL -> announcementRepository.getSchoolNotices()
                NoticeType.STAFF -> announcementRepository.getStaffNotices()
                NoticeType.CLASS -> return@launch
            }

            flow.onStart {
                _uiState.update { currentState ->
                    if (isRefreshing) {
                        (currentState as? UiState.Success)?.let { UiState.Success(it.data.copy(isRefreshing = true)) } ?: currentState
                    } else {
                        UiState.Loading
                    }
                }
            }.collect { result ->
                result
                    .onSuccess { notices ->
                        _uiState.update { currentState ->
                            val current = (currentState as? UiState.Success)?.data ?: NoticeListUiState()
                            UiState.Success(
                                current.copy(
                                    allNotices = notices,
                                    isRefreshing = false
                                )
                            )
                        }
                        applyFilter()
                    }
                    .onFailure { error ->
                        handleError(error, isRefreshing)
                    }
            }
        }
    }

    private fun fetchClassNotices(classId: Int, isRefreshing: Boolean = false) {
        viewModelScope.launch {
            announcementRepository.getClassNotices(classId)
                .onStart {
                    _uiState.update { currentState ->
                        if (isRefreshing) {
                            (currentState as? UiState.Success)?.let { UiState.Success(it.data.copy(isRefreshing = true)) } ?: currentState
                        } else {
                            (currentState as? UiState.Success)?.let { UiState.Success(it.data.copy(isNoticesLoading = true)) } ?: currentState
                        }
                    }
                }
                .collect { result ->
                    result
                        .onSuccess { notices ->
                            _uiState.update { currentState ->
                                val current = (currentState as? UiState.Success)?.data ?: NoticeListUiState()
                                UiState.Success(
                                    current.copy(
                                        allNotices = notices,
                                        isRefreshing = false,
                                        isNoticesLoading = false
                                    )
                                )
                            }
                            applyFilter()
                        }
                        .onFailure { error ->
                            _uiState.update { currentState ->
                                (currentState as? UiState.Success)?.let {
                                    UiState.Success(it.data.copy(isRefreshing = false, isNoticesLoading = false))
                                } ?: currentState
                            }
                            sendEvent(
                                NoticeListEvent.ShowMessage(
                                    SnackbarMessage(error.errorMessage(), MessageType.ERROR)
                                )
                            )
                        }
                }
        }
    }

    private fun handleError(error: Throwable, isRefresh: Boolean) {
        if (isRefresh) {
            _uiState.update { currentState ->
                (currentState as? UiState.Success)?.let { UiState.Success(it.data.copy(isRefreshing = false)) } ?: currentState
            }
            sendEvent(NoticeListEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
        } else {
            _uiState.update { UiState.Error(error.errorMessage()) }
        }
    }

    override fun handleIntent(intent: NoticeListIntent) {
        when (intent) {
            is NoticeListIntent.OnBackClicked -> sendEvent(NoticeListEvent.NavigateBack)
            is NoticeListIntent.OnNoticeClicked -> sendEvent(NoticeListEvent.NavigateToDetail(intent.noticeId, noticeType))
            is NoticeListIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is NoticeListIntent.OnFilterSelected -> onFilterSelected(intent.filter)
            is NoticeListIntent.OnShowFilter -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isFilterVisible = true)) }
            }
            is NoticeListIntent.OnDismissFilter -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isFilterVisible = false)) }
            }
            is NoticeListIntent.OnClassSelected -> onClassSelected(intent.classItem)
            is NoticeListIntent.OnShowClassPicker -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isClassPickerVisible = true)) }
            }
            is NoticeListIntent.OnDismissClassPicker -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isClassPickerVisible = false)) }
            }
            is NoticeListIntent.OnRefresh -> {
                val current = (_uiState.value as? UiState.Success)?.data
                if (noticeType == NoticeType.CLASS) {
                    current?.selectedClass?.classID?.let { fetchClassNotices(it, true) }
                } else {
                    fetchNotices(true)
                }
            }
        }
    }

    private fun onClassSelected(classItem: Class) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(selectedClass = classItem, isClassPickerVisible = false)) }
        classItem.classID?.let { fetchClassNotices(it) }
    }

    private fun onSearchQueryChanged(query: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(searchQuery = query)) }
        applyFilter()
    }

    private fun onFilterSelected(filter: NoticeFilter) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(selectedFilter = filter, isFilterVisible = false)) }
        applyFilter()
    }

    private fun applyFilter() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = current.allNotices
            .filter { notice ->
                when (current.selectedFilter) {
                    NoticeFilter.ALL -> true
                    NoticeFilter.UNREAD -> !notice.isRead
                    NoticeFilter.READ -> notice.isRead
                }
            }
            .filter { notice ->
                current.searchQuery.isBlank() ||
                        notice.heading.contains(current.searchQuery, ignoreCase = true)
            }
        _uiState.update { UiState.Success(current.copy(filteredNotices = filtered)) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AnnouncementNavGraph.NoticeList, NoticeListViewModel> {
        override fun create(param: AnnouncementNavGraph.NoticeList): NoticeListViewModel
    }
}

@Immutable
data class NoticeListUiState(
    val isRefreshing: Boolean = false,
    val isNoticesLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: NoticeFilter = NoticeFilter.ALL,
    val isFilterVisible: Boolean = false,
    val allNotices: List<Notice> = emptyList(),
    val filteredNotices: List<Notice> = emptyList(),
    val classes: List<Class> = emptyList(),
    val selectedClass: Class? = null,
    val isClassPickerVisible: Boolean = false,
)

sealed interface NoticeListIntent {
    data object OnBackClicked : NoticeListIntent
    data class OnNoticeClicked(val noticeId: String) : NoticeListIntent
    data class OnSearchQueryChanged(val query: String) : NoticeListIntent
    data class OnFilterSelected(val filter: NoticeFilter) : NoticeListIntent
    data object OnShowFilter : NoticeListIntent
    data object OnDismissFilter : NoticeListIntent
    data class OnClassSelected(val classItem: Class) : NoticeListIntent
    data object OnShowClassPicker : NoticeListIntent
    data object OnDismissClassPicker : NoticeListIntent
    data object OnRefresh : NoticeListIntent
}

sealed interface NoticeListEvent {
    data object NavigateBack : NoticeListEvent
    data class NavigateToDetail(val noticeId: String, val noticeType: NoticeType) : NoticeListEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : NoticeListEvent
}
