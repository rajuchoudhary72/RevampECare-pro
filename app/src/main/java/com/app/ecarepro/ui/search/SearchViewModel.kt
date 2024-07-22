package com.app.ecarepro.ui.search

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.SearchOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    val searchOptions =
        MutableStateFlow<List<SearchOption>>(emptyList())
    val searchQuery = MutableStateFlow("")

    fun clearSearchQuery() {
        searchQuery.update { "" }
    }
}