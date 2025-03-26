package com.app.ecarepro.ui.report.lessonplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllStaffListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")


    private val staffListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStaffList>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val staffListStateFlow: StateFlow<NetworkResult<NetworkStaffList>> = staffListMutableStateFlow

    init {
        getStaffList()
    }

    private fun getStaffList() = viewModelScope.launch {
        runCatching {
            staffListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.reportLessonteachersList()
        }.onSuccess {
            staffListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            staffListMutableStateFlow.value = NetworkResult.Error(it.message)
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

