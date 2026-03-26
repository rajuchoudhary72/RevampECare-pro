package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.taskmanger.NetworkCommonResponse
import com.app.ecarepro.core.network.model.taskmanger.NetworkCreateTaskResponse
import com.app.ecarepro.core.network.model.taskmanger.NetworkGetTaskDetails
import com.app.ecarepro.core.network.model.taskmanger.NetworkGetTaskOverview
import com.app.ecarepro.core.network.model.taskmanger.NetworkSaveTaskRequest
import com.app.ecarepro.core.network.model.taskmanger.NetworkSendCommentDto
import com.app.ecarepro.core.network.model.taskmanger.NetworkTaskListAssigneeResponse
import com.app.ecarepro.core.network.model.taskmanger.NetworkUpdateTaskAttachmentDto
import com.app.ecarepro.core.network.model.taskmanger.NetworkUpdateTaskDto
import com.app.ecarepro.core.network.model.taskmanger.NetworkUpdateTaskStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TaskManagerService {

    @GET("TaskManager/Overview")
    suspend fun getTaskOverview(
        @Query("filter") filter: Int,
        @Query("pg") page: Int = 1,
    ): NetworkGetTaskOverview

    @GET("TaskManager/Task")
    suspend fun getTaskDetails(
        @Query("ID") taskId: String,
    ): NetworkGetTaskDetails

    @POST("TaskManager/UpdateTask")
    suspend fun updateTask(
        @Body updateTaskDto: NetworkUpdateTaskDto,
    ): NetworkCommonResponse

    @GET("TaskManager/UpdateTaskStatus")
    suspend fun updateTaskStatus(
        @Query("ID") taskId: String,
        @Query("Status") status: Int,
    ): NetworkUpdateTaskStatusResponse

    @POST("TaskManager/CommentOnTask")
    suspend fun sendComment(
        @Body sendCommentDto: NetworkSendCommentDto,
    ): NetworkCommonResponse

    @POST("TaskManager/ManageTaskAttachment")
    suspend fun updateTaskAttachment(
        @Body updateTaskAttachmentDto: NetworkUpdateTaskAttachmentDto,
    ): NetworkCommonResponse

    @GET("TaskManager/CreateTask")
    suspend fun getCreateTaskData(): NetworkCreateTaskResponse

    @GET("TaskManager/TaskListAssigne")
    suspend fun getTaskListAssignees(
        @Query("tlId") tlId: Int,
    ): NetworkTaskListAssigneeResponse

    @POST("TaskManager/SaveTask")
    suspend fun saveTask(
        @Body request: NetworkSaveTaskRequest,
    ): NetworkCommonResponse
}
