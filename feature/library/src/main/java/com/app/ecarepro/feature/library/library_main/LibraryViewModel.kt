package com.app.ecarepro.feature.library.library_main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.library.LibraryBook
import com.app.ecarepro.core.domain.repository.LibraryRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository,
) : BaseViewModel<LibraryIntent, LibraryEvent>() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null
    private var activeSearchQuery: String = ""
    private var searchPage: Int = 1
    private var canLoadMoreSearch: Boolean = true
    private var isSearchLoadingMore: Boolean = false

    init {
        handleIntent(LibraryIntent.LoadData)
        setupSearchDebounce()
    }

    override fun handleIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.LoadData -> loadData()
            is LibraryIntent.SelectTab -> selectTab(intent.tab)
            is LibraryIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is LibraryIntent.OnBookClicked -> sendEvent(LibraryEvent.NavigateToBookDetail(intent.bookId))
            is LibraryIntent.ToggleMyAccountCard -> toggleExpansion(intent.bookId)
            is LibraryIntent.OnSearchResultAppeared -> onSearchResultAppeared(intent.bookId)
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collect { text ->
                    val trimmed = text.trim()
                    searchJob?.cancel()
                    if (trimmed.length >= 3) {
                        resetSearchState()
                        activeSearchQuery = trimmed
                        _uiState.update { it.copy(isSearchActive = true) }
                        searchJob = viewModelScope.launch {
                            searchBooks(trimmed, page = 1)
                        }
                    } else {
                        activeSearchQuery = ""
                        _uiState.update {
                            it.copy(
                                isSearchActive = false,
                                searchResults = emptyList(),
                            )
                        }
                        resetSearchState()
                    }
                }
        }
    }

    private fun resetSearchState() {
        searchPage = 1
        canLoadMoreSearch = true
        isSearchLoadingMore = false
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getLibraryData().collect { result ->
                result.onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            latestBooks = data.latestBooks.map { book -> BookCardPresentation.from(book) },
                            myAccountBooks = data.myAccountBooks.map { book -> MyAccountCardPresentation.from(book) },
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(LibraryEvent.ShowMessage(SnackbarMessage(error.message ?: "Error", MessageType.ERROR)))
                }
            }
        }
    }

    private fun selectTab(tab: LibraryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    private fun toggleExpansion(bookId: Int) {
        _uiState.update {
            val expanded = it.expandedBookIds.toMutableSet()
            if (expanded.contains(bookId)) expanded.remove(bookId) else expanded.add(bookId)
            it.copy(expandedBookIds = expanded)
        }
    }

    private suspend fun searchBooks(query: String, page: Int) {
        if (page == 1) {
            _uiState.update { it.copy(isSearchLoading = true) }
        }

        val result = repository.searchBooks(query, page)

        currentCoroutineContext().ensureActive()

        result.onSuccess { books ->
            val presentations = books.map { BookCardPresentation.from(it) }
            _uiState.update {
                it.copy(
                    searchResults = if (page == 1) presentations else it.searchResults + presentations,
                    isSearchLoading = false,
                )
            }
            searchPage = page
            canLoadMoreSearch = books.isNotEmpty()
            isSearchLoadingMore = false
        }.onFailure { error ->
            isSearchLoadingMore = false
            if (page == 1) {
                _uiState.update { it.copy(searchResults = emptyList(), isSearchLoading = false) }
            }
            sendEvent(LibraryEvent.ShowMessage(SnackbarMessage(error.message ?: "Search failed", MessageType.ERROR)))
        }
    }

    private fun onSearchResultAppeared(bookId: Int) {
        val state = _uiState.value
        if (!state.isSearchActive || !canLoadMoreSearch || isSearchLoadingMore) return
        if (activeSearchQuery.isEmpty()) return
        if (state.searchResults.lastOrNull()?.id != bookId) return

        isSearchLoadingMore = true
        val nextPage = searchPage + 1
        val query = activeSearchQuery
        viewModelScope.launch {
            searchBooks(query, nextPage)
        }
    }
}

@Immutable
data class LibraryUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val selectedTab: LibraryTab = LibraryTab.LATEST_BOOKS,
    val searchQuery: String = "",
    val latestBooks: List<BookCardPresentation> = emptyList(),
    val myAccountBooks: List<MyAccountCardPresentation> = emptyList(),
    val isSearchActive: Boolean = false,
    val searchResults: List<BookCardPresentation> = emptyList(),
    val isSearchLoading: Boolean = false,
    val expandedBookIds: Set<Int> = emptySet(),
)

enum class LibraryTab {
    LATEST_BOOKS,
    MY_ACCOUNT;

    fun displayName(): String = when (this) {
        LATEST_BOOKS -> "Latest books"
        MY_ACCOUNT -> "My account"
    }
}

@Immutable
data class BookCardPresentation(
    val id: Int,
    val title: String,
    val author: String,
    val publication: String?,
    val coverImageURL: String?,
) {
    companion object {
        fun from(book: LibraryBook) = BookCardPresentation(
            id = book.bookID,
            title = book.title,
            author = book.author,
            publication = book.publication,
            coverImageURL = book.coverImg,
        )
    }
}

@Immutable
data class MyAccountCardPresentation(
    val id: Int,
    val title: String,
    val coverImageURL: String?,
    val subtitle: String,
    val issuedOn: String,
    val returnDate: String,
    val returnOn: String,
    val fineAmount: String,
    val hasFine: Boolean,
) {
    companion object {
        fun from(book: LibraryBook): MyAccountCardPresentation {
            val costPart = book.cost?.takeIf { it.isNotEmpty() }?.let { "Cost: \u20b9$it" }
            val accPart = book.accessionNumber.takeIf { it.isNotEmpty() }?.let { "Accession No: $it" }
            val subtitle = listOfNotNull(costPart, accPart).joinToString(" \u2022 ")

            val fineAmount = book.fineAmount
            val hasFine = fineAmount != null && fineAmount > 0
            val fineDisplay = if (hasFine) "\u20b9 ${"%.2f".format(fineAmount)}" else "-"

            return MyAccountCardPresentation(
                id = book.bookID,
                title = book.title,
                coverImageURL = book.coverImg,
                subtitle = subtitle,
                issuedOn = book.issuedOn ?: "-",
                returnDate = book.expectedReturnDate ?: "-",
                returnOn = book.returnOn ?: "-",
                fineAmount = fineDisplay,
                hasFine = hasFine,
            )
        }
    }
}

sealed interface LibraryIntent {
    data object LoadData : LibraryIntent
    data class SelectTab(val tab: LibraryTab) : LibraryIntent
    data class OnSearchQueryChanged(val query: String) : LibraryIntent
    data class OnBookClicked(val bookId: Int) : LibraryIntent
    data class ToggleMyAccountCard(val bookId: Int) : LibraryIntent
    data class OnSearchResultAppeared(val bookId: Int) : LibraryIntent
}

sealed interface LibraryEvent {
    data class NavigateToBookDetail(val bookId: Int) : LibraryEvent
    data class ShowMessage(val message: SnackbarMessage) : LibraryEvent
}
