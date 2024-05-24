package com.app.ecarepro.ui.question_paper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
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
class QuestionPaperViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val  userRepository: UserRepository
) : ViewModel() {


    private val classMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> = MutableStateFlow(
        NetworkResult.Loading())
    val classStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = classMutableStateFlow


    private val questionPaperMutableStateFlow: MutableStateFlow<NetworkResult<NetworkQuestionPaper>> = MutableStateFlow(
        NetworkResult.Loading())
    val questionPaperStateFlow: StateFlow<NetworkResult<NetworkQuestionPaper>> = questionPaperMutableStateFlow



    fun getMyClass(subID: Int, iD: Int  )=viewModelScope.launch {
          runCatching {
              classMutableStateFlow.value =NetworkResult.Loading( )
              userRepository.staffMyClass(subID, iD)
          }.onSuccess {
              classMutableStateFlow.value =NetworkResult.Success(it)
          }.onFailure {
              classMutableStateFlow.value = NetworkResult.Error(it.message)
          }
      }

    fun getQuestionPaper(
        classID: Int,
        yrID: Int
    )=viewModelScope.launch {
        runCatching {
            questionPaperMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getQuestionPaper(classID, yrID)
        }.onSuccess {
            questionPaperMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            questionPaperMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}