package com.app.ecarepro.ui.syllabus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.AcademicRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ClassSyllabusViewModel @Inject constructor(
    private   val academicRepo: AcademicRepo
) : ViewModel() {

    private val classSyllabusStateFlow: MutableStateFlow<NetworkResult<NetworkClassSyllabus>> = MutableStateFlow(
        NetworkResult.Loading())
    val _classSyllabusStateFlow: StateFlow<NetworkResult<NetworkClassSyllabus>> = classSyllabusStateFlow

    fun getClassSyllabus( )=viewModelScope.launch {
        runCatching {
            classSyllabusStateFlow.value = NetworkResult.Loading()
            academicRepo.getClassSyllabus( )
        }.onSuccess {
            classSyllabusStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            classSyllabusStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}