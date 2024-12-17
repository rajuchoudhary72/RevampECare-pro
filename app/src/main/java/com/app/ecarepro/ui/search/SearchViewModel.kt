package com.app.ecarepro.ui.search

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.SearchOption
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    val searchOptions =
        MutableStateFlow<List<SearchOption>>(emptyList())
    val searchQuery = MutableStateFlow("")

    fun clearSearchQuery() {
        searchQuery.update { "" }
    }

    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.GLOBAL_SEARCH)
    }
}