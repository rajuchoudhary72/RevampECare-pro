package com.app.ecarepro.ui.institutioncode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.model.asNetworkSchool
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstitutionCodeViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userDataStore: UserDataStore,
    private val schoolDatabase: SchoolDatabase
) : ViewModel() {

    val schools = userDataStore.getCurrentSchoolCodeAsFlow().flatMapLatest { schoolCode ->
        schoolDatabase.getSchoolsFlow().map {
            it.map {
                if (it.schoolCode == schoolCode) {
                    it.asNetworkSchool().copy(isSelected = true)
                } else {
                    it.asNetworkSchool()
                }

            }
        }
    }.asLiveData()

    fun validateSchoolCode(schoolCode: String, onResponse: (NetworkSchool?) -> Unit) {
        viewModelScope.launch {
            schoolRepository.validateSchoolCode(schoolCode.toUpperCase()).collectLatest {
                onResponse(it)
            }
        }
    }

}