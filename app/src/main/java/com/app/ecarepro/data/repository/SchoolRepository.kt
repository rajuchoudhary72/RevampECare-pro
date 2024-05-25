package com.app.ecarepro.data.repository

import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.UpdateTaskDto
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
import com.app.ecarepro.model.Title
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.model.UpdateTaskAttachmentDto
import com.app.ecarepro.model.Watcher
import com.app.ecarepro.ui.assign_home.StudentList
import com.app.ecarepro.ui.medicalcard.MedicalCardResponse
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    suspend fun fetchWalkThroughData()
    fun getOnboardingSlides(): Flow<List<Slide>>
    fun validateSchoolCode(schoolCode: String): Flow<NetworkSchool?>
    suspend fun getSchools(): List<School>
    fun getSchoolDetails(schoolCode: String): Flow<NetworkSchool>
    suspend fun getNotice(pg: Int, classID: Int): NetworkNotice
    suspend fun getCirculars(pg: Int, yrID: Int, title: String): NetworkCircular
    suspend fun getNoticeDTL(ntID: Int, iD: Int): NetworkNoticDetails
    suspend fun getCircularDTL(cirID: Int, iD: Int): NetworkCircularDetails
    suspend fun getClass(): ClassPromotionModel
    suspend fun getClassPromotions(classId: String): PromotionModel
    suspend fun submitClassPromotions(request: RequestClassPromotion): AppResponse
    fun getFeeds(pg: Int): Flow<Result<FeedsDto>>
    fun getTaskList(filter: Int): Flow<Result<TasksDto>>
    fun getTaskDetails(taskId: String): Flow<Result<TaskDetails>>
    fun getTasks(): Flow<Result<List<Title>>>
    fun addTask(request: AddTaskDto): Flow<Result<String>>
    fun updateTaskImage(request: UpdateTaskAttachmentDto): Flow<Result<String>>
    fun updateTask(request: UpdateTaskDto): Flow<Result<String>>
    fun getWatchers(): Flow<Result<List<Watcher>>>
    suspend fun getStudentListToAssignHouse(id: String, orderBy: String): StudentList
    suspend fun assignHouse(request: List<AssignHouseRequest>): CommonResponse
    suspend fun getMedicalCard(): MedicalCardResponse
    suspend fun updateMedicalCard(request: UpdateMedicalCardRequest): CommonResponse
    fun updateTaskStatus(id: String?, statusId: Int): Flow<Result<String>>
}