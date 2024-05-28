package com.app.ecarepro.ui.timeTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeTableNavHostViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val timeTableMutableStateFlow: MutableStateFlow<NetworkResult<NetworkTeachersTimetable>> = MutableStateFlow(
        NetworkResult.Loading())
    val timeTableStateFlow: StateFlow<NetworkResult<NetworkTeachersTimetable>> = timeTableMutableStateFlow

    fun teachersTimetable(
        id: String
    )=viewModelScope.launch {
        runCatching {
            timeTableMutableStateFlow.value = NetworkResult.Loading()
            userRepository.teachersTimetable( id)
        }.onSuccess {
            timeTableMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            timeTableMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}