package com.app.ecarepro.ui.book_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.BookDTL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("") // Holds the search query
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<BookDTL>>(emptyList()) // Initial empty list
    val searchResults: StateFlow<List<BookDTL>> = _searchResults

    private val _bookSearchStateFlow = MutableStateFlow<NetworkResult<Nothing>>(NetworkResult.Loading())
    val bookSearchStateFlow: StateFlow<NetworkResult<Nothing>> = _bookSearchStateFlow

    fun setSearchQuery(query: String) {
        // Trim spaces and normalize the query to avoid unnecessary queries
        _searchQuery.value = query.trim().replace(Regex("\\s+"), " ")
    }

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait 300ms after the last input
                .filter { it.isNotBlank() } // Ignore queries that are empty or only whitespace
                .distinctUntilChanged() // Emit only when the query changes
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        _searchResults.value = emptyList() // Clear search results when query is empty
                        flow { emit(NetworkResult.Loading()) } // Emit loading state for empty query
                    } else {
                        getLibrarySearchFlow(query, 1) // Call the API for the current query
                    }
                }
                .collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _searchResults.value = emptyList() // Clear results while loading
                        }
                        is NetworkResult.Success -> {
                            _searchResults.value = result.data!!.bookDTL ?: emptyList()
                        }
                        is NetworkResult.Error -> {
                            _searchResults.value = emptyList() // Clear results on error
                        }
                    }
                }
        }
    }

    private fun getLibrarySearchFlow(query: String, pg: Int): Flow<NetworkResult<NetworkBookDetails>> = flow {
        emit(NetworkResult.Loading()) // Emit loading state
        runCatching {
            userRepository.getLibrarySearch(query, pg)
        }.onSuccess {
            emit(NetworkResult.Success(it)) // Emit success with data
        }.onFailure {
            emit(NetworkResult.Error(it.message)) // Emit error with message
        }
    }
}
