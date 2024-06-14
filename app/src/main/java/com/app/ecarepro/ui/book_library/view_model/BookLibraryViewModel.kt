package com.app.ecarepro.ui.book_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkLibraryDTL
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookLibraryViewModel  @Inject constructor(
    private val  userRepository: UserRepository

) : ViewModel() {


    private val libraryDTLMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLibraryDTL>> = MutableStateFlow(
        NetworkResult.Loading())
    val libraryDTLStateFlow: StateFlow<NetworkResult<NetworkLibraryDTL>> = libraryDTLMutableStateFlow

    fun getLibraryDTL()=viewModelScope.launch {
        runCatching {
            libraryDTLMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getLibraryDTL()
        }.onSuccess {
            libraryDTLMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            libraryDTLMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}