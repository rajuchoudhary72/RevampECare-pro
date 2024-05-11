package com.app.ecarepro.data

import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.asExternalModel
import com.app.ecarepro.data.network.service.SchoolService
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.AppResponse
import com.app.ecarepro.model.ClassPromotionModel
import com.app.ecarepro.model.FeedsDto
import com.app.ecarepro.model.PromotionModel
import com.app.ecarepro.model.RequestClassPromotion
import com.app.ecarepro.model.School
import com.app.ecarepro.model.Slide
import com.app.ecarepro.model.TaskDetails
import com.app.ecarepro.model.TasksDto
import com.app.ecarepro.ui.assign_home.StudentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SchoolRepositoryImpl @Inject constructor(
    private val schoolService: SchoolService,
    private val userDataStore: UserDataStore
) : SchoolRepository {
    override suspend fun fetchWalkThroughData() {
        schoolService.getWalkThroughData().slides.map { it.asExternalModel() }
            .also { userDataStore.saveSlides(it) }
    }

    override fun getOnboardingSlides(): Flow<List<Slide>> {
        return userDataStore.getSlides()
    }

    override fun validateSchoolCode(schoolCode: String): Flow<NetworkSchool?> {
        return flow {
            try {
                val response = schoolService.validateSchoolCode(schoolCode)
                userDataStore.saveSchoolData(response)
                emit(response)
            } catch (e: Exception) {
                emit(null)
            }
        }
    }

    override suspend fun getSchools(): List<School> {
        return schoolService.getSchools().list.map { it.asExternalModel() }
    }

    override fun getSchoolDetails(schoolCode: String): Flow<NetworkSchool> {
        return flow {
            emit(userDataStore.getSchoolData()!!)
        }
    }

    override suspend fun getNotice(pg: Int, classID: Int): NetworkNotice {
        return schoolService.getNotices(pg, classID)

    }

    override suspend fun getCirculars(pg: Int, yrID: Int, title: String): NetworkCircular {
        return schoolService.getCirculars(pg, yrID, title)
    }

    override suspend fun getNoticeDTL(ntID: Int, iD: Int): NetworkNoticDetails {
        return schoolService.getNoticeDTL(ntID, iD)
    }

    override suspend fun getCircularDTL(cirID: Int, iD: Int): NetworkCircularDetails {
        return schoolService.getCircularDTL(cirID, iD)
    }

    override suspend fun getClass(): ClassPromotionModel {
        return schoolService.getClassTeacherOf()
    }

    override suspend fun getClassPromotions(classId: String): PromotionModel {
        return schoolService.getClassPromotion(classId)

    }

    override suspend fun submitClassPromotions(request: RequestClassPromotion): AppResponse {
        return schoolService.saveClassPromotion(request)
    }

    override fun getFeeds(pg: Int): Flow<Result<FeedsDto>> {
        return flow {
            try {
                val response = schoolService.getSchoolFeeds(pg = pg)
                if (pg == 1) {
                    userDataStore.saveFeeds(response)
                }
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    override fun getTaskList(filter: Int): Flow<Result<TasksDto>> {
        return flow {
            try {
                val response = schoolService.getTaskList(filter)
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getTaskDetails(taskId: String): Flow<Result<TaskDetails>> {
        return flow {
            try {
                val response = schoolService.getTaskDetails(taskId)
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun getStudentListToAssignHouse(
        id: String,
        orderBy: String
    ): StudentList {
        return schoolService.getStudentListToAssignHouse(id,orderBy)
    }

    override suspend fun assignHouse(request: List<AssignHouseRequest>): CommonResponse {
        return schoolService.assignHouse(request)
    }
}