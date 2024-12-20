package com.app.ecarepro.ui.students_list.students_new_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudentsListShareViewModel @Inject constructor() : ViewModel() {

    private val studentMutableLiveData = MutableLiveData<List<Student>>()


    fun getStudentMutableLiveData(): LiveData<List<Student>>{
        return studentMutableLiveData
    }

    fun setStudentMutableLiveData(networkStudentProfile: List<Student>) {
        this.studentMutableLiveData.value = networkStudentProfile
    }
}

