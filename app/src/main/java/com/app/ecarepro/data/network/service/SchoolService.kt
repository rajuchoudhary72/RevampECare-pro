package com.app.ecarepro.data.network.service

import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkSchoolsDto
import com.app.ecarepro.data.network.model.NetworkWalkThrough
import com.app.ecarepro.data.network.model.UpdateTaskDto
import com.app.ecarepro.model.AddTaskDto
import com.app.ecarepro.model.AppResponse
import com.app.ecarepro.model.ClassPromotionModel
import com.app.ecarepro.model.FeedsDto
import com.app.ecarepro.model.PromotionModel
import com.app.ecarepro.model.RequestClassPromotion
import com.app.ecarepro.model.TaskDetails
import com.app.ecarepro.model.TaskDto
import com.app.ecarepro.model.TasksDto
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.model.UpdateTaskAttachmentDto
import com.app.ecarepro.model.WatchersDto
import com.app.ecarepro.ui.assign_home.StudentList
import com.app.ecarepro.ui.medicalcard.MedicalCardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SchoolService {
    @GET("School/WalkThrough")
    suspend fun getWalkThroughData(): NetworkWalkThrough

    @GET("School/DTL")
    suspend fun validateSchoolCode(@Query("SchCode") schoolCode: String): NetworkSchool

    @GET("School/List")
    suspend fun getSchools(): NetworkSchoolsDto

    @GET("School/Notices")
    suspend fun getNotices(
        @Query("pg") pg: Int,
        @Query("ClassID") classID: Int,
        @Query("isClassNotice") isClassNotice: Boolean,
    ): NetworkNotice

    @GET("School/Circulars")
    suspend fun getCirculars(
        @Query("pg") pg: Int,
        @Query("YrID") yrID: Int,
        @Query("title") title: String
    ): NetworkCircular

    @GET("School/NoticeDTL")
    suspend fun getNoticeDTL(
        @Query("NtID") ntID: Int,
        @Query("ID") iD: Int,
    ): NetworkNoticDetails

    @GET("School/CircularDTL")
    suspend fun getCircularDTL(
        @Query("CirID") cirID: Int,
        @Query("ID") iD: Int,
    ): NetworkCircularDetails

    @GET("Staff/ClassTeacherOf")
    suspend fun getClassTeacherOf(): ClassPromotionModel

    @GET("Admin/ClassPromotion")
    suspend fun getClassPromotion(@Query("ClassId") classId: String): PromotionModel


    @POST("Admin/SaveClassPromotion")
    suspend fun saveClassPromotion(@Body body: RequestClassPromotion): AppResponse

    @GET("School/Feed")
    suspend fun getSchoolFeeds(
        @Query("IsDashboard") isDashboard: Boolean = true,
        @Query("pg") pg: Int,
    ): FeedsDto

    @GET("TaskManager/Overview")
    suspend fun getTaskList(
        @Query("filter") filter: Int,
        @Query("pg") pg: Int = 1,
    ): TasksDto

    @GET("TaskManager/Task")
    suspend fun getTaskDetails(
        @Query("ID") taskId: String
    ): TaskDetails

    @GET("TaskManager/TaskList")
    suspend fun getTasks(): TaskDto

    @POST("TaskManager/SaveTask")
    suspend fun saveTask(
        @Body request: AddTaskDto
    ): TaskDto

    @POST("TaskManager/ManageTaskAttachment")
    suspend fun updateTaskAttachment(
        @Body request: UpdateTaskAttachmentDto
    ): CommonResponse

    @GET("TaskManager/CreateTask")
    suspend fun getWatcher(): WatchersDto

    @GET("Student/MedicalCard")
    suspend fun medicalCard(): MedicalCardResponse

    @POST("Student/UpdateMedicalCard")
    suspend fun updateMedicalCard(@Body request: UpdateMedicalCardRequest): CommonResponse

    @GET("Admin/StudentListToAssignHouse")
    suspend fun getStudentListToAssignHouse(
        @Query("ID") id: String,
        @Query("Orderby") orderBy: String
    ): StudentList

    @POST("Admin/AssignHouse")
    suspend fun assignHouse(
        @Body request: List<AssignHouseRequest>
    ): CommonResponse

    @POST("TaskManager/UpdateTask")
    suspend fun updateTask(
        @Body request: UpdateTaskDto
    ): CommonResponse

    @GET("TaskManager/UpdateTaskStatus")
    suspend fun updateTaskStatus(
        @Query("ID") id: String,
        @Query("Status") status: Int
    ): CommonResponse
}