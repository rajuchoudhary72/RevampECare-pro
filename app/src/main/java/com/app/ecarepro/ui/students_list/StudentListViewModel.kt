package com.app.ecarepro.ui.students_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var schoolCode: String = ""

    val schoolType = MutableStateFlow(2)
    val toFragment = savedStateHandle.getStateFlow(Constant.TO, "")

    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    val studentListStateFlow: Flow<NetworkResult<NetworkStudentList>> = combine(
        flow = schoolType,
        flow2 = toFragment
    ) { scholarType, toFragment ->
        var showAll = false
        if (toFragment == Constant.FRA_ADD_APPRE || toFragment == Constant.FRA_VIEW_APPRE || toFragment == Constant.FRA_VIEW_INFE || toFragment == Constant.FRA_ADD_INFE) {
            showAll = userDataStore.isGeneralSettingEnabled("DisciplineLogStudent")
        }

        val result = runCatching {
            userRepository.getStudentList(scholarType, showAll)
        }

        if (result.isSuccess) {
            NetworkResult.Success(result.getOrNull()!!)
        } else {
            NetworkResult.Error(result.exceptionOrNull()?.message)

        }
    }.onStart {
        emit(NetworkResult.Loading())
    }

    init {
        viewModelScope.launch {
            schoolCode = userDataStore.getSchoolData()?.schoolCode.toString()
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


    fun validateSchoolCode(schoolCode: String, onResponse: (NetworkSchool?) -> Unit) {
        viewModelScope.launch {
            schoolRepository.validateSchoolCode(schoolCode.uppercase(Locale.getDefault()))
                .collectLatest {
                    onResponse(it)
                }
        }
    }

}

