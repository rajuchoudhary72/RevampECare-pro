package com.app.ecarepro.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.StaffRepository
import com.app.ecarepro.model.MyClasse
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.NoticeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val staffRepository: StaffRepository
) : ViewModel() {

        fun getNotice(pg: Int,classID: Int,onResponse: (List<Notice>) -> Unit ){
            viewModelScope.launch {
                onResponse(schoolRepository.getNotice(pg, classID))
            }
        }



      fun getMyClass(subID: Int, iD: Int, onResponse: (List<MyClasse>) -> Unit) {

        viewModelScope.launch {
            onResponse(staffRepository.getMyClass(subID, iD))
        }

    }


}