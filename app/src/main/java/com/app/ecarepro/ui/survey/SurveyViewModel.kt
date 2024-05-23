package com.app.ecarepro.ui.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val surveyListMutableStateFlow: MutableStateFlow<NetworkResult<SurveyListResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val surveyListStateFlow: StateFlow<NetworkResult<SurveyListResponse>> =
        surveyListMutableStateFlow

    fun surveyList(pg: Int, isReport: Boolean) = viewModelScope.launch {
        runCatching {
            surveyListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.surveyList(pg, isReport)
        }.onSuccess {
            surveyListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            surveyListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    private val surveyQuestionsResponseStateFlow: MutableStateFlow<NetworkResult<SurveyQuestionsResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val surveyQuestionsStateFlow: StateFlow<NetworkResult<SurveyQuestionsResponse>> =
        surveyQuestionsResponseStateFlow

    fun surveyQuestions(
        id: String
    ) = viewModelScope.launch {
        runCatching {
            surveyQuestionsResponseStateFlow.value = NetworkResult.Loading()
            userRepository.surveyQuestions(id)
        }.onSuccess {
            surveyQuestionsResponseStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            surveyQuestionsResponseStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    private val commonResponseStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(NetworkResult.Loading()
    )
    val commonStateFlow: StateFlow<NetworkResult<CommonResponse>> =
        commonResponseStateFlow

    fun surveyQuestionsSubmit(
        model: SurveyQuestionsSubmitRequest
    ) = viewModelScope.launch {
        runCatching {
            commonResponseStateFlow.value = NetworkResult.Loading()
            userRepository.submitSurveyQuestions(model)
        }.onSuccess {
            commonResponseStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            commonResponseStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}