package com.app.ecarepro.ui.classteacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkClassTeacher
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassTeacherViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    private val classTeachersMutableStateFlow: MutableStateFlow<NetworkResult<NetworkClassTeacher>> = MutableStateFlow(
        NetworkResult.Loading())
    val classTeachersStateFlow: StateFlow<NetworkResult<NetworkClassTeacher>> = classTeachersMutableStateFlow

    fun  getClassTeacher(

    )=viewModelScope.launch {
        runCatching {
            classTeachersMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getClassTeacher( )
        }.onSuccess {
            classTeachersMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            classTeachersMutableStateFlow.value = NetworkResult.Error(it.message)
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

