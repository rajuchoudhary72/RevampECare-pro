package com.app.ecarepro.ui.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.search.SearchPagerFragment.Companion.SEARCH_TYPE
import com.app.ecarepro.ui.search.SearchPagerViewModel.Companion.SEARCH_TYPE_MODULE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchPagerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val userRepository: UserRepository
) : ViewModel() {
    private val searchType = savedStateHandle.getStateFlow(SEARCH_TYPE, SEARCH_TYPE_MODULE)
    private val searchQuery = MutableStateFlow("")

    private val students = mutableListOf<Student>()
    private val staffs = mutableListOf<Staff>()
    private val modules = mutableListOf<Menu>()

    val uiState: Flow<SearchUiState> = combine(
        flow = searchType, flow2 = searchQuery
    ) { type, query ->
        Pair(type, query)
    }.flatMapLatest { (type, query) ->
        when (type) {
            "Module" -> {
                searchModule(query, type)
            }

            "Student" -> {
                searchStudent(query, type)
            }

            else -> {
                searchStaff(query, type)
            }
        }
    }

    private fun searchModule(query: String, type: String): Flow<SearchUiState> = channelFlow {
        if (query.isEmpty()) {
            send(SearchUiState.Default)
        } else {
            send(filterModule(query, type))
        }
    }

    private fun filterModule(query: String, type: String): SearchUiState {
        val filteredModules = modules.filter {
            it.title?.contains(query, true) ?: false
        }
        Log.e("Search", "setModules: ${filteredModules.size}", )
        return if (filteredModules.isEmpty()) {
            SearchUiState.NoResultFound
        } else {
            SearchUiState.Success(
                searchQuery = query,
                searchType = type,
                modules = filteredModules
            )
        }
    }

    private suspend fun searchStudent(query: String, type: String): Flow<SearchUiState> =
        channelFlow {
            if (query.isEmpty()) {
                send(SearchUiState.Default)
            } else {
                if (students.isEmpty()) {
                    send(SearchUiState.Loading)
                    userRepository.getStudents().collectLatest { result ->
                        if (result.isSuccess) {
                            students.addAll(result.getOrNull()!!)
                            send(filterStudents(query, type))
                        } else {
                            send(SearchUiState.Error(result.exceptionOrNull()!!))
                        }
                    }
                } else {
                    send(filterStudents(query, type))
                }
            }
        }

    private fun filterStudents(
        query: String,
        type: String
    ): SearchUiState {
        val filteredStudents = students.filter {
            it.name.contains(query, true) ||
            it.admissionNumber.contains(query, true) ||
            it.`class`.contains(query, true) ||
            it.fatherName.contains(query, true) ||
            it.contactMob.contains(query, true)
        }
        return if (filteredStudents.isEmpty()) {
            SearchUiState.NoResultFound
        } else {
            SearchUiState.Success(
                searchQuery = query,
                searchType = type,
                students = filteredStudents
            )
        }
    }

    private suspend fun searchStaff(query: String, type: String): Flow<SearchUiState> =
        channelFlow {
            if (query.isEmpty()) {
                send(SearchUiState.Default)
            } else {
                if (staffs.isEmpty()) {
                    send(SearchUiState.Loading)
                    userRepository.getStaffs().collectLatest { result ->
                        if (result.isSuccess) {
                            staffs.addAll(result.getOrNull()!!)
                            send(filterStaffs(query, type))
                        } else {
                            send(SearchUiState.Error(result.exceptionOrNull()!!))
                        }
                    }
                } else {
                    send(filterStaffs(query, type))
                }
            }
        }

    private fun filterStaffs(
        query: String,
        type: String
    ): SearchUiState {
        val filteredStaffs = staffs.filter {
            it.name.contains(query, true) ||
            it.designation.contains(query, true) ||
            it.email.contains(query, true) ||
            it.mobile.contains(query, true)
        }
        return if (filteredStaffs.isEmpty()) {
            SearchUiState.NoResultFound
        } else {
            SearchUiState.Success(
                searchQuery = query,
                searchType = type,
                staffs = filteredStaffs
            )
        }
    }

    fun setModules(modules: List<Menu>) {
        this.modules.apply {
            clear()
            addAll(modules)
            Log.e("Search", "setModules: ${modules.size}", )
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    companion object {
        const val SEARCH_TYPE_MODULE = "Module"
        const val SEARCH_TYPE_STUDENT = "Student"
        const val SEARCH_TYPE_STAFF = "Staff"
    }
}


sealed interface SearchUiState {
    object Loading : SearchUiState

    object Default : SearchUiState

    object NoResultFound : SearchUiState

    data class Success(
        val searchQuery: String? = "",
        val searchType: String? = SEARCH_TYPE_MODULE,
        val students: List<Student> = emptyList(),
        val staffs: List<Staff> = emptyList(),
        val modules: List<Menu> = emptyList(),
    ) : SearchUiState

    data class Error(
        val error: Throwable
    ) : SearchUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}