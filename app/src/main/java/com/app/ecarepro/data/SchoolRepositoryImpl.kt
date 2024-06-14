package com.app.ecarepro.data

import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.UpdateTaskDto
import com.app.ecarepro.data.network.model.asExternalModel
import com.app.ecarepro.data.network.service.SchoolService
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.AddTaskDto
import com.app.ecarepro.model.AppResponse
import com.app.ecarepro.model.ClassPromotionModel
import com.app.ecarepro.model.FeedsDto
import com.app.ecarepro.model.PromotionModel
import com.app.ecarepro.model.RequestClassPromotion
import com.app.ecarepro.model.School
import com.app.ecarepro.model.Slide
import com.app.ecarepro.model.TaskDetails
import com.app.ecarepro.model.TasksDto
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.ui.assign_home.StudentList
import com.app.ecarepro.ui.medicalcard.MedicalCardResponse
import com.app.ecarepro.model.Title
import com.app.ecarepro.model.UpdateTaskAttachmentDto
import com.app.ecarepro.model.Watcher
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
                e.printStackTrace()
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

    override suspend fun getNotice(pg: Int, classID: Int,isClassNotice: Boolean): NetworkNotice {
        return schoolService.getNotices(pg, classID,isClassNotice)

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

    override fun getTasks(): Flow<Result<List<Title>>> {
        return flow {
            try {
                val response = schoolService.getTasks()
                if (response.errorCode == 0) {
                    emit(Result.success(response.titles ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun addTask(request: AddTaskDto): Flow<Result<String>> {
        return flow {
            try {
                val response = schoolService.saveTask(request)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Task Saved"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun updateTaskImage(request: UpdateTaskAttachmentDto): Flow<Result<String>> {
        return flow {
            try {
                val response = schoolService.updateTaskAttachment(request)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Task Saved"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun updateTask(request: UpdateTaskDto): Flow<Result<String>> {
        return flow {
            try {
                val response = schoolService.updateTask(request)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message?:""))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getWatchers(): Flow<Result<List<Watcher>>> {
        return flow {
            try {
                val response = schoolService.getWatcher()
                if (response.errorCode == 0) {
                    emit(Result.success(response.watchers?: emptyList()))
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

    override suspend fun getMedicalCard(): MedicalCardResponse {
        return schoolService.medicalCard()
    }

    override suspend fun updateMedicalCard(request: UpdateMedicalCardRequest): CommonResponse {
        return schoolService.updateMedicalCard(request)
    }


    override fun updateTaskStatus(id: String?, statusId: Int): Flow<Result<String>> {
        return flow {
            try {
                val response = schoolService.updateTaskStatus(id!!, statusId)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message?:""))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }
}