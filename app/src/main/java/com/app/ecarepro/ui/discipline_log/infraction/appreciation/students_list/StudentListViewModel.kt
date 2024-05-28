package com.app.ecarepro.ui.discipline_log.infraction.appreciation.students_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val studentListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentList>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentListStateFlow: StateFlow<NetworkResult<NetworkStudentList>> = studentListMutableStateFlow

    fun  getStudentList(
        scholarType: Int,
        showAll: Boolean
    )=viewModelScope.launch {
        runCatching {
            studentListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getStudentList(scholarType, showAll)
        }.onSuccess {
            studentListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}

