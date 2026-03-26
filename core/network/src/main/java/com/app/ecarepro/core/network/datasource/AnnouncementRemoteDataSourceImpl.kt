package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.domain.exception.ApiException
import com.app.ecarepro.core.network.AnnouncementRemoteDataSource
import com.app.ecarepro.core.network.model.announcement.NetworkCircular
import com.app.ecarepro.core.network.model.announcement.NetworkCreateCircularResponse
import com.app.ecarepro.core.network.model.announcement.NetworkGetCirculars
import com.app.ecarepro.core.network.model.announcement.NetworkNotice
import com.app.ecarepro.core.network.model.announcement.NetworkSaveCircularRequest
import com.app.ecarepro.core.network.model.announcement.NetworkStaffContact
import com.app.ecarepro.core.network.model.announcement.NetworkStudentParentContact
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AnnouncementService
import javax.inject.Inject

internal class AnnouncementRemoteDataSourceImpl @Inject constructor(
    private val announcementService: AnnouncementService,
) : AnnouncementRemoteDataSource {

    override suspend fun getSchoolNotices(page: Int): List<NetworkNotice> {
        return announcementService.getNotices(page = page).unwrapPayload { noticeList ?: emptyList() }
    }

    override suspend fun getStaffNotices(page: Int): List<NetworkNotice> {
        return announcementService.getNotices(page = page, isStaffNotice = true).unwrapPayload { noticeList ?: emptyList() }
    }

    override suspend fun getClassNotices(classId: Int, page: Int): List<NetworkNotice> {
        return announcementService.getNotices(page = page, classId = classId).unwrapPayload { noticeList ?: emptyList() }
    }

    override suspend fun getNoticeDetail(id: String): NetworkNotice {
        return announcementService.getNoticeDetail(id).unwrapPayload { notice!! }
    }

    override suspend fun getCirculars(page: Int, title: String, date: String, yrId: Int): NetworkGetCirculars {
        val response = announcementService.getCirculars(page = page, title = title, date = date, yrId = yrId)
        if (response.errorCode != 0) {
            throw ApiException(response.errorCode, response.message)
        }
        return response
    }

    override suspend fun getCircularDetail(id: String): NetworkCircular {
        return announcementService.getCircularDetail(id).unwrapPayload { circuler!! }
    }

    override suspend fun getCreateCircularData(): NetworkCreateCircularResponse {
        val response = announcementService.getCreateCircularData()
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }

    override suspend fun getStaffContacts(staffTypeIds: String): List<NetworkStaffContact> {
        return announcementService.getStaffContacts(staffTypeIds).unwrapPayload { contacts ?: emptyList() }
    }

    override suspend fun getStudentParentContacts(classIds: String, scholarType: Int): List<NetworkStudentParentContact> {
        return announcementService.getStudentParentContacts(classIds = classIds, scholarType = scholarType)
            .unwrapPayload { contacts ?: emptyList() }
    }

    override suspend fun saveCircular(request: NetworkSaveCircularRequest) {
        announcementService.saveCircular(request).unwrapPayload { message }
    }
}
