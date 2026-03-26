package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.TaskManagerRemoteDataSource
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
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.TaskManagerService
import javax.inject.Inject

internal class TaskManagerRemoteDataSourceImpl @Inject constructor(
    private val taskManagerService: TaskManagerService,
) : TaskManagerRemoteDataSource {

    override suspend fun getTaskOverview(filter: Int): NetworkGetTaskOverview {
        val response = taskManagerService.getTaskOverview(filter = filter)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun getTaskDetails(taskId: String): NetworkGetTaskDetails {
        val response = taskManagerService.getTaskDetails(taskId = taskId)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun updateTask(updateTaskDto: NetworkUpdateTaskDto): NetworkCommonResponse {
        val response = taskManagerService.updateTask(updateTaskDto)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun updateTaskStatus(taskId: String, status: Int): NetworkUpdateTaskStatusResponse {
        val response = taskManagerService.updateTaskStatus(taskId = taskId, status = status)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun sendComment(sendCommentDto: NetworkSendCommentDto): NetworkCommonResponse {
        val response = taskManagerService.sendComment(sendCommentDto)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun updateTaskAttachment(dto: NetworkUpdateTaskAttachmentDto): NetworkCommonResponse {
        val response = taskManagerService.updateTaskAttachment(dto)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun getCreateTaskData(): NetworkCreateTaskResponse {
        val response = taskManagerService.getCreateTaskData()
        response.unwrapPayload { this }
        return response
    }

    override suspend fun getTaskListAssignees(tlId: Int): NetworkTaskListAssigneeResponse {
        val response = taskManagerService.getTaskListAssignees(tlId)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun saveTask(request: NetworkSaveTaskRequest): NetworkCommonResponse {
        val response = taskManagerService.saveTask(request)
        response.unwrapPayload { this }
        return response
    }
}
