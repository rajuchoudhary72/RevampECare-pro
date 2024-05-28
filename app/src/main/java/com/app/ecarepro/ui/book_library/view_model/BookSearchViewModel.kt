package com.app.ecarepro.ui.book_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookSearchViewModel  @Inject constructor(
    private val  userRepository: UserRepository

) : ViewModel() {


    private val bookSearchStateFlow: MutableStateFlow<NetworkResult<NetworkBookDetails>> = MutableStateFlow(
        NetworkResult.Loading())
    val _bookSearchStateFlow: StateFlow<NetworkResult<NetworkBookDetails>> = bookSearchStateFlow

    fun getLibrarySearch( query: String,pg: Int  )=viewModelScope.launch {
        runCatching {
            bookSearchStateFlow.value = NetworkResult.Loading()
            userRepository.getLibrarySearch(query, pg)
        }.onSuccess {
            bookSearchStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            bookSearchStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}