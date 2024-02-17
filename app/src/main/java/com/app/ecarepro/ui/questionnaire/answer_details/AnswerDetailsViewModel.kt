package com.app.ecarepro.ui.questionnaire.answer_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnswerDetailsViewModel @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {


    private val answerDetailMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAnswerDetails>> = MutableStateFlow(
        NetworkResult.Loading())
    val answerDetailStateFlow: StateFlow<NetworkResult<NetworkAnswerDetails>> = answerDetailMutableStateFlow

    private val postAnswerMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val postAnswerStateFlow: StateFlow<NetworkResult<CommonResponse>> = postAnswerMutableStateFlow


    fun getAnswerList(qID: Int )=viewModelScope.launch {
        runCatching {
            answerDetailMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.answerList( qID)
        }.onSuccess {
            answerDetailMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            answerDetailMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

    fun postAnswer (qid:String,answer:String)=viewModelScope.launch {
        runCatching {
            postAnswerMutableStateFlow.value= NetworkResult.Loading( )

            userRepository.postAnswer( qid, answer)
         }.onSuccess {
            postAnswerMutableStateFlow.value= NetworkResult.Success(it)
         }.onFailure {
            postAnswerMutableStateFlow.value= NetworkResult.Error(it.message)
         }
    }



}