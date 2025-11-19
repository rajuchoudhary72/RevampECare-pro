package com.app.ecarepro.feature.syllabus

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.domain.repository.AdminRepository
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
    private val adminRepository: AdminRepository,
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
            adminRepository
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
            is SyllabusIntent.OnBackClicked -> {
                viewModelScope.launch { sendEvent(SyllabusEvent.NavigateBack) }
            }

            is SyllabusIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is SyllabusIntent.OnClassSelected -> onClassSelected(intent.index)
            is SyllabusIntent.OnAddNewClicked -> sendEvent(SyllabusEvent.NavigateToAddSyllabus)
            is SyllabusIntent.OnDownloadClicked -> downloadSyllabus(intent.syllabusId)
            is SyllabusIntent.OnViewClicked -> viewSyllabus(intent.syllabusId)
            is SyllabusIntent.OnMenuClicked -> showMenuBottomSheet(intent.syllabusId)
            is SyllabusIntent.OnDismissMenu -> dismissMenuBottomSheet()
            is SyllabusIntent.OnDismissDeleteBottomSheet -> dismissDeleteBottomSheet()
            is SyllabusIntent.OnEditClicked -> {}
            is SyllabusIntent.ShowDeleteBottomSheet -> showDeleteBottomSheet()
            SyllabusIntent.OnDeleteClicked -> {}
        }
    }

    // Consider adding a helper function for better reusability and clarity
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
            // The filtering logic now happens inside filterSyllabusByClass
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredSyllabuses = filterSyllabusByClass(
                        currentState.syllabuses,
                        currentState.selectedClassIndex,
                        currentState.classTabs,
                        query // Pass the search query to the filter function
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
                    // The filtering logic now happens inside filterSyllabusByClass
                    filteredSyllabuses = filterSyllabusByClass(
                        currentState.syllabuses,
                        index,
                        currentState.classTabs,
                        currentState.searchQuery // Also apply the existing search query
                    )
                )
            )
        }
    }


    /**
     * Filters the complete syllabus map and returns a flat list based on the selected class and search query.
     */
    private fun filterSyllabusByClass(
        syllabuses: Map<String, List<Syllabus>>,
        selectedTabIndex: Int,
        classTabs: List<String>,
        searchQuery: String,
    ): List<Syllabus> {
        val selectedClass = classTabs.getOrNull(selectedTabIndex) ?: "All"

        // 1. Get the list of syllabuses for the selected class. If "All", flatten the whole map.
        val syllabusesForSelectedClass = if (selectedClass == "All") {
            syllabuses.values.flatten()
        } else {
            syllabuses[selectedClass] ?: emptyList()
        }

        // 2. Filter this list by the search query.
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
    data object OnEditClicked : SyllabusIntent
    data object ShowDeleteBottomSheet : SyllabusIntent
    data object OnDeleteClicked : SyllabusIntent
}

sealed interface SyllabusEvent {
    data object NavigateBack : SyllabusEvent

    data object NavigateToAddSyllabus : SyllabusEvent
    data class ViewSyllabus(
        val title: String,
        val url: String,
    ) : SyllabusEvent

    data class ShowMessage(val snackbarMessage: SnackbarMessage) : SyllabusEvent
}