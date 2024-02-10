package com.app.ecarepro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _openNavigationDrawer = MutableLiveData(false)
    val openNavigationDrawer = _openNavigationDrawer

    val user = userDataStore.getUserAsFlow()

    fun openDrawer(open: Boolean) {
        _openNavigationDrawer.postValue(open)
    }

    fun logout(onDataClear: () -> Unit) {
        viewModelScope.launch {
            userDataStore.clear()
            onDataClear()
        }
    }
}