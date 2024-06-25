package com.app.ecarepro.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel


class SharedViewModel : ViewModel() {

    // Mutable LiveData which observed by LiveData
    // and updated to EditTexts when it is changed.
    private val mutableLiveData: MutableLiveData<CharSequence> = MutableLiveData()

    // function to set the changed
    // data from the EditTexts
    fun setData(input: CharSequence) {
        mutableLiveData.value = input
    }

     fun getData(): MutableLiveData<CharSequence> = mutableLiveData
}
