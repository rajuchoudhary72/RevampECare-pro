package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.announcement.NetworkCircular
import com.app.ecarepro.core.network.model.announcement.NetworkCreateCircularResponse
import com.app.ecarepro.core.network.model.announcement.NetworkGetCirculars
import com.app.ecarepro.core.network.model.announcement.NetworkNotice
import com.app.ecarepro.core.network.model.announcement.NetworkSaveCircularRequest
import com.app.ecarepro.core.network.model.announcement.NetworkStaffContact
import com.app.ecarepro.core.network.model.announcement.NetworkStudentParentContact

interface AnnouncementRemoteDataSource {
    suspend fun getSchoolNotices(page: Int): List<NetworkNotice>
    suspend fun getStaffNotices(page: Int): List<NetworkNotice>
    suspend fun getClassNotices(classId: Int, page: Int): List<NetworkNotice>
    suspend fun getNoticeDetail(id: String): NetworkNotice
    suspend fun getCirculars(page: Int, title: String, date: String, yrId: Int): NetworkGetCirculars
    suspend fun getCircularDetail(id: String): NetworkCircular
    suspend fun getCreateCircularData(): NetworkCreateCircularResponse
    suspend fun getStaffContacts(staffTypeIds: String): List<NetworkStaffContact>
    suspend fun getStudentParentContacts(classIds: String, scholarType: Int): List<NetworkStudentParentContact>
    suspend fun saveCircular(request: NetworkSaveCircularRequest)
}
