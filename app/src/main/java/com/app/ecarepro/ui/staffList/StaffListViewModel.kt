package com.app.ecarepro.ui.staffList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Assuming these are defined elsewhere:
// data class NetworkStaffList(...)
// interface UserRepository { suspend fun getStaffList(): NetworkStaffList }
// sealed class NetworkResult<T>(...) { data class Success<T>(val data: T) : NetworkResult<T>(data) ; data class Error<T>(val message: String?) : NetworkResult<T>(null); class Loading<T> : NetworkResult<T>(null)}

@HiltViewModel
class StaffListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    //region Search State
    private val _showSearchView = MutableStateFlow(false)
    val showSearchView: StateFlow<Boolean> = _showSearchView.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    //endregion


    //region Staff List State
    private val _staffListState: MutableStateFlow<NetworkResult<NetworkStaffList>> =
        MutableStateFlow(NetworkResult.Loading())
    val staffListState: StateFlow<NetworkResult<NetworkStaffList>> = _staffListState.asStateFlow()
    //endregion

    init {
        refreshStaffList()
    }

    /**
     * Fetches the staff list from the repository.
     *
     * Updates the [_staffListState] based on the result of the network operation.
     */
    fun refreshStaffList() {
        viewModelScope.launch {
            _staffListState.update { NetworkResult.Loading() } //Immediately set loading state
            try {
                val staffList = userRepository.getStaffList()
                _staffListState.update { NetworkResult.Success(staffList) }
            } catch (e: Exception) {
                _staffListState.update { NetworkResult.Error(e.message) }
            }
        }
    }

    /**
     * Shows the search bar.
     */
    fun showSearchBar() {
        _showSearchView.update { true }
    }

    /**
     * Clears the search query. If the query was already empty, hides the search view.
     */
    fun clearSearchQuery() {
        _searchQuery.update { currentQuery ->
            if (currentQuery.isEmpty()) {
                _showSearchView.update { false }
                ""
            } else {
                ""
            }
        }
    }
}

