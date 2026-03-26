package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.domain.model.CreateCircularData
import com.app.ecarepro.core.domain.model.Notice
import com.app.ecarepro.core.domain.model.SaveCircularRequest
import com.app.ecarepro.core.domain.model.StaffContact
import com.app.ecarepro.core.domain.model.StudentParentContact
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun getSchoolNotices(page: Int = 1): Flow<Result<List<Notice>>>
    fun getStaffNotices(page: Int = 1): Flow<Result<List<Notice>>>
    fun getClassNotices(classId: Int, page: Int = 1): Flow<Result<List<Notice>>>
    fun getNoticeDetail(id: String): Flow<Result<Notice>>
    fun getCirculars(page: Int = 1, title: String = "", date: String = "", yrId: Int = 0): Flow<Result<Pair<List<Circular>, List<AcademicYear>>>>
    fun getCircularDetail(id: String): Flow<Result<Circular>>
    fun getCreateCircularData(): Flow<Result<CreateCircularData>>
    fun getStaffContacts(staffTypeIds: List<Int>): Flow<Result<List<StaffContact>>>
    fun getStudentParentContacts(classIds: List<Int>, scholarType: Int): Flow<Result<List<StudentParentContact>>>
    fun saveCircular(request: SaveCircularRequest): Flow<Result<Unit>>
}
