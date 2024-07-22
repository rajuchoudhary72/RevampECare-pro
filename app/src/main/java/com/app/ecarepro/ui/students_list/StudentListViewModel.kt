package com.app.ecarepro.ui.students_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    var schoolCode : String = ""

    init {
        viewModelScope.launch {
            schoolCode = userDataStore.getSchoolData()?.schoolCode.toString()
        }
    }

    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

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


    fun validateSchoolCode( schoolCode:String,  onResponse: (NetworkSchool?) -> Unit) {
        viewModelScope.launch {
            schoolRepository.validateSchoolCode(schoolCode.uppercase(Locale.getDefault())).collectLatest {
                onResponse(it)
            }
        }
    }

}

