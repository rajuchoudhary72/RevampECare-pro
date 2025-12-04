package com.app.ecarepro.feature.syllabus

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.syllabus.SyllabusViewModel.Companion.DEFAULT_SELECTED_CLASS_INDEX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SyllabusViewModel @Inject constructor(
    private val syllabusRepository: SyllabusRepository,
    private val fileDownloader: FileDownloader,
) : BaseViewModel<SyllabusIntent, SyllabusEvent>() {

    private val _uiState: MutableStateFlow<UiState<SyllabusUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    companion object {
        const val DEFAULT_SELECTED_CLASS_INDEX = 0
    }

    init {
        fetchSyllabus()
    }

    private fun fetchSyllabus() {
        viewModelScope.launch {
            syllabusRepository
                .getSyllabus()
                .onStart {
                    _uiState.update { UiState.Loading }
                }.collect { result ->
                    result
                        .onSuccess { syllabuses ->
                            val syllabusMap = mutableMapOf<String, List<Syllabus>>()
                            syllabusMap["All"] = syllabuses
                            syllabusMap.putAll(syllabuses.groupBy { it.classSTD })

                            _uiState.update {
                                UiState.Success(
                                    SyllabusUiState(
                                        selectedClassIndex = DEFAULT_SELECTED_CLASS_INDEX,
                                        classTabs = syllabusMap.keys.toList(),
                                        syllabuses = syllabusMap,
                                        filteredSyllabuses = syllabuses
                                    )
                                )

                            }
                        }
                        .onFailure { error ->
                            _uiState.update { UiState.Error(error.errorMessage()) }

                        }
                }
        }
    }

    override fun handleIntent(intent: SyllabusIntent) {
        when (intent) {
            is SyllabusIntent.OnBackClicked -> viewModelScope.launch { sendEvent(SyllabusEvent.NavigateBack) }
            is SyllabusIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is SyllabusIntent.OnClassSelected -> onClassSelected(intent.index)
            is SyllabusIntent.OnAddNewClicked -> sendEvent(SyllabusEvent.NavigateToAddSyllabus)
            is SyllabusIntent.OnDownloadClicked -> downloadSyllabus(intent.syllabusId)
            is SyllabusIntent.OnViewClicked -> viewSyllabus(intent.syllabusId)
            is SyllabusIntent.OnMenuClicked -> showMenuBottomSheet(intent.syllabusId)
            is SyllabusIntent.OnDismissMenu -> dismissMenuBottomSheet()
            is SyllabusIntent.OnDismissDeleteBottomSheet -> dismissDeleteBottomSheet()
            is SyllabusIntent.OnEditClicked -> editSyllabus(intent.syllabusId)
            is SyllabusIntent.ShowDeleteBottomSheet -> showDeleteBottomSheet()
            is SyllabusIntent.OnDeleteClicked -> deleteSyllabus(intent.syllabusId)
        }
    }

    private fun editSyllabus(syllabusId: String?) {
        viewModelScope.launch {
            if (syllabusId == null) {
                return@launch
            }
            val currentState = (_uiState.value as? UiState.Success)?.data
            if (currentState == null) {
                sendEvent(
                    SyllabusEvent.ShowMessage(
                        SnackbarMessage(
                            "Could not get current state.",
                            MessageType.ERROR
                        )
                    )
                )
                return@launch
            }

            val syllabus = getSyllabusById(currentState, syllabusId)
            if (syllabus == null) {
                sendEvent(
                    SyllabusEvent.ShowMessage(
                        SnackbarMessage(
                            "Syllabus not found.",
                            MessageType.ERROR
                        )
                    )
                )
                return@launch
            }

            sendEvent(SyllabusEvent.EditSyllabus(syllabus))
            dismissMenuBottomSheet()
        }

    }

    private fun deleteSyllabus(syllabusId: String?) {
        viewModelScope.launch {
            if (syllabusId == null) {
                return@launch
            }

            syllabusRepository
                .deleteSyllabus(syllabusId)
                .onStart {
                    val currentState = (_uiState.value as UiState.Success).data
                    _uiState.update {
                        UiState.Success(
                            currentState.copy(isLoading = true)
                        )
                    }
                }
                .collect { result ->
                    result
                        .onSuccess {
                            sendEvent(
                                SyllabusEvent.ShowMessage(
                                    SnackbarMessage(
                                        "Syllabus deleted successfully",
                                        MessageType.SUCCESS
                                    )
                                )
                            )
                            fetchSyllabus()
                        }
                        .onFailure { error ->
                            sendEvent(
                                SyllabusEvent.ShowMessage(
                                    SnackbarMessage(
                                        error.errorMessage(),
                                        MessageType.ERROR
                                    )
                                )
                            )
                        }
                }

        }
    }

    private fun getSyllabusById(state: SyllabusUiState, syllabusId: String): Syllabus? {
        return state.syllabuses.values.flatten().find { it.id == syllabusId }
    }

    private fun downloadSyllabus(syllabusId: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data
        if (currentState == null) {
            sendEvent(
                SyllabusEvent.ShowMessage(
                    SnackbarMessage(
                        "Could not get current state.",
                        MessageType.ERROR
                    )
                )
            )
            return
        }

        val syllabus = getSyllabusById(currentState, syllabusId)
        if (syllabus == null) {
            sendEvent(
                SyllabusEvent.ShowMessage(
                    SnackbarMessage(
                        "Syllabus not found.",
                        MessageType.ERROR
                    )
                )
            )
            return
        }

        syllabus.filePath?.let { url ->
            viewModelScope.launch(Dispatchers.IO) {
                val title = syllabus.title ?: "${syllabus.classSTD} Syllabus"

                try {
                    fileDownloader.download(
                        DownloadRequest(
                            url = url,
                            fileName = title
                        )
                    ).first()

                    withContext(Dispatchers.Main) {
                        sendEvent(
                            SyllabusEvent.ShowMessage(
                                SnackbarMessage("Download Started", MessageType.INFO)
                            )
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        sendEvent(
                            SyllabusEvent.ShowMessage(
                                SnackbarMessage(
                                    "Download failed. Please try again.",
                                    MessageType.ERROR
                                )
                            )
                        )
                    }
                }
            }
        } ?: run {
            sendEvent(
                SyllabusEvent.ShowMessage(
                    SnackbarMessage(
                        "Syllabus file path is not available.",
                        MessageType.ERROR
                    )
                )
            )
        }
    }

    private fun viewSyllabus(syllabusId: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val syllabus = getSyllabusById(currentState, syllabusId)

        syllabus?.filePath?.let {
            sendEvent(
                SyllabusEvent.ViewSyllabus(
                    title = syllabus.title ?: "Syllabus",
                    url = it
                )
            )
        } ?: run {
            sendEvent(
                SyllabusEvent.ShowMessage(
                    SnackbarMessage(
                        "Syllabus file path is not available.",
                        MessageType.ERROR
                    )
                )
            )
        }
    }

    private fun showDeleteBottomSheet() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isDeleteSheetVisible = true,
                    isMenuVisible = false
                )
            )
        }
    }

    private fun showMenuBottomSheet(syllabusId: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isMenuVisible = true,
                    selectedSyllabusId = syllabusId
                )
            )
        }
    }

    private fun dismissMenuBottomSheet() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isMenuVisible = false,
                    selectedSyllabusId = null
                )
            )
        }
    }

    private fun dismissDeleteBottomSheet() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isDeleteSheetVisible = false,
                    selectedSyllabusId = null
                )
            )
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredSyllabuses = filterSyllabusByClass(
                        currentState.syllabuses,
                        currentState.selectedClassIndex,
                        currentState.classTabs,
                        query
                    )
                )
            )
        }
    }

    private fun onClassSelected(index: Int) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    selectedClassIndex = index,
                    filteredSyllabuses = filterSyllabusByClass(
                        currentState.syllabuses,
                        index,
                        currentState.classTabs,
                        currentState.searchQuery
                    )
                )
            )
        }
    }

    private fun filterSyllabusByClass(
        syllabuses: Map<String, List<Syllabus>>,
        selectedTabIndex: Int,
        classTabs: List<String>,
        searchQuery: String,
    ): List<Syllabus> {
        val selectedClass = classTabs.getOrNull(selectedTabIndex) ?: "All"

        val syllabusesForSelectedClass = if (selectedClass == "All") {
            syllabuses.values.flatten()
        } else {
            syllabuses[selectedClass] ?: emptyList()
        }

        return if (searchQuery.isBlank()) {
            syllabusesForSelectedClass
        } else {
            syllabusesForSelectedClass.filter {
                it.title.orEmpty().contains(searchQuery, ignoreCase = true) ||
                        it.subject.orEmpty().contains(searchQuery, ignoreCase = true)
            }
        }
    }

}

@Immutable
data class SyllabusUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedClassIndex: Int = DEFAULT_SELECTED_CLASS_INDEX,
    val classTabs: List<String> = emptyList(),
    val syllabuses: Map<String, List<Syllabus>> = emptyMap(),
    val filteredSyllabuses: List<Syllabus> = emptyList(),
    val isMenuVisible: Boolean = false,
    val isDeleteSheetVisible: Boolean = false,
    val selectedSyllabusId: String? = null,
)

sealed interface SyllabusIntent {
    data object OnBackClicked : SyllabusIntent
    data object OnAddNewClicked : SyllabusIntent
    data class OnSearchQueryChanged(val query: String) : SyllabusIntent
    data class OnClassSelected(val index: Int) : SyllabusIntent
    data class OnViewClicked(val syllabusId: String) : SyllabusIntent
    data class OnDownloadClicked(val syllabusId: String) : SyllabusIntent
    data class OnMenuClicked(val syllabusId: String) : SyllabusIntent
    data object OnDismissMenu : SyllabusIntent
    data object OnDismissDeleteBottomSheet : SyllabusIntent
    data class OnEditClicked(val syllabusId: String?) : SyllabusIntent
    data object ShowDeleteBottomSheet : SyllabusIntent
    data class OnDeleteClicked(val syllabusId: String?) : SyllabusIntent
}

sealed interface SyllabusEvent {
    data object NavigateBack : SyllabusEvent

    data object NavigateToAddSyllabus : SyllabusEvent
    data class ViewSyllabus(
        val title: String,
        val url: String,
    ) : SyllabusEvent

    data class EditSyllabus(val syllabus: Syllabus) : SyllabusEvent

    data class ShowMessage(val snackbarMessage: SnackbarMessage) : SyllabusEvent
}