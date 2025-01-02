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
import androidx.lifecycle.Lifecycle
import com.app.ecarepro.data.network.model.UserData
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
@HiltViewModel
class HelpViewModel @Inject constructor(
    private val  schoolDatabase: SchoolDatabase,
    private val schoolRepository: SchoolRepository,
    private val analyticsManager: AnalyticsManager,

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
    fun sendScreenEvent() {
        analyticsManager.trackScreen(AnalyticsConstants.Screens.HELP_SCREEN)
    }
    fun sentAnalyticEvent(
        event: String,
        attributes: Map<String, String>
    ) {
        analyticsManager.trackEvent(
            event,
            attributes
        )
    }
}