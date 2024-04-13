package com.app.ecarepro.ui.assignment.submit_assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubmitAssignmentViewModel @Inject constructor(
    private var userRepository: UserRepository
) : ViewModel() {


    private val submitAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val submitAssignmentStateFlow: StateFlow<NetworkResult<CommonResponse>> = submitAssignmentMutableStateFlow




      suspend fun submitAssignment(
        id: String,
        asgID: Int,
        data: String,
        fileName: String,
        attachment: String,
        fileURL: String,
        fileExt: String
    )=viewModelScope.launch {
        runCatching {
            submitAssignmentMutableStateFlow.value= NetworkResult.Loading( )

            userRepository.submitAssignment(id, asgID, data, fileName, attachment, fileURL, fileExt)
        }.onSuccess {
            submitAssignmentMutableStateFlow.value= NetworkResult.Success(it)
        }.onFailure {
            submitAssignmentMutableStateFlow.value= NetworkResult.Error(it.message)
        }
    }

}