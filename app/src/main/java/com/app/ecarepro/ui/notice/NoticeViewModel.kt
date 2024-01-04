package com.app.ecarepro.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Notice
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

        fun getNotice(pg: Int,classID: Int,onResponse: (List<Notice>) -> Unit ){
            viewModelScope.launch {
                onResponse(schoolRepository.getNotice(pg, classID))
            }
        }
}