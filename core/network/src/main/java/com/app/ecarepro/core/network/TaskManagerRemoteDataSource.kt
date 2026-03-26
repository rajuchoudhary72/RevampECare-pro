package com.app.ecarepro.core.network

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

interface TaskManagerRemoteDataSource {
    suspend fun getTaskOverview(filter: Int): NetworkGetTaskOverview
    suspend fun getTaskDetails(taskId: String): NetworkGetTaskDetails
    suspend fun updateTask(updateTaskDto: NetworkUpdateTaskDto): NetworkCommonResponse
    suspend fun updateTaskStatus(taskId: String, status: Int): NetworkUpdateTaskStatusResponse
    suspend fun sendComment(sendCommentDto: NetworkSendCommentDto): NetworkCommonResponse
    suspend fun updateTaskAttachment(dto: NetworkUpdateTaskAttachmentDto): NetworkCommonResponse
    suspend fun getCreateTaskData(): NetworkCreateTaskResponse
    suspend fun getTaskListAssignees(tlId: Int): NetworkTaskListAssigneeResponse
    suspend fun saveTask(request: NetworkSaveTaskRequest): NetworkCommonResponse
}
