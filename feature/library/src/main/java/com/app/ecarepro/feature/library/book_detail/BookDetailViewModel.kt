package com.app.ecarepro.feature.library.book_detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.library.LibraryBook
import com.app.ecarepro.core.domain.repository.LibraryRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.library.navigation.LibraryNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BookDetailViewModel.Factory::class)
class BookDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: LibraryNavGraph.BookDetail,
    private val repository: LibraryRepository,
) : BaseViewModel<BookDetailIntent, BookDetailEvent>() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<LibraryNavGraph.BookDetail, BookDetailViewModel> {
        override fun create(param: LibraryNavGraph.BookDetail): BookDetailViewModel
    }

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(BookDetailIntent.LoadData)
    }

    override fun handleIntent(intent: BookDetailIntent) {
        when (intent) {
            is BookDetailIntent.LoadData -> loadBookDetail()
            is BookDetailIntent.OnBackClicked -> sendEvent(BookDetailEvent.NavigateBack)
        }
    }

    private fun loadBookDetail() {
        viewModelScope.launch {
            repository.getBookDetail(navKey.bookId).collect { result ->
                result.onSuccess { book ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            title = book.title,
                            coverImageURL = book.coverImg,
                            isAvailable = book.status.equals("Available", ignoreCase = true),
                            fields = buildDetailFields(book),
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(BookDetailEvent.ShowMessage(SnackbarMessage(error.message ?: "Error", MessageType.ERROR)))
                }
            }
        }
    }

    private fun buildDetailFields(book: LibraryBook): List<BookDetailField> {
        return listOf(
            BookDetailField(label = "Accession No", value = book.accessionNumber.ifEmpty { "-" }),
            BookDetailField(label = "Book title", value = book.title.ifEmpty { "-" }),
            BookDetailField(label = "Author", value = book.author.ifEmpty { "-" }),
            BookDetailField(label = "Subject", value = book.subject.ifEmpty { "-" }),
            BookDetailField(label = "Storage Hint", value = book.storageHint.ifEmpty { "-" }),
            BookDetailField(label = "Year of Publication", value = book.publicationYear.ifEmpty { "-" }),
            BookDetailField(label = "Book No", value = book.bookNo.ifEmpty { "-" }),
            BookDetailField(label = "Class no", value = book.classNo.ifEmpty { "-" }),
            BookDetailField(label = "Issuable", value = if (book.issuable?.equals("Yes", ignoreCase = true) == true) "Yes" else "No"),
            BookDetailField(label = "Status", value = book.status.ifEmpty { "-" }, isStatusField = true),
        )
    }
}

@Immutable
data class BookDetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val title: String = "",
    val coverImageURL: String? = null,
    val isAvailable: Boolean = false,
    val fields: List<BookDetailField> = emptyList(),
)

@Immutable
data class BookDetailField(
    val label: String,
    val value: String,
    val isStatusField: Boolean = false,
)

sealed interface BookDetailIntent {
    data object LoadData : BookDetailIntent
    data object OnBackClicked : BookDetailIntent
}

sealed interface BookDetailEvent {
    data object NavigateBack : BookDetailEvent
    data class ShowMessage(val message: SnackbarMessage) : BookDetailEvent
}
