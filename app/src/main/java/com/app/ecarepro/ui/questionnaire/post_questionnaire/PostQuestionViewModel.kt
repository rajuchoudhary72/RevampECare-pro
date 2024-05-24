package com.app.ecarepro.ui.questionnaire.post_questionnaire

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
class PostQuestionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val addQuestionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val addQuestionStateFlow: StateFlow<NetworkResult<CommonResponse>> = addQuestionMutableStateFlow


    fun addQuestion(question: String, attachment: String, fileURL: String, fileExt: String) =
        viewModelScope.launch {
            runCatching {
                addQuestionMutableStateFlow.value = NetworkResult.Loading()

                userRepository.addQuestion(question, attachment, fileURL, fileExt)
            }.onSuccess {
                addQuestionMutableStateFlow.value = NetworkResult.Success(it)
            }.onFailure {
                addQuestionMutableStateFlow.value = NetworkResult.Error(it.message)
            }
        }


}