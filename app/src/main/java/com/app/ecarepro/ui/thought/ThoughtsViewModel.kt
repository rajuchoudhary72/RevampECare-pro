package com.app.ecarepro.ui.thought

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.repository.ThoughtsRepo
import com.app.ecarepro.model.Notice
import com.app.ecarepro.utils.ResponseState
import com.app.ecarepro.utils.ResponseStateCreateTou
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThoughtsViewModel @Inject constructor(
    private val  thoughtsRepo: ThoughtsRepo
) : ViewModel() {

    private val postStateFlow:MutableStateFlow<ResponseState> = MutableStateFlow(ResponseState.Empty)
     val _postStateFlow: StateFlow<ResponseState> = postStateFlow

    private val createToutStateFlow:MutableStateFlow<ResponseStateCreateTou> = MutableStateFlow(ResponseStateCreateTou.Empty)
     val _createTouStateFlow: StateFlow<ResponseStateCreateTou> = createToutStateFlow

    private val likeStateFlow:MutableStateFlow<ResponseStateCreateTou> = MutableStateFlow(ResponseStateCreateTou.Empty)
    val _likeStateFlow: StateFlow<ResponseStateCreateTou> = likeStateFlow

    private val whoLikeStateFlow:MutableStateFlow<NetworkResult<NetworkWhoLike>> = MutableStateFlow(NetworkResult.Loading())
    val _whoLikeStateFlow: StateFlow<NetworkResult<NetworkWhoLike>> = whoLikeStateFlow

    private val thoughtsDeleteStateFlow:MutableStateFlow<ResponseStateCreateTou> = MutableStateFlow(ResponseStateCreateTou.Empty)
    val _thoughtsDeleteStateFlow: StateFlow<ResponseStateCreateTou> = thoughtsDeleteStateFlow
    fun getThoughts(pg: Int,
                    dir: Int,
                    mythoughts: Boolean)=viewModelScope.launch {
        postStateFlow.value = ResponseState.Loading
        runCatching {
            thoughtsRepo.getThoughts(pg, dir, mythoughts)
              }.onSuccess {
                  postStateFlow.value=ResponseState.Success(it)
        }.onFailure {
            postStateFlow.value = ResponseState.Failure(it)
        }
    }



      fun like(thID: Int, like: Boolean)=viewModelScope.launch {
          likeStateFlow.value = ResponseStateCreateTou.Loading
        runCatching {
            thoughtsRepo.like(thID, like)
        }.onSuccess {
            likeStateFlow.value = ResponseStateCreateTou.Success(it)
        }.onFailure {
            likeStateFlow.value = ResponseStateCreateTou.Failure(it)
        }
    }

    fun thoughtsDelete(thID: Int)=viewModelScope.launch {
        thoughtsDeleteStateFlow.value = ResponseStateCreateTou.Loading
        runCatching {
            thoughtsRepo.thoughtsDelete(thID )
        }.onSuccess {
            thoughtsDeleteStateFlow.value = ResponseStateCreateTou.Success(it)
        }.onFailure {
            thoughtsDeleteStateFlow.value = ResponseStateCreateTou.Failure(it)
        }
    }

    fun whoLiked(thID: Int)=viewModelScope.launch {
        whoLikeStateFlow.value = NetworkResult.Loading()
        runCatching {
            thoughtsRepo.whoLiked(thID )
        }.onSuccess {
            whoLikeStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            whoLikeStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun thoughtsCreate(quotation:String,author:String)=viewModelScope.launch {
        createToutStateFlow.value = ResponseStateCreateTou.Loading
        runCatching {
            thoughtsRepo.thoughtsCreate(quotation, author)
        }.onSuccess {
            createToutStateFlow.value=ResponseStateCreateTou.Success(it)
        }.onFailure {
            createToutStateFlow.value=ResponseStateCreateTou.Failure(it)
        }


    }


}