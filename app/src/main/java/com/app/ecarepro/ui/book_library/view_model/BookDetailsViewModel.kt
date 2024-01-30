package com.app.ecarepro.ui.book_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.LibraryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val libraryRepo: LibraryRepo
) : ViewModel() {


    private val bookDetailsStateFlow: MutableStateFlow<NetworkResult<NetworkBookDetails>> = MutableStateFlow(
        NetworkResult.Loading())
    val _bookDetailsStateFlow: StateFlow<NetworkResult<NetworkBookDetails>> = bookDetailsStateFlow

    fun getBookDTL( bookID: Int,id: Int  )=viewModelScope.launch {
        runCatching {
            bookDetailsStateFlow.value = NetworkResult.Loading()
            libraryRepo.getBookDTL(bookID, id )
        }.onSuccess {
            bookDetailsStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            bookDetailsStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}