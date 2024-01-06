package com.app.ecarepro.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.StaffRepository
import com.app.ecarepro.model.NoticeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeDetailsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    fun getNoticeDTL(ntID: Int, iD: Int ,onResponse: (NoticeData) -> Unit ){
        viewModelScope.launch {
            onResponse(schoolRepository.getNoticeDTL(ntID, iD))
        }
    }

}