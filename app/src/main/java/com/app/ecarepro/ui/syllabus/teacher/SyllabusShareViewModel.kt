package com.app.ecarepro.ui.syllabus.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecarepro.model.Syllabuse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyllabusShareViewModel @Inject constructor() : ViewModel() {

    private val syllabusMutableLiveData = MutableLiveData<List<Syllabuse>>()



    fun getSyllabusMutableLiveData(): LiveData<List<Syllabuse>> {
        return syllabusMutableLiveData
    }

    fun setSyllabusMutableLiveData(data: List<Syllabuse>) {
        this.syllabusMutableLiveData.value = data
    }

}