package com.app.ecarepro.feature.classteacher

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.ClassTeacher
import com.app.ecarepro.core.domain.repository.ClassTeacherRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.component.personlist.ListViewMode
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassTeacherViewModel @Inject constructor(
    private val repository: ClassTeacherRepository,
) : BaseViewModel<ClassTeacherIntent, ClassTeacherEvent>() {

    private val _uiState: MutableStateFlow<UiState<ClassTeacherUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var allTeachers: List<ClassTeacher> = emptyList()

    init {
        fetchTeachers()
    }

    private fun fetchTeachers(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            repository.getClassTeachers()
                .onStart {
                    _uiState.update { current ->
                        if (isRefreshing) {
                            (current as? UiState.Success)?.let {
                                UiState.Success(it.data.copy(isRefreshing = true))
                            } ?: current
                        } else {
                            UiState.Loading
                        }
                    }
                }.collect { result ->
                    result
                        .onSuccess { teachers ->
                            allTeachers = teachers
                            val presentations = filter("", SortConfig(SortOption.NAME, SortDirection.ASCENDING))
                            _uiState.update {
                                UiState.Success(ClassTeacherUiState(filteredTeachers = presentations))
                            }
                        }
                        .onFailure { error ->
                            handleError(error, isRefreshing)
                        }
                }
        }
    }

    private fun handleError(error: Throwable, isRefresh: Boolean) {
        val message = error.errorMessage()
        if (isRefresh) {
            _uiState.update { current ->
                (current as? UiState.Success)?.let {
                    UiState.Success(it.data.copy(isRefreshing = false))
                } ?: current
            }
            sendEvent(ClassTeacherEvent.ShowMessage(SnackbarMessage(message, MessageType.ERROR)))
        } else {
            _uiState.update { UiState.Error(message) }
        }
    }

    override fun handleIntent(intent: ClassTeacherIntent) {
        when (intent) {
            ClassTeacherIntent.OnBackClicked -> sendEvent(ClassTeacherEvent.NavigateBack)
            is ClassTeacherIntent.OnSearchQueryChanged -> onSearchChanged(intent.query)
            ClassTeacherIntent.OnRefresh -> fetchTeachers(true)
            ClassTeacherIntent.OnViewModeToggle -> toggleViewMode()
            is ClassTeacherIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
        }
    }

    private fun toggleViewMode() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                current.copy(
                    viewMode = if (current.viewMode == ListViewMode.GRID) ListViewMode.LIST else ListViewMode.GRID
                )
            )
        }
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filter(current.searchQuery, sortConfig)
        _uiState.update {
            UiState.Success(current.copy(sortConfig = sortConfig, filteredTeachers = filtered))
        }
    }

    private fun onSearchChanged(query: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filter(query, current.sortConfig)
        _uiState.update {
            UiState.Success(current.copy(searchQuery = query, filteredTeachers = filtered))
        }
    }

    private fun filter(searchQuery: String, sortConfig: SortConfig): List<PersonPresentation> {
        var result = allTeachers

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase()
            result = result.filter { t ->
                t.name.lowercase().contains(q) ||
                t.designation.lowercase().contains(q) ||
                t.className.lowercase().contains(q)
            }
        }

        val sorted = when (sortConfig.option) {
            SortOption.NAME -> result.sortedBy { it.name }
            else -> result.sortedBy { it.className }
        }

        return if (sortConfig.direction == SortDirection.DESCENDING) sorted.reversed().map { it.toPresentation() }
        else sorted.map { it.toPresentation() }
    }
}

@Immutable
data class ClassTeacherUiState(
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val filteredTeachers: List<PersonPresentation> = emptyList(),
    val viewMode: ListViewMode = ListViewMode.GRID,
    val sortConfig: SortConfig = SortConfig(SortOption.NAME, SortDirection.ASCENDING),
)

sealed interface ClassTeacherIntent {
    data object OnBackClicked : ClassTeacherIntent
    data class OnSearchQueryChanged(val query: String) : ClassTeacherIntent
    data object OnRefresh : ClassTeacherIntent
    data object OnViewModeToggle : ClassTeacherIntent
    data class OnSortSelected(val sortConfig: SortConfig) : ClassTeacherIntent
}

sealed interface ClassTeacherEvent {
    data object NavigateBack : ClassTeacherEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : ClassTeacherEvent
}
