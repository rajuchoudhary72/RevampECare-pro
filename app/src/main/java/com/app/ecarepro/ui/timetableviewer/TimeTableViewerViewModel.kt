package com.app.ecarepro.ui.timetableviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.data.network.model.NetworkTimeTableViewer
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeTableViewerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val timeTableViewerMutableStateFlow: MutableStateFlow<NetworkResult<NetworkTimeTableViewer>> = MutableStateFlow(
        NetworkResult.Loading())
    val timeTableViewerStateFlow: StateFlow<NetworkResult<NetworkTimeTableViewer>> = timeTableViewerMutableStateFlow

    init {
        getTimetableViewer()
    }

    private fun getTimetableViewer() = viewModelScope.launch {
        runCatching {
            timeTableViewerMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getTimetableViewer(  )
        }.onSuccess {
            timeTableViewerMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            timeTableViewerMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}