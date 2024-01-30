package com.app.ecarepro.ui.institutioncode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstitutionCodeViewModel @Inject constructor( private val schoolRepository: SchoolRepository ) : ViewModel() {

    fun validateSchoolCode(schoolCode: String, onResponse: (NetworkSchool?) -> Unit) {
        viewModelScope.launch {
            schoolRepository.validateSchoolCode(schoolCode.toUpperCase()).collectLatest {
                onResponse(it)
            }
        }
    }

}