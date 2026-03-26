package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.CreateTaskData
import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskDetailsDomain
import com.app.ecarepro.core.domain.model.TaskOverviewData
import kotlinx.coroutines.flow.Flow

interface TaskManagerRepository {
    fun getTaskOverview(filter: Int): Flow<Result<TaskOverviewData>>
    fun getTaskDetails(taskId: String): Flow<Result<TaskDetailsDomain>>
    fun updateTask(fieldName: String, id: String, newValue: String, oldValue: String): Flow<Result<String>>
    fun updateTaskStatus(taskId: String, status: Int): Flow<Result<String>>
    fun sendComment(taskId: String, comment: String): Flow<Result<String>>
    fun updateTaskAttachment(taskId: String, base64: String, fileExt: String): Flow<Result<String>>
    fun getCreateTaskData(): Flow<Result<CreateTaskData>>
    fun getTaskListAssignees(tlId: Int): Flow<Result<List<TaskAssignee>>>
    fun saveTask(
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
    ): Flow<Result<String>>
}
