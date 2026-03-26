package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.CreateTaskData
import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskDetailsDomain
import com.app.ecarepro.core.domain.model.TaskOverviewData
import com.app.ecarepro.core.domain.repository.TaskManagerRepository
import com.app.ecarepro.core.network.TaskManagerRemoteDataSource
import com.app.ecarepro.core.network.model.taskmanger.NetworkAttachment
import com.app.ecarepro.core.network.model.taskmanger.NetworkFileAttachment
import com.app.ecarepro.core.network.model.taskmanger.NetworkSaveTaskRequest
import com.app.ecarepro.core.network.model.taskmanger.NetworkSendCommentDto
import com.app.ecarepro.core.network.model.taskmanger.NetworkUpdateTaskAttachmentDto
import com.app.ecarepro.core.network.model.taskmanger.NetworkUpdateTaskDto
import com.app.ecarepro.core.network.model.taskmanger.toDomainAssignees
import com.app.ecarepro.core.network.model.taskmanger.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TaskManagerRepositoryImpl @Inject constructor(
    private val taskManagerRemoteDataSource: TaskManagerRemoteDataSource,
) : TaskManagerRepository {

    override fun getTaskOverview(filter: Int): Flow<Result<TaskOverviewData>> {
        return asResultFlow {
            taskManagerRemoteDataSource.getTaskOverview(filter).toDomainModel()
        }
    }

    override fun getTaskDetails(taskId: String): Flow<Result<TaskDetailsDomain>> {
        return asResultFlow {
            taskManagerRemoteDataSource.getTaskDetails(taskId).toDomainModel()
        }
    }

    override fun updateTask(
        fieldName: String,
        id: String,
        newValue: String,
        oldValue: String,
    ): Flow<Result<String>> {
        return asResultFlow {
            val response = taskManagerRemoteDataSource.updateTask(
                NetworkUpdateTaskDto(
                    fieldName = fieldName,
                    id = id,
                    newValue = newValue,
                    oldValue = oldValue,
                )
            )
            response.message
        }
    }

    override fun updateTaskStatus(taskId: String, status: Int): Flow<Result<String>> {
        return asResultFlow {
            val response = taskManagerRemoteDataSource.updateTaskStatus(taskId, status)
            response.message
        }
    }

    override fun sendComment(taskId: String, comment: String): Flow<Result<String>> {
        return asResultFlow {
            val response = taskManagerRemoteDataSource.sendComment(
                NetworkSendCommentDto(comment = comment, id = taskId)
            )
            response.message
        }
    }

    override fun updateTaskAttachment(
        taskId: String,
        base64: String,
        fileExt: String,
    ): Flow<Result<String>> {
        return asResultFlow {
            val response = taskManagerRemoteDataSource.updateTaskAttachment(
                NetworkUpdateTaskAttachmentDto(
                    action = 1,
                    attachment = NetworkAttachment(attachment = base64, fileExt = fileExt),
                    id = taskId,
                )
            )
            response.message
        }
    }

    override fun getCreateTaskData(): Flow<Result<CreateTaskData>> {
        return asResultFlow {
            taskManagerRemoteDataSource.getCreateTaskData().toDomainModel()
        }
    }

    override fun getTaskListAssignees(tlId: Int): Flow<Result<List<TaskAssignee>>> {
        return asResultFlow {
            taskManagerRemoteDataSource.getTaskListAssignees(tlId).toDomainAssignees()
        }
    }

    override fun saveTask(
        tlId: Int,
        title: String,
        description: String,
        priority: Int,
        startDate: String,
        dueDate: String,
        remindBefore: Int,
        isPublic: Boolean,
        assigneesIDs: String,
        watchersIDs: String,
        attachmentBase64: String?,
        attachmentFileExt: String?,
    ): Flow<Result<String>> {
        return asResultFlow {
            val attachment = if (attachmentBase64 != null && attachmentFileExt != null) {
                NetworkFileAttachment(attachment = attachmentBase64, fileExt = attachmentFileExt)
            } else null

            val response = taskManagerRemoteDataSource.saveTask(
                NetworkSaveTaskRequest(
                    tlId = tlId,
                    title = title,
                    description = description,
                    priority = priority,
                    startDate = startDate,
                    dueDate = dueDate,
                    remindBefore = remindBefore,
                    isPublic = isPublic,
                    assigneesIDs = assigneesIDs,
                    watchersIDs = watchersIDs,
                    attachment = attachment,
                )
            )
            response.message
        }
    }
}
