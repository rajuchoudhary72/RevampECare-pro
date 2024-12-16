package com.app.ecarepro.ui.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {

    private val questionnaireStateFlow: MutableStateFlow<NetworkResult<NetworkQuestionnaire>> = MutableStateFlow(
        NetworkResult.Loading())
    val _questionnaireStateFlow: StateFlow<NetworkResult<NetworkQuestionnaire>> = questionnaireStateFlow

    private val deleteAnswerMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val deleteAnswerStateFlow: StateFlow<NetworkResult<CommonResponse>> = deleteAnswerMutableStateFlow


    fun getQuestionnaireList( pg: Int, myque: Boolean )=viewModelScope.launch {
        runCatching {
            questionnaireStateFlow.value = NetworkResult.Loading( )
            userRepository.getQuestionnaireList( pg, myque)
        }.onSuccess {
            questionnaireStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            questionnaireStateFlow .value = NetworkResult.Error(it.message)
        }
    }

    fun questionnaireLike(qID: Int, like: Boolean)=viewModelScope.launch {
        runCatching {
             userRepository.questionnaireLike( qID, like)
        }.onSuccess {
         }.onFailure {
         }
    }

    fun deleteAnswer(ansID: Int )=viewModelScope.launch {
        runCatching {
            deleteAnswerMutableStateFlow.value= NetworkResult.Loading( )

            userRepository.deleteQID(ansID)
        }.onSuccess {
            deleteAnswerMutableStateFlow.value= NetworkResult.Success(it)
        }.onFailure {
            deleteAnswerMutableStateFlow.value= NetworkResult.Error(it.message)
        }
    }



}