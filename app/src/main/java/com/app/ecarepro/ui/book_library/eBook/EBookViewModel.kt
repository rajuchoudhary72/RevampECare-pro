package com.app.ecarepro.ui.book_library.eBook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkEBook
import com.app.ecarepro.data.network.model.NetworkLibraryDTL
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EBookViewModel  @Inject constructor(
    private val  userRepository: UserRepository

) : ViewModel() {


    private val eBookListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkEBook>> = MutableStateFlow(
        NetworkResult.Loading())
    val eBookListStateFlow: StateFlow<NetworkResult<NetworkEBook>> = eBookListMutableStateFlow

    fun getEBook(
        query: String,
        mode: Int
    )=viewModelScope.launch {
        runCatching {
            eBookListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getEBook(query, mode)
        }.onSuccess {
            eBookListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            eBookListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}