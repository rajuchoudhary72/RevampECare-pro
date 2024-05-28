package com.app.ecarepro.ui.book_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LatestBookViewModel @Inject constructor(
    private val  userRepository: UserRepository

) :  ViewModel() {



    private val latestBookStateFlow: MutableStateFlow<NetworkResult<NetworkLatestBook>> = MutableStateFlow(
        NetworkResult.Loading())
    val _latestBookStateFlow: StateFlow<NetworkResult<NetworkLatestBook>> = latestBookStateFlow

    fun getLibraryDetails(  )=viewModelScope.launch {
        runCatching {
            latestBookStateFlow.value = NetworkResult.Loading()
            userRepository.getLibraryDetails( )
        }.onSuccess {
            latestBookStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            latestBookStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}