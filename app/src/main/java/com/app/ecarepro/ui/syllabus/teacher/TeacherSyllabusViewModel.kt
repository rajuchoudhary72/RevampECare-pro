package com.app.ecarepro.ui.syllabus.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkTeacherSyllabus
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TeacherSyllabusViewModel @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {

    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    var isDataLoaded= false
    var isAll= true
    var lastSpinnerPos=0


    private val teacherSyllabusMutableStateFlow: MutableLiveData<NetworkResult<NetworkTeacherSyllabus>> = MutableLiveData(
        NetworkResult.Loading())
    val teacherSyllabusStateFlow: LiveData<NetworkResult<NetworkTeacherSyllabus>> = teacherSyllabusMutableStateFlow


    private val deleteSyllabusMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val deleteSyllabusStateFlow: StateFlow<NetworkResult<CommonResponse>> = deleteSyllabusMutableStateFlow


    fun getTeacherSyllabuses( )=viewModelScope.launch {
        runCatching {
            teacherSyllabusMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getTeacherSyllabuses( )
        }.onSuccess {
            teacherSyllabusMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            teacherSyllabusMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun deleteSyllabus(
        ID: String
    )=viewModelScope.launch {
        runCatching {
            deleteSyllabusMutableStateFlow.value = NetworkResult.Loading()
            userRepository.deleteSyllabus( ID)
        }.onSuccess {
            deleteSyllabusMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            deleteSyllabusMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun showSearchBar() {
        showSearchView.update { true }
    }

    fun clearSearchQuery() {
        if (searchQuery.value.isEmpty()) {
            showSearchView.update { false }
        } else
            searchQuery.update {
                ""
            }
    }

}