package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.domain.model.CreateCircularData
import com.app.ecarepro.core.domain.model.Notice
import com.app.ecarepro.core.domain.model.SaveCircularRequest
import com.app.ecarepro.core.domain.model.StaffContact
import com.app.ecarepro.core.domain.model.StudentParentContact
import com.app.ecarepro.core.domain.repository.AnnouncementRepository
import com.app.ecarepro.core.network.AnnouncementRemoteDataSource
import com.app.ecarepro.core.network.model.announcement.toDomainModel
import com.app.ecarepro.core.network.model.announcement.toNetworkModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AnnouncementRepositoryImpl @Inject constructor(
    private val announcementRemoteDataSource: AnnouncementRemoteDataSource,
) : AnnouncementRepository {

    override fun getSchoolNotices(page: Int): Flow<Result<List<Notice>>> {
        return asResultFlow {
            announcementRemoteDataSource.getSchoolNotices(page).map { it.toDomainModel() }
        }
    }

    override fun getStaffNotices(page: Int): Flow<Result<List<Notice>>> {
        return asResultFlow {
            announcementRemoteDataSource.getStaffNotices(page).map { it.toDomainModel() }
        }
    }

    override fun getClassNotices(classId: Int, page: Int): Flow<Result<List<Notice>>> {
        return asResultFlow {
            announcementRemoteDataSource.getClassNotices(classId, page).map { it.toDomainModel() }
        }
    }

    override fun getNoticeDetail(id: String): Flow<Result<Notice>> {
        return asResultFlow {
            announcementRemoteDataSource.getNoticeDetail(id).toDomainModel()
        }
    }

    override fun getCirculars(page: Int, title: String, date: String, yrId: Int): Flow<Result<Pair<List<Circular>, List<AcademicYear>>>> {
        return asResultFlow {
            val response = announcementRemoteDataSource.getCirculars(page, title, date, yrId)
            val circulars = response.circularList?.map { it.toDomainModel() } ?: emptyList()
            val years = response.academicYears?.map { it.toDomainModel() } ?: emptyList()
            Pair(circulars, years)
        }
    }

    override fun getCircularDetail(id: String): Flow<Result<Circular>> {
        return asResultFlow {
            announcementRemoteDataSource.getCircularDetail(id).toDomainModel()
        }
    }

    override fun getCreateCircularData(): Flow<Result<CreateCircularData>> = asResultFlow {
        announcementRemoteDataSource.getCreateCircularData().toDomainModel()
    }

    override fun getStaffContacts(staffTypeIds: List<Int>): Flow<Result<List<StaffContact>>> = asResultFlow {
        announcementRemoteDataSource.getStaffContacts(staffTypeIds.joinToString(",")).map { it.toDomainModel() }
    }

    override fun getStudentParentContacts(classIds: List<Int>, scholarType: Int): Flow<Result<List<StudentParentContact>>> = asResultFlow {
        announcementRemoteDataSource.getStudentParentContacts(classIds.joinToString(","), scholarType).map { it.toDomainModel() }
    }

    override fun saveCircular(request: SaveCircularRequest): Flow<Result<Unit>> = asResultFlow {
        announcementRemoteDataSource.saveCircular(request.toNetworkModel())
    }
}
