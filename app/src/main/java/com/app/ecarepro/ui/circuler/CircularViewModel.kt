package com.app.ecarepro.ui.circuler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Circular
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CircularViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val circularsStateFlow: MutableLiveData<NetworkResult<NetworkCircular>> = MutableLiveData<NetworkResult<NetworkCircular>>(
        NetworkResult.Loading())
    val _circularsStateFlowStateFlow: LiveData<NetworkResult<NetworkCircular>> = circularsStateFlow

    var pageIndex = 1
    var isFirst=true
    var cacheListData :  ArrayList<Circular> = ArrayList()
    var academicYear=""
    var academicYearID=0
    var lastSearchQuery: String = ""


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