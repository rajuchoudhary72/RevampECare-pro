package com.app.ecarepro.ui.searchinstitution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.School
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchInstitutionViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    val schools = mutableListOf<School>()

    fun getSchools(onResponse: (List<School>) -> Unit) {
        viewModelScope.launch {
            onResponse(schoolRepository.getSchools())
        }
    }
}