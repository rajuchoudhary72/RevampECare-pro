package com.app.ecarepro.ui.assignment.staff

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkTeacherAssignment
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TeacherAssignmentViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Use a more descriptive variable name and leverage null safety
    private val _staffId: String =
        savedStateHandle.get<String>(Constant.STAFF_ID_ARGUMENT)
            ?: throw IllegalArgumentException("Staff ID is required.")

    // Use a StateFlow for immutable state
    private val _userType = MutableStateFlow<String?>(null)
    val userType: StateFlow<String?> = _userType.asStateFlow()

    init {
        fetchUserRole()
        fetchTeachersAssignments(_staffId)
    }

    // Use StateFlows with backing properties to expose read-only state
    private val _showSearchView = MutableStateFlow(false)
    val showSearchView: StateFlow<Boolean> = _showSearchView.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _teacherAssignmentState =
        MutableStateFlow<NetworkResult<NetworkTeacherAssignment>>(NetworkResult.Loading())
    val teacherAssignmentState: StateFlow<NetworkResult<NetworkTeacherAssignment>> =
        _teacherAssignmentState.asStateFlow()

    private val _deleteAssignmentState =
        MutableStateFlow<NetworkResult<CommonResponse>>(NetworkResult.Loading())
    val deleteAssignmentState: StateFlow<NetworkResult<CommonResponse>> =
        _deleteAssignmentState.asStateFlow()

    // Use a clear and descriptive function name
    private fun fetchUserRole() {
        viewModelScope.launch {
            _userType.value = userDataStore.getRoleName()
        }
    }

    // Use a clear and descriptive function name
    fun fetchTeachersAssignments(staffId: String) = viewModelScope.launch {
        _teacherAssignmentState.value = NetworkResult.Loading()
        try {
            val result = userRepository.teachersAssignment(staffId)
            _teacherAssignmentState.value = NetworkResult.Success(result)

        } catch (e: Exception) {
            _teacherAssignmentState.value =
                NetworkResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    // Use a clear and descriptive function name
    fun deleteAssignment(assignmentId: String) = viewModelScope.launch {
        _deleteAssignmentState.value = NetworkResult.Loading()
        try {
            val result = userRepository.deleteAssignment(assignmentId)
            _deleteAssignmentState.value = NetworkResult.Success(result)
        } catch (e: Exception) {
            _deleteAssignmentState.value =
                NetworkResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    fun showSearchBar() {
        _showSearchView.update { true }
    }

    fun clearSearchQuery() {
        if (_searchQuery.value.isEmpty()) {
            _showSearchView.update { false }
        } else {
            _searchQuery.update { "" }
        }
    }
}