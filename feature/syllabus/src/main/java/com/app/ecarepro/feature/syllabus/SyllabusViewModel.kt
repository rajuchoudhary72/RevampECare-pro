package com.app.ecarepro.feature.syllabus

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.AdminRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// A dummy data model, should be replaced with the actual model from the domain layer.
data class Syllabus(
    val id: Int,
    val title: String,
    val className: String,
    val subject: String,
    val date: String,
)

// A dummy data model for class tabs.
data class ClassTab(
    val id: String,
    val name: String,
)

@HiltViewModel
class SyllabusViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : BaseViewModel<SyllabusIntent, SyllabusEvent>() {

    private val _uiState: MutableStateFlow<UiState<SyllabusUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val allSyllabuses = listOf(
        Syllabus(1, "English syllabus", "9th class", "English", "08 Aug 2025"),
        Syllabus(2, "Chemistry syllabus", "9th class", "Chemistry", "08 Aug 2025"),
        Syllabus(3, "Physics syllabus", "9th class", "Physics", "08 Aug 2025"),
        Syllabus(4, "Hindi syllabus", "9th class", "Hindi", "08 Aug 2025"),
        Syllabus(5, "Maths syllabus", "LKG", "Maths", "09 Aug 2025"),
        Syllabus(6, "Science syllabus", "HKG", "Science", "10 Aug 2025"),
        Syllabus(7, "History syllabus", "1st", "History", "11 Aug 2025"),
    )

    private val classTabs = listOf(
        ClassTab("All", "All"),
        ClassTab("LKG", "LKG"),
        ClassTab("HKG", "HKG"),
        ClassTab("UKG", "UKG"),
        ClassTab("1st", "1st"),
        ClassTab("2nd", "2nd"),
    )

    init {
        fetchSyllabus()
    }

    private fun fetchSyllabus() {
        _uiState.update {
            UiState.Success(
                SyllabusUiState(
                    syllabuses = allSyllabuses,
                    filteredSyllabuses = allSyllabuses,
                    classTabs = classTabs
                )
            )
        }
    }

    override fun handleIntent(intent: SyllabusIntent) {
        when (intent) {
            is SyllabusIntent.OnBackClicked -> {
                viewModelScope.launch { sendEvent(SyllabusEvent.NavigateBack) }
            }

            is SyllabusIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is SyllabusIntent.OnClassSelected -> onClassSelected(intent.index)
            is SyllabusIntent.OnAddNewClicked -> {}
            is SyllabusIntent.OnDownloadClicked -> {}
            is SyllabusIntent.OnViewClicked -> {
                sendEvent(
                    SyllabusEvent.ViewSyllabus(
                        title = intent.syllabusId.toString(),
                        url = "https://icseindia.org/document/sample.pdf"
                    )
                )
            }
            is SyllabusIntent.OnMenuClicked -> showMenuBottomSheet(intent.syllabusId)
            is SyllabusIntent.OnDismissMenu -> dismissMenuBottomSheet()
            is SyllabusIntent.OnDismissDeleteBottomSheet -> dismissDeleteBottomSheet()
            is SyllabusIntent.OnEditClicked -> {}
            is SyllabusIntent.ShowDeleteBottomSheet -> showDeleteBottomSheet()
            SyllabusIntent.OnDeleteClicked -> {}
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

    private fun showMenuBottomSheet(syllabusId: Int) {
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
            val filtered = if (query.isBlank()) {
                currentState.syllabuses
            } else {
                currentState.syllabuses.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.subject.contains(query, ignoreCase = true)
                }
            }
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredSyllabuses = filterSyllabusByClass(
                        filtered,
                        currentState.selectedClassIndex
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
                        index
                    )
                )
            )
        }
    }

    private fun filterSyllabusByClass(
        syllabuses: List<Syllabus>,
        selectedTabIndex: Int,
    ): List<Syllabus> {
        if (selectedTabIndex == 0) return syllabuses // "All" tab
        val selectedClass = classTabs[selectedTabIndex].name
        return syllabuses.filter {
            it.className.equals(selectedClass, ignoreCase = true)
        }
    }
}

@Immutable
data class SyllabusUiState(
    val searchQuery: String = "",
    val selectedClassIndex: Int = 0,
    val classTabs: List<ClassTab> = emptyList(),
    val syllabuses: List<Syllabus> = emptyList(),
    val filteredSyllabuses: List<Syllabus> = emptyList(),
    val isMenuVisible: Boolean = false,
    val isDeleteSheetVisible: Boolean = false,
    val selectedSyllabusId: Int? = null,
)

sealed interface SyllabusIntent {
    data object OnBackClicked : SyllabusIntent
    data object OnAddNewClicked : SyllabusIntent
    data class OnSearchQueryChanged(val query: String) : SyllabusIntent
    data class OnClassSelected(val index: Int) : SyllabusIntent
    data class OnViewClicked(val syllabusId: Int) : SyllabusIntent
    data class OnDownloadClicked(val syllabusId: Int) : SyllabusIntent
    data class OnMenuClicked(val syllabusId: Int) : SyllabusIntent
    data object OnDismissMenu : SyllabusIntent
    data object OnDismissDeleteBottomSheet : SyllabusIntent
    data object OnEditClicked : SyllabusIntent
    data object ShowDeleteBottomSheet : SyllabusIntent
    data object OnDeleteClicked : SyllabusIntent
}

sealed interface SyllabusEvent {
    data object NavigateBack : SyllabusEvent
    data class ViewSyllabus(
        val title: String,
        val url: String,
    ) : SyllabusEvent
}