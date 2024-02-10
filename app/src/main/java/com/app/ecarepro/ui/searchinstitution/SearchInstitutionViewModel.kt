package com.app.ecarepro.ui.searchinstitution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.School
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchInstitutionViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val _schools = MutableStateFlow<List<School>>(emptyList())

    val searchQuery = MutableStateFlow("")

    val schools =
        combine(
            flow = searchQuery,
            flow2 = _schools
        ) { query, schools ->
            schools.filter {
                it.schoolCode?.contains(query, true) == true || it.address?.contains(
                    query,
                    true
                ) == true
            }
        }


    fun getSchools(onResponse: () -> Unit) {
        viewModelScope.launch {
            _schools.update { schoolRepository.getSchools() }
            onResponse()
        }
    }
}