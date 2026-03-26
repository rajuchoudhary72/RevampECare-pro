package com.app.ecarepro.feature.update_record.class_promotion

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.NextSessionClass
import com.app.ecarepro.core.domain.model.PromotionStudent
import com.app.ecarepro.core.domain.model.StudentPromotion
import com.app.ecarepro.core.domain.repository.ClassPromotionRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassPromotionViewModel @Inject constructor(
    private val repository: ClassPromotionRepository,
) : BaseViewModel<ClassPromotionIntent, ClassPromotionEvent>() {

    private val _uiState = MutableStateFlow(ClassPromotionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadClasses()
    }

    override fun handleIntent(intent: ClassPromotionIntent) {
        when (intent) {
            is ClassPromotionIntent.OnBackClicked -> sendEvent(ClassPromotionEvent.NavigateBack)
            is ClassPromotionIntent.SelectClass -> onClassTabSelected(intent.index)
            is ClassPromotionIntent.OnPromoteClick -> onPromoteClick(intent.stID)
            is ClassPromotionIntent.OnNextClassSelected -> onNextClassSelected(intent.nextClass)
            is ClassPromotionIntent.OnPromoteClassConfirm -> onPromoteClassConfirm()
            is ClassPromotionIntent.OnSectionSelected -> onSectionSelected(intent.secID, intent.secName)
            is ClassPromotionIntent.DismissClassSheet -> _uiState.update { it.copy(showClassSheet = false, promotingStudentStID = null, selectedNextClass = null) }
            is ClassPromotionIntent.DismissSectionSheet -> _uiState.update { it.copy(showSectionSheet = false) }
            is ClassPromotionIntent.OnSortClick -> _uiState.update { it.copy(showSortSheet = true) }
            is ClassPromotionIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
            is ClassPromotionIntent.DismissSortSheet -> _uiState.update { it.copy(showSortSheet = false) }
            is ClassPromotionIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }

    private fun loadClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClasses = true, errorMessage = null) }
            repository.getClassTeacherOf().collect { result ->
                result
                    .onSuccess { classes ->
                        _uiState.update { it.copy(isLoadingClasses = false, classes = classes) }
                        if (classes.isNotEmpty()) loadStudents(classes[0].id)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoadingClasses = false, errorMessage = error.errorMessage()) }
                    }
            }
        }
    }

    private fun onClassTabSelected(index: Int) {
        val classes = _uiState.value.classes
        if (index >= classes.size) return
        _uiState.update { it.copy(selectedClassIndex = index, allStudents = emptyList(), filteredStudents = emptyList()) }
        loadStudents(classes[index].id)
    }

    private fun loadStudents(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStudents = true, errorMessage = null) }
            repository.getClassPromotion(classId).collect { result ->
                result
                    .onSuccess { data ->
                        val studentStates = data.students.map { PromotionStudentUiState(student = it) }
                        val filtered = applyFilterAndSort(studentStates, _uiState.value.searchQuery, _uiState.value.sortConfig)
                        _uiState.update {
                            it.copy(
                                isLoadingStudents = false,
                                yrID = data.yrID,
                                nYrID = data.nYrID,
                                allStudents = studentStates,
                                filteredStudents = filtered,
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoadingStudents = false, errorMessage = error.errorMessage()) }
                    }
            }
        }
    }

    private fun onPromoteClick(stID: Int) {
        _uiState.update { it.copy(showClassSheet = true, promotingStudentStID = stID, selectedNextClass = null) }
    }

    private fun onNextClassSelected(nextClass: NextSessionClass) {
        _uiState.update { it.copy(selectedNextClass = nextClass) }
    }

    private fun onPromoteClassConfirm() {
        if (_uiState.value.selectedNextClass == null) return
        _uiState.update { it.copy(showClassSheet = false, showSectionSheet = true) }
    }

    private fun onSectionSelected(secID: Int, secName: String) {
        val stID = _uiState.value.promotingStudentStID ?: return
        val selectedClass = _uiState.value.selectedNextClass ?: return
        val promoted = PromotionStudentUiState(
            student = _uiState.value.allStudents.first { it.student.stID == stID }.student,
            promotedToClassID = selectedClass.classID,
            promotedToSectionID = secID,
            promotedClassName = selectedClass.className,
            promotedSectionName = secName,
        )
        val updatedAll = _uiState.value.allStudents.map { s ->
            if (s.student.stID == stID) promoted else s
        }
        val filtered = applyFilterAndSort(updatedAll, _uiState.value.searchQuery, _uiState.value.sortConfig)
        _uiState.update {
            it.copy(
                showSectionSheet = false,
                promotingStudentStID = null,
                selectedNextClass = null,
                allStudents = updatedAll,
                filteredStudents = filtered,
            )
        }
        submitSinglePromotion(promoted)
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val filtered = applyFilterAndSort(_uiState.value.allStudents, _uiState.value.searchQuery, sortConfig)
        _uiState.update { it.copy(showSortSheet = false, sortConfig = sortConfig, filteredStudents = filtered) }
    }

    private fun onSearchQueryChanged(query: String) {
        val filtered = applyFilterAndSort(_uiState.value.allStudents, query, _uiState.value.sortConfig)
        _uiState.update { it.copy(searchQuery = query, filteredStudents = filtered) }
    }

    private fun submitSinglePromotion(promoted: PromotionStudentUiState) {
        val promotedToClassID = promoted.promotedToClassID ?: return
        val promotedToSectionID = promoted.promotedToSectionID ?: return
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val promotion = listOf(StudentPromotion(stID = promoted.student.stID, newClassID = promotedToClassID, newSectionID = promotedToSectionID))
            repository.saveClassPromotion(state.yrID, state.nYrID, promotion).collect { result ->
                result
                    .onSuccess { message ->
                        _uiState.update { it.copy(isSubmitting = false) }
                        sendEvent(ClassPromotionEvent.ShowMessage(SnackbarMessage(message.ifBlank { "Promotion saved successfully" }, MessageType.SUCCESS)))
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSubmitting = false) }
                        sendEvent(ClassPromotionEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun applyFilterAndSort(
        students: List<PromotionStudentUiState>,
        query: String,
        sortConfig: SortConfig,
    ): List<PromotionStudentUiState> {
        val filtered = if (query.isBlank()) students else students.filter { s ->
            s.student.name.contains(query, ignoreCase = true) ||
                s.student.admissionNumber.contains(query, ignoreCase = true)
        }
        val comparator: Comparator<PromotionStudentUiState> = when (sortConfig.option) {
            SortOption.ROLL_NUMBER -> compareBy { it.student.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
            SortOption.ADMISSION_NUMBER -> compareBy { it.student.admissionNumber }
            SortOption.NAME -> compareBy { it.student.name }
            else -> compareBy { it.student.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
        }
        return if (sortConfig.direction == SortDirection.ASCENDING) {
            filtered.sortedWith(comparator)
        } else {
            filtered.sortedWith(comparator.reversed())
        }
    }
}

@Immutable
data class ClassPromotionUiState(
    val classes: List<Class> = emptyList(),
    val selectedClassIndex: Int = 0,
    val yrID: Int = 0,
    val nYrID: Int = 0,
    val isLoadingClasses: Boolean = false,
    val isLoadingStudents: Boolean = false,
    val isSubmitting: Boolean = false,
    val allStudents: List<PromotionStudentUiState> = emptyList(),
    val filteredStudents: List<PromotionStudentUiState> = emptyList(),
    val showClassSheet: Boolean = false,
    val showSectionSheet: Boolean = false,
    val promotingStudentStID: Int? = null,
    val selectedNextClass: NextSessionClass? = null,
    val showSortSheet: Boolean = false,
    val sortConfig: SortConfig = SortConfig(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
)

@Immutable
data class PromotionStudentUiState(
    val student: PromotionStudent,
    val promotedToClassID: Int? = null,
    val promotedToSectionID: Int? = null,
    val promotedClassName: String? = null,
    val promotedSectionName: String? = null,
) {
    val isPromoted: Boolean get() = promotedToClassID != null && promotedToSectionID != null
}

sealed interface ClassPromotionIntent {
    data object OnBackClicked : ClassPromotionIntent
    data class SelectClass(val index: Int) : ClassPromotionIntent
    data class OnPromoteClick(val stID: Int) : ClassPromotionIntent
    data class OnNextClassSelected(val nextClass: NextSessionClass) : ClassPromotionIntent
    data object OnPromoteClassConfirm : ClassPromotionIntent
    data class OnSectionSelected(val secID: Int, val secName: String) : ClassPromotionIntent
    data object DismissClassSheet : ClassPromotionIntent
    data object DismissSectionSheet : ClassPromotionIntent
    data object OnSortClick : ClassPromotionIntent
    data class OnSortSelected(val sortConfig: SortConfig) : ClassPromotionIntent
    data object DismissSortSheet : ClassPromotionIntent
    data class OnSearchQueryChanged(val query: String) : ClassPromotionIntent
}

sealed interface ClassPromotionEvent {
    data object NavigateBack : ClassPromotionEvent
    data class ShowMessage(val message: SnackbarMessage) : ClassPromotionEvent
}
