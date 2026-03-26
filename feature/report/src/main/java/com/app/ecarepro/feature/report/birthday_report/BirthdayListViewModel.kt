package com.app.ecarepro.feature.report.birthday_report

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.birthday.Birthday
import com.app.ecarepro.core.domain.repository.BirthdayRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BirthdayListViewModel @Inject constructor(
    private val repository: BirthdayRepository,
) : BaseViewModel<BirthdayListIntent, BirthdayListEvent>() {

    private val _uiState = MutableStateFlow(BirthdayListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(BirthdayListIntent.LoadData)
    }

    override fun handleIntent(intent: BirthdayListIntent) {
        when (intent) {
            is BirthdayListIntent.LoadData -> fetchBirthdays()
            is BirthdayListIntent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = intent.tab) }
                fetchBirthdays()
            }
            is BirthdayListIntent.SelectFilterMode -> {
                _uiState.update { it.copy(filterMode = intent.mode) }
                fetchBirthdays()
            }
            is BirthdayListIntent.SelectMonth -> {
                _uiState.update { it.copy(selectedMonthIndex = intent.monthIndex) }
                fetchBirthdays()
            }
            is BirthdayListIntent.SelectDate -> {
                val calendar = Calendar.getInstance().apply { time = intent.date }
                val monthIndex = calendar.get(Calendar.MONTH)
                _uiState.update { it.copy(selectedDate = intent.date, selectedMonthIndex = monthIndex) }
                fetchBirthdays()
            }
            is BirthdayListIntent.ShowFilterModeSheet -> _uiState.update { it.copy(showFilterModeSheet = true) }
            is BirthdayListIntent.DismissFilterModeSheet -> _uiState.update { it.copy(showFilterModeSheet = false) }
            is BirthdayListIntent.ShowMonthSheet -> _uiState.update { it.copy(showMonthSheet = true) }
            is BirthdayListIntent.DismissMonthSheet -> _uiState.update { it.copy(showMonthSheet = false) }
            is BirthdayListIntent.ShowDatePicker -> _uiState.update { it.copy(showDatePicker = true) }
            is BirthdayListIntent.DismissDatePicker -> _uiState.update { it.copy(showDatePicker = false) }
            is BirthdayListIntent.OnBackClicked -> sendEvent(BirthdayListEvent.NavigateBack)
        }
    }

    private fun fetchBirthdays() {
        val state = _uiState.value
        val userType = state.selectedTab.userType
        val rptType = state.filterMode.rptType
        val monthNo = state.selectedMonthIndex + 1
        val date: String? = if (state.filterMode == FilterMode.DATE) {
            SimpleDateFormat("yyyy-M-d", Locale.US).format(state.selectedDate)
        } else null

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getBirthdays(userType, rptType, monthNo, date).collect { result ->
                result.onSuccess { birthdays ->
                    _uiState.update { s ->
                        s.copy(
                            isLoading = false,
                            birthdays = birthdays.map { BirthdayCardPresentation.from(it, s.selectedTab) },
                            error = null,
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Something went wrong") }
                }
            }
        }
    }
}

@Immutable
data class BirthdayListUiState(
    val selectedTab: BirthdayTab = BirthdayTab.STUDENTS,
    val filterMode: FilterMode = FilterMode.MONTH,
    val selectedMonthIndex: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedDate: Date = Date(),
    val birthdays: List<BirthdayCardPresentation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showFilterModeSheet: Boolean = false,
    val showMonthSheet: Boolean = false,
    val showDatePicker: Boolean = false,
)

sealed interface BirthdayListIntent {
    data object LoadData : BirthdayListIntent
    data class SelectTab(val tab: BirthdayTab) : BirthdayListIntent
    data class SelectFilterMode(val mode: FilterMode) : BirthdayListIntent
    data class SelectMonth(val monthIndex: Int) : BirthdayListIntent
    data class SelectDate(val date: Date) : BirthdayListIntent
    data object ShowFilterModeSheet : BirthdayListIntent
    data object DismissFilterModeSheet : BirthdayListIntent
    data object ShowMonthSheet : BirthdayListIntent
    data object DismissMonthSheet : BirthdayListIntent
    data object ShowDatePicker : BirthdayListIntent
    data object DismissDatePicker : BirthdayListIntent
    data object OnBackClicked : BirthdayListIntent
}

sealed interface BirthdayListEvent {
    data object NavigateBack : BirthdayListEvent
}

enum class BirthdayTab(val userType: String, val label: String) {
    STUDENTS(userType = "1", label = "Students"),
    PARENTS(userType = "2", label = "Parents"),
    STAFF(userType = "3", label = "Staff"),
}

enum class FilterMode(val rptType: Int, val label: String) {
    MONTH(rptType = 2, label = "Month"),
    DATE(rptType = 1, label = "Date"),
}

@Immutable
data class BirthdayCardPresentation(
    val id: Int,
    val name: String,
    val phoneNo: String?,
    val subtitle: String,
    val birthdayOn: String,
    val photoUrl: String?,
) {
    companion object {
        fun from(birthday: Birthday, tab: BirthdayTab): BirthdayCardPresentation {
            val name: String
            val subtitle: String
            when (tab) {
                BirthdayTab.STUDENTS -> {
                    name = listOfNotNull(birthday.name, birthday.className).joinToString(", ")
                    subtitle = ""
                }
                BirthdayTab.PARENTS -> {
                    name = birthday.name
                    subtitle = when {
                        !birthday.fatherName.isNullOrBlank() -> {
                            val cls = birthday.className?.let { ", $it" }.orEmpty()
                            "F/O: ${birthday.fatherName}$cls"
                        }
                        !birthday.motherName.isNullOrBlank() -> {
                            val cls = birthday.className?.let { ", $it" }.orEmpty()
                            "M/O: ${birthday.motherName}$cls"
                        }
                        else -> ""
                    }
                }
                BirthdayTab.STAFF -> {
                    name = listOfNotNull(birthday.name, birthday.designation).joinToString(", ")
                    subtitle = birthday.fatherName?.let { "Father/Spouse: $it" }.orEmpty()
                }
            }
            return BirthdayCardPresentation(
                id = birthday.id,
                name = name,
                phoneNo = birthday.mobile,
                subtitle = subtitle,
                birthdayOn = birthday.birthdayOn,
                photoUrl = birthday.photo,
            )
        }
    }
}
