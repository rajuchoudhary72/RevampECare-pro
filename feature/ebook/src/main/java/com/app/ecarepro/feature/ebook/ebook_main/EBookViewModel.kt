package com.app.ecarepro.feature.ebook.ebook_main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.ebook.EBook
import com.app.ecarepro.core.domain.repository.EBookRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EBookViewModel @Inject constructor(
    private val repository: EBookRepository,
) : BaseViewModel<EBookIntent, EBookEvent>() {

    private val _uiState = MutableStateFlow(EBookUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var isInitialLoad = true

    init {
        handleIntent(EBookIntent.LoadData)
        setupSearchDebounce()
    }

    override fun handleIntent(intent: EBookIntent) {
        when (intent) {
            is EBookIntent.LoadData -> fetchEBooks(query = "''", mode = 0)
            is EBookIntent.SelectTab -> _uiState.update { it.copy(selectedTab = intent.tab) }
            is EBookIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is EBookIntent.OnEBookClicked -> openEBook(intent.book)
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collect { text ->
                    if (isInitialLoad) return@collect
                    val trimmed = text.trim()
                    when {
                        trimmed.isEmpty() -> fetchEBooks(query = "''", mode = 0)
                        trimmed.length >= 3 -> fetchEBooks(query = trimmed, mode = 1)
                        // 1-2 chars: do nothing, wait for more input
                    }
                }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    private fun fetchEBooks(query: String, mode: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = isInitialLoad,
                    isSilentLoading = !isInitialLoad,
                )
            }

            repository.getEBooks(query, mode).collect { result ->
                result.onSuccess { (books, megaLink) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSilentLoading = false,
                            isError = false,
                            eBooks = books.map { book -> EBookCardPresentation.from(book) },
                            megaBookLink = if (isInitialLoad) megaLink else it.megaBookLink,
                        )
                    }
                    isInitialLoad = false
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSilentLoading = false,
                            isError = isInitialLoad,
                        )
                    }
                    if (!isInitialLoad) {
                        sendEvent(
                            EBookEvent.ShowMessage(
                                SnackbarMessage(error.message ?: "Error", MessageType.ERROR)
                            )
                        )
                    }
                    isInitialLoad = false
                }
            }
        }
    }

    private fun openEBook(book: EBookCardPresentation) {
        if (_uiState.value.isSilentLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSilentLoading = true) }

            repository.getOnlineCode(book.accessionNo).collect { result ->
                result.onSuccess { pdfUrl ->
                    _uiState.update { it.copy(isSilentLoading = false) }
                    if (pdfUrl.isNotBlank()) {
                        sendEvent(EBookEvent.OpenPdf(url = pdfUrl, title = book.title))
                    } else {
                        sendEvent(
                            EBookEvent.ShowMessage(
                                SnackbarMessage("Unable to load e-book", MessageType.ERROR)
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isSilentLoading = false) }
                    sendEvent(
                        EBookEvent.ShowMessage(
                            SnackbarMessage(error.message ?: "Unable to load e-book", MessageType.ERROR)
                        )
                    )
                }
            }
        }
    }
}

@Immutable
data class EBookUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isSilentLoading: Boolean = false,
    val selectedTab: EBookTab = EBookTab.MY_SCHOOL_LIBRARY,
    val searchQuery: String = "",
    val eBooks: List<EBookCardPresentation> = emptyList(),
    val megaBookLink: String? = null,
)

enum class EBookTab {
    MY_SCHOOL_LIBRARY,
    MEGA_EBOOK;

    fun displayName(): String = when (this) {
        MY_SCHOOL_LIBRARY -> "My school library"
        MEGA_EBOOK -> "Mega E-book"
    }
}

@Immutable
data class EBookCardPresentation(
    val id: String,
    val title: String,
    val author: String,
    val coverImageURL: String?,
    val accessionNo: String,
) {
    companion object {
        fun from(book: EBook) = EBookCardPresentation(
            id = book.accessionNo.ifEmpty { book.bookID.toString() },
            title = book.title,
            author = book.author,
            coverImageURL = book.coverImg,
            accessionNo = book.accessionNo,
        )
    }
}

sealed interface EBookIntent {
    data object LoadData : EBookIntent
    data class SelectTab(val tab: EBookTab) : EBookIntent
    data class OnSearchQueryChanged(val query: String) : EBookIntent
    data class OnEBookClicked(val book: EBookCardPresentation) : EBookIntent
}

sealed interface EBookEvent {
    data class OpenPdf(val url: String, val title: String) : EBookEvent
    data class ShowMessage(val message: SnackbarMessage) : EBookEvent
}
