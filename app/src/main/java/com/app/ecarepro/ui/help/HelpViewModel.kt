package com.app.ecarepro.ui.help

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.ecarepro.data.database.databases.SchoolDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.app.ecarepro.data.repository.SchoolRepository
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.viewModelScope

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val  schoolDatabase: SchoolDatabase,
    private val schoolRepository: SchoolRepository,

    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val schoolCode = savedStateHandle.get<String>("schoolCode")
        ?: throw IllegalArgumentException("School code required")
    val school = schoolDatabase.getSchoolFlow(schoolCode).asLiveData()
    init {
        getSchoolDetails()
    }
    private fun getSchoolDetails() {
        viewModelScope.launch {
            schoolRepository
                .validateSchoolCode(schoolCode.toUpperCase())
                .collectLatest {
                }
        }
    }
}