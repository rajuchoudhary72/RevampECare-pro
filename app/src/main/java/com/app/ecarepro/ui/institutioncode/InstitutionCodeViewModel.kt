package com.app.ecarepro.ui.institutioncode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.model.asNetworkSchool
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
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
    private val schoolDatabase: SchoolDatabase,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    val isMainApp = BuildConfig.FLAVOR == "Franciscan e-Care"
    val isMYSFHS = BuildConfig.FLAVOR == "MYSFHS"
    val isMYSFPSPlay = BuildConfig.FLAVOR == "MYSFPS Play"

    val schools = userDataStore.getCurrentSchoolCodeAsFlow().flatMapLatest { schoolCode ->
        schoolDatabase.getSchoolsFlow().map {
            it.map {
                if (it.schoolCode == schoolCode) {
                    it.asNetworkSchool()?.copy(isSelected = true)
                } else {
                    it.asNetworkSchool()
                }

            }
        }
    }.asLiveData()

    fun validateSchoolCode(schoolCode: String, onResponse: (NetworkSchool?) -> Unit) {
        viewModelScope.launch {
            schoolRepository.validateSchoolCode(schoolCode.toUpperCase()).collectLatest {
                sentSchoolCodeValidateEvent(schoolCode)
                onResponse(it)
            }
        }
    }

    suspend fun isUserAuthenticated() = userDataStore.isUserAuthenticated()

    fun sendScreenEvent() {
        analyticsManager.trackScreen(AnalyticsConstants.Screens.SCHOOL_CODE)
    }

    private fun sentSchoolCodeValidateEvent(schoolCode: String) {
        analyticsManager.trackEvent(
            AnalyticsConstants.Events.VALIDATE_SCHOOL_CODE,
            mapOf(
                AnalyticsConstants.Attributes.SCHOOL_CODE to schoolCode
            )
        )
    }

}