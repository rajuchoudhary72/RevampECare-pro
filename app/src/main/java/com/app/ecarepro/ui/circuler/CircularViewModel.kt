package com.app.ecarepro.ui.circuler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CircularViewModel  @Inject constructor(
    private val schoolRepository: SchoolRepository,
 ) : ViewModel() {

    private val circularsStateFlow: MutableStateFlow<NetworkResult<NetworkCircular>> = MutableStateFlow(
        NetworkResult.Loading())
    val _circularsStateFlowStateFlow: StateFlow<NetworkResult<NetworkCircular>> = circularsStateFlow

    fun getCirculars(pg: Int,yrID: Int,title :String)=viewModelScope.launch {
       runCatching {
           circularsStateFlow.value =NetworkResult.Loading()
           schoolRepository.getCirculars(pg, yrID,title)
       }.onSuccess {
           circularsStateFlow.value =NetworkResult.Success(it)
       }.onFailure {
           circularsStateFlow.value = NetworkResult.Error(it.message)
       }

    }


}