package com.app.ecarepro.ui.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

}