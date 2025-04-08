package com.app.ecarepro.ui.timetableviewer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ClassTimeTableViewModel @Inject constructor() : ViewModel() {

    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    fun showSearchBar() {
        showSearchView.update { true }
    }

    fun clearSearchQuery() {
        if (searchQuery.value.isEmpty()) {
            showSearchView.update { false }
        } else
            searchQuery.update {
                ""
            }
    }
}

