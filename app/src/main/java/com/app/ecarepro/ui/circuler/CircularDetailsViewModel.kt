package com.app.ecarepro.ui.circuler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CircularDetailsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {


    private val circularDTLStateFlow: MutableStateFlow<NetworkResult<NetworkCircularDetails>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val _circularDTLStateFlow: StateFlow<NetworkResult<NetworkCircularDetails>> =
        circularDTLStateFlow

    fun getCircularDTL(cirID: Int, iD: Int) = viewModelScope.launch {
        runCatching {
            circularDTLStateFlow.value = NetworkResult.Loading()
            schoolRepository.getCircularDTL(cirID, iD)

        }.onSuccess {
            circularDTLStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            circularDTLStateFlow.value = NetworkResult.Error(it.message)
        }
    }

}