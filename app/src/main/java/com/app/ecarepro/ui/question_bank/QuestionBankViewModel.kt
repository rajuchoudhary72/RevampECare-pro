package com.app.ecarepro.ui.question_bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkQuestionBank
import com.app.ecarepro.data.network.model.NetworkQuestionPaper
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class QuestionBankViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val  userRepository: UserRepository
) : ViewModel() {




    private val questionBankMutableStateFlow: MutableStateFlow<NetworkResult<NetworkQuestionBank>> = MutableStateFlow(
        NetworkResult.Loading())
    val questionBankStateFlow: StateFlow<NetworkResult<NetworkQuestionBank>> = questionBankMutableStateFlow


    private val questionBankMutableDeleteStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val questionBankDeleteStateFlow: StateFlow<NetworkResult<CommonResponse>> = questionBankMutableDeleteStateFlow





    fun getMyQuestionBank( )=viewModelScope.launch {
        runCatching {
            questionBankMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getMyQuestionBank( )
        }.onSuccess {
            questionBankMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            questionBankMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getDeleteQuestion(
        id: String
    )=viewModelScope.launch {
        runCatching {
            questionBankMutableDeleteStateFlow.value =NetworkResult.Loading( )
            userRepository.getDeleteQuestion(id )
        }.onSuccess {
            questionBankMutableDeleteStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            questionBankMutableDeleteStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}