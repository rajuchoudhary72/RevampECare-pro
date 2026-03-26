package com.app.ecarepro.feature.questionpaper

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.QPAcademicYear
import com.app.ecarepro.core.domain.model.QuestionPaperItem
import com.app.ecarepro.core.domain.model.SubjectPapers
import com.app.ecarepro.core.domain.repository.QuestionPaperRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestionPaperViewModel @Inject constructor(
    private val questionPaperRepository: QuestionPaperRepository,
    private val userRepository: UserRepository,
    private val fileDownloader: FileDownloader,
) : BaseViewModel<QuestionPaperIntent, QuestionPaperEvent>() {

    private val _uiState: MutableStateFlow<UiState<QuestionPaperUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            try {
                val user = userRepository.getActiveUser()
                val isStaff = user?.userType == 1

                if (isStaff) {
                    val classes = questionPaperRepository.getStaffClasses()
                    if (classes.isEmpty()) {
                        _uiState.update { UiState.Error("No classes found") }
                        return@launch
                    }
                    val firstClass = classes.first()
                    _uiState.update {
                        UiState.Success(
                            QuestionPaperUiState(
                                isStaff = true,
                                classes = classes,
                                selectedClass = firstClass,
                            )
                        )
                    }
                    fetchQuestionPapers(firstClass.id.toIntOrNull() ?: 0, 0)
                } else {
                    val classId = user?.classID ?: 0
                    _uiState.update {
                        UiState.Success(
                            QuestionPaperUiState(
                                isStaff = false,
                                selectedClassId = classId,
                            )
                        )
                    }
                    fetchQuestionPapers(classId, 0)
                }
            } catch (e: Exception) {
                _uiState.update { UiState.Error(e.message ?: "Something went wrong") }
            }
        }
    }

    private fun fetchQuestionPapers(classId: Int, yrId: Int) {
        viewModelScope.launch {
            val currentData = (_uiState.value as? UiState.Success)?.data ?: return@launch
            _uiState.update { UiState.Success(currentData.copy(isPapersLoading = true)) }
            try {
                val response = questionPaperRepository.getQuestionPapers(classId, yrId)
                val years = response.academicYear ?: emptyList()
                val subjectPapers = response.qPList ?: emptyList()

                val currentYear = years.firstOrNull { it.isCur == true } ?: years.firstOrNull()
                val subjects = subjectPapers.map { it.subjectName ?: "" }

                val updatedData = currentData.copy(
                    isPapersLoading = false,
                    academicYears = years,
                    selectedYear = currentYear,
                    subjectPapers = subjectPapers,
                    subjectTabs = subjects,
                    selectedSubjectIndex = 0,
                )
                _uiState.update { UiState.Success(updatedData) }
            } catch (e: Exception) {
                val updatedData = currentData.copy(
                    isPapersLoading = false,
                    subjectPapers = emptyList(),
                    subjectTabs = emptyList(),
                )
                _uiState.update { UiState.Success(updatedData) }
                sendEvent(
                    QuestionPaperEvent.ShowMessage(
                        SnackbarMessage(e.message ?: "Failed to load question papers", MessageType.ERROR)
                    )
                )
            }
        }
    }

    override fun handleIntent(intent: QuestionPaperIntent) {
        when (intent) {
            is QuestionPaperIntent.OnBackClicked -> sendEvent(QuestionPaperEvent.NavigateBack)
            is QuestionPaperIntent.OnSubjectTabSelected -> onSubjectTabSelected(intent.index)
            is QuestionPaperIntent.OnClassSelectClicked -> {
                val data = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(data.copy(showClassSheet = true)) }
            }
            is QuestionPaperIntent.OnClassSelected -> onClassSelected(intent.classId)
            is QuestionPaperIntent.OnDismissClassSheet -> {
                val data = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(data.copy(showClassSheet = false)) }
            }
            is QuestionPaperIntent.OnYearSelectClicked -> {
                val data = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(data.copy(showYearSheet = true)) }
            }
            is QuestionPaperIntent.OnYearSelected -> onYearSelected(intent.yrId)
            is QuestionPaperIntent.OnDismissYearSheet -> {
                val data = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(data.copy(showYearSheet = false)) }
            }
            is QuestionPaperIntent.OnDownloadClicked -> downloadPaper(intent.paper)
            is QuestionPaperIntent.OnViewClicked -> viewPaper(intent.paper)
        }
    }

    private fun onSubjectTabSelected(index: Int) {
        val data = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(data.copy(selectedSubjectIndex = index)) }
    }

    private fun onClassSelected(classId: String) {
        val data = (_uiState.value as? UiState.Success)?.data ?: return
        val selectedClass = data.classes.firstOrNull { it.id == classId } ?: return
        val yrId = data.selectedYear?.yrID ?: 0
        _uiState.update {
            UiState.Success(data.copy(selectedClass = selectedClass, showClassSheet = false))
        }
        fetchQuestionPapers(classId.toIntOrNull() ?: 0, yrId)
    }

    private fun onYearSelected(yrId: Int) {
        val data = (_uiState.value as? UiState.Success)?.data ?: return
        val selectedYear = data.academicYears.firstOrNull { it.yrID == yrId } ?: return
        val classId = data.selectedClass?.id?.toIntOrNull() ?: data.selectedClassId
        _uiState.update {
            UiState.Success(data.copy(selectedYear = selectedYear, showYearSheet = false))
        }
        fetchQuestionPapers(classId, yrId)
    }

    private fun downloadPaper(paper: QuestionPaperItem) {
        val url = paper.file ?: run {
            sendEvent(
                QuestionPaperEvent.ShowMessage(
                    SnackbarMessage("File not available", MessageType.ERROR)
                )
            )
            return
        }
        val fileName = paper.examName ?: "QuestionPaper"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fileDownloader.download(DownloadRequest(url = url, fileName = fileName)).first()
                withContext(Dispatchers.Main) {
                    sendEvent(
                        QuestionPaperEvent.ShowMessage(
                            SnackbarMessage("Download Started", MessageType.INFO)
                        )
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    sendEvent(
                        QuestionPaperEvent.ShowMessage(
                            SnackbarMessage("Download failed. Please try again.", MessageType.ERROR)
                        )
                    )
                }
            }
        }
    }

    private fun viewPaper(paper: QuestionPaperItem) {
        val url = paper.file ?: run {
            sendEvent(
                QuestionPaperEvent.ShowMessage(
                    SnackbarMessage("File not available", MessageType.ERROR)
                )
            )
            return
        }
        val title = paper.examName ?: "Question Paper"
        sendEvent(QuestionPaperEvent.NavigateToViewer(title = title, url = url))
    }
}

@Immutable
data class QuestionPaperUiState(
    val isStaff: Boolean = false,
    val classes: List<Class> = emptyList(),
    val selectedClass: Class? = null,
    val selectedClassId: Int = 0,
    val academicYears: List<QPAcademicYear> = emptyList(),
    val selectedYear: QPAcademicYear? = null,
    val subjectPapers: List<SubjectPapers> = emptyList(),
    val subjectTabs: List<String> = emptyList(),
    val selectedSubjectIndex: Int = 0,
    val isPapersLoading: Boolean = false,
    val showClassSheet: Boolean = false,
    val showYearSheet: Boolean = false,
)

sealed interface QuestionPaperIntent {
    data object OnBackClicked : QuestionPaperIntent
    data class OnSubjectTabSelected(val index: Int) : QuestionPaperIntent
    data object OnClassSelectClicked : QuestionPaperIntent
    data class OnClassSelected(val classId: String) : QuestionPaperIntent
    data object OnDismissClassSheet : QuestionPaperIntent
    data object OnYearSelectClicked : QuestionPaperIntent
    data class OnYearSelected(val yrId: Int) : QuestionPaperIntent
    data object OnDismissYearSheet : QuestionPaperIntent
    data class OnDownloadClicked(val paper: QuestionPaperItem) : QuestionPaperIntent
    data class OnViewClicked(val paper: QuestionPaperItem) : QuestionPaperIntent
}

sealed interface QuestionPaperEvent {
    data object NavigateBack : QuestionPaperEvent
    data class NavigateToViewer(val title: String, val url: String) : QuestionPaperEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : QuestionPaperEvent
}
