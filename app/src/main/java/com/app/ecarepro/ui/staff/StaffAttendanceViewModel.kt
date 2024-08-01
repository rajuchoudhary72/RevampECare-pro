package com.app.ecarepro.ui.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.StaffAttendanceDetails
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StaffAttendanceViewModel @Inject constructor(
    private val userRepository: UserRepository, private val messageRepository: MessageRepository
) : ViewModel() {

    val loadState = MutableStateFlow<LoadingState>(LoadingState.Loading)

    val isSearchViewVisible = MutableStateFlow(false)

    val searchQuery = MutableStateFlow("")
    val date = MutableStateFlow(getFormatedDate())
    private val sortByDesignation = MutableStateFlow(SortBy.ASC)
    private val sortByName = MutableStateFlow(SortBy.ASC)
    private val attendanceType = MutableStateFlow(AttendanceType.ALL)
    val selectStaffType = MutableStateFlow<StaffType?>(null)
    val staffTypes = MutableStateFlow<List<StaffType>>(emptyList())
    val attendance = MutableStateFlow<List<StaffAttendanceDetails>>(emptyList())

    init {
        loadData()
    }

    private fun loadData() {
        loadState.update { LoadingState.Loading }
        viewModelScope.launch {
            messageRepository.getStaffTypes().collectLatest {
                it.onSuccess { types ->
                    loadState.update { LoadingState.Success }
                    staffTypes.update { types }
                }.onFailure { error ->
                    loadState.update { LoadingState.Error(error) }
                }
            }
            getAttendance()
        }
    }

    private fun getAttendance() {
        loadState.update { LoadingState.Loading }
        viewModelScope.launch {
            userRepository.getStaffAttendance(
                date = date.value,
                staffType = selectStaffType.value?.staffTypeID?.toString()
            ).collectLatest {
                it.onSuccess { items ->
                    loadState.update { LoadingState.Success }
                    attendance.update { items }
                }.onFailure { error ->
                    loadState.update { LoadingState.Error(error) }
                }
            }
        }
    }


    val uiState = combine(
        attendance, searchQuery, sortByDesignation, sortByName, attendanceType
    ) { attendance: List<StaffAttendanceDetails>, searchQuery: String, sortByDesignation: SortBy, sortByName: SortBy, attendanceType:AttendanceType ->
        StaffAttendanceUiState.Success(
            attendance = attendance.filter { it.name.contains(searchQuery, ignoreCase = true) }
                .sortedBy { it.name }.apply {
                    if (sortByName == SortBy.DESC) {
                        reversed()
                    }

                    if (sortByDesignation == SortBy.DESC) {
                        sortedBy { it.designation }
                    }

                    if(attendanceType == AttendanceType.ABSENT){
                        filter { it.isAbsent == true || it.isHoliday == true }
                    }else if(attendanceType == AttendanceType.PRESENT){
                        filter { it.isPrasent == true }
                    }
                },
            searchQuery = searchQuery,
            sortByDesignation = sortByDesignation,
            sortByName = sortByName
        )
    }

    fun toggleSortByDesignation() {
        sortByDesignation.update {
            if (it == SortBy.ASC) SortBy.DESC else SortBy.ASC
        }
    }

    fun toggleSortByName() {
        sortByName.update {
            if (it == SortBy.ASC) SortBy.DESC else SortBy.ASC
        }
    }

    fun setAttendanceType(attendanceType: AttendanceType) {
        this@StaffAttendanceViewModel.attendanceType.update {
            attendanceType
        }
    }

    fun clearSearchQuery() {
        if (searchQuery.value.isEmpty()) {
            showSearchView(false)
        } else {
            searchQuery.update { "" }
        }
    }

    fun showSearchView(show: Boolean = true) {
        isSearchViewVisible.update { show }
    }

    fun selectDate(date: Date) {
        this@StaffAttendanceViewModel.date.update {
            getFormatedDate(date)
        }
        getAttendance()
    }

    fun selectStaffType(staffType: StaffType) {
        selectStaffType.update {
            staffType
        }
        getAttendance()
    }
}

sealed interface LoadingState {
    object Loading : LoadingState
    object Success : LoadingState
    data class Error(val error: Throwable) : LoadingState
}


sealed interface StaffAttendanceUiState {
    object Loading : StaffAttendanceUiState

    object NoResultFound : StaffAttendanceUiState

    data class Success(
        val attendance: List<StaffAttendanceDetails>,
        val selectStaffType: String? = null,
        val searchQuery: String = "",
        val sortByDesignation: SortBy = SortBy.ASC,
        val sortByName: SortBy = SortBy.ASC,
    ) : StaffAttendanceUiState

    data class Error(
        val error: Throwable
    ) : StaffAttendanceUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

enum class SortBy {
    ASC, DESC
}

enum class AttendanceType {
    ALL, PRESENT, ABSENT
}

fun getFormatedDate(date: Date = Date()): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(date)
}