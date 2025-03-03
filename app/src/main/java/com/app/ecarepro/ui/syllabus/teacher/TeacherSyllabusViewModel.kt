package com.app.ecarepro.ui.syllabus.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkTeacherSyllabus
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TeacherSyllabusViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _showSearchView = MutableStateFlow(false)
    val showSearchView: StateFlow<Boolean> = _showSearchView.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun showSearchBar() {
        _showSearchView.update { true }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun clearSearchQuery() {
        if (_searchQuery.value.isEmpty()) {
            _showSearchView.update { false }
        } else {
            _searchQuery.update { "" }
        }
    }

    private val _teacherSyllabusState =
        MutableStateFlow<NetworkResult<NetworkTeacherSyllabus>>(NetworkResult.Loading())
    val teacherSyllabusState: StateFlow<NetworkResult<NetworkTeacherSyllabus>> =
        _teacherSyllabusState.asStateFlow()

    init {
        getTeacherSyllabuses()
    }

    fun getTeacherSyllabuses() {
        viewModelScope.launch {
            _teacherSyllabusState.update { NetworkResult.Loading() }
            try {
                val result = userRepository.getTeacherSyllabuses()
                _teacherSyllabusState.update { NetworkResult.Success(result) }
            } catch (e: Exception) {
                _teacherSyllabusState.update {
                    NetworkResult.Error(
                        e.message ?: "An error occurred"
                    )
                }
            }
        }
    }

    private val _deleteSyllabusState =
        MutableStateFlow<NetworkResult<CommonResponse>>(NetworkResult.Loading())
    val deleteSyllabusState: StateFlow<NetworkResult<CommonResponse>> =
        _deleteSyllabusState.asStateFlow()

    fun deleteSyllabus(syllabusId: String) {
        viewModelScope.launch {
            _deleteSyllabusState.update { NetworkResult.Loading() }
            try {
                val result = userRepository.deleteSyllabus(syllabusId)
                _deleteSyllabusState.update { NetworkResult.Success(result) }
            } catch (e: Exception) {
                _deleteSyllabusState.update {
                    NetworkResult.Error(
                        e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
}