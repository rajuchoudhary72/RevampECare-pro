package com.app.ecarepro.ui.question_bank.add_question_bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkQuestionBank
import com.app.ecarepro.data.network.model.NetworkQuestionPaper
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.postQuestionBank.NetworkPostQuestionBank
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankChapters
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankCreate
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankSubject
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddQuestionBankViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val  userRepository: UserRepository
) : ViewModel() {




    private val questionBankCreateMutableStateFlow: MutableStateFlow<NetworkResult<NetworkQuestionBankCreate>> = MutableStateFlow(
        NetworkResult.Loading())
    val questionBankCreateStateFlow: StateFlow<NetworkResult<NetworkQuestionBankCreate>> = questionBankCreateMutableStateFlow



    private val questionBankSubjectMutableStateFlow: MutableStateFlow<NetworkResult<NetworkQuestionBankSubject>> = MutableStateFlow(
        NetworkResult.Loading())
    val questionBankSubjectStateFlow: StateFlow<NetworkResult<NetworkQuestionBankSubject>> = questionBankSubjectMutableStateFlow


    private val questionBankChaptersMutableStateFlow: MutableStateFlow<NetworkResult<NetworkQuestionBankChapters>> = MutableStateFlow(
        NetworkResult.Loading())
    val questionBankChaptersStateFlow: StateFlow<NetworkResult<NetworkQuestionBankChapters>> = questionBankChaptersMutableStateFlow


    private val postQuestionBankMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val postQuestionBankChaptersStateFlow: StateFlow<NetworkResult<CommonResponse>> = postQuestionBankMutableStateFlow





    fun getMyQuestionBankCreate( )=viewModelScope.launch {
        runCatching {
            questionBankCreateMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getQuestionBankCreate( )
        }.onSuccess {
            questionBankCreateMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            questionBankCreateMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getQuestionBankSubject(
        classID: Int
    )=viewModelScope.launch {
        runCatching {
            questionBankSubjectMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getQuestionBankSubject(classID )
        }.onSuccess {
            questionBankSubjectMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            questionBankSubjectMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getQuestionBankChapters(
        classID: Int,
        subID: Int
    )=viewModelScope.launch {
        runCatching {
            questionBankChaptersMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getQuestionBankChapters(classID,subID )
        }.onSuccess {
            questionBankChaptersMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            questionBankChaptersMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun submitPostQuestion(
        model: NetworkPostQuestionBank
    )=viewModelScope.launch {
        runCatching {
            postQuestionBankMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.submitPostQuestion(model)
        }.onSuccess {
            postQuestionBankMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            postQuestionBankMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}