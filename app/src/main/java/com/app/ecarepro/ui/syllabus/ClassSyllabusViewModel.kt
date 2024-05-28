package com.app.ecarepro.ui.syllabus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ClassSyllabusViewModel @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {

    private val classSyllabusMutableStateFlow: MutableStateFlow<NetworkResult<NetworkClassSyllabus>> = MutableStateFlow(
        NetworkResult.Loading())
    val classSyllabusStateFlow: StateFlow<NetworkResult<NetworkClassSyllabus>> = classSyllabusMutableStateFlow

    fun getClassSyllabus( )=viewModelScope.launch {
        runCatching {
            classSyllabusMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getClassSyllabus( )
        }.onSuccess {
            classSyllabusMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            classSyllabusMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}