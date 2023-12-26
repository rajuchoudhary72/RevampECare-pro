package com.app.ecarepro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SystemViewModel @Inject constructor() : ViewModel() {

    private val _openNavigationDrawer = MutableLiveData(false)
    val openNavigationDrawer = _openNavigationDrawer

    fun openDrawer(open:Boolean){
        _openNavigationDrawer.postValue(open)
    }
}