package com.app.ecarepro.core.network.model.taskmanger

import com.app.ecarepro.core.domain.model.CreateTaskData
import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskListItem
import com.app.ecarepro.core.domain.model.TaskWatcherDomain
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== Response Models ==============

@Serializable
data class NetworkCreateTaskResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("createTaskList")
    val createTaskList: Boolean? = null,
    @SerialName("taskList")
    val taskList: List<NetworkTaskListItem>? = null,
    @SerialName("watchers")
    val watchers: List<NetworkTaskWatcher>? = null,
) : NetworkResponse

@Serializable
data class NetworkTaskListItem(
    @SerialName("tlId")
    val tlId: Int? = null,
    @SerialName("title")
    val title: String? = null,
)

@Serializable
data class NetworkTaskListAssigneeResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("assignees")
    val assignees: List<NetworkTaskAssignee>? = null,
) : NetworkResponse

// ============== Request Models ==============

@Serializable
data class NetworkSaveTaskRequest(
    @SerialName("tskID")
    val tskID: Int? = null,
    @SerialName("tlId")
    val tlId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("priority")
    val priority: Int,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("dueDate")
    val dueDate: String,
    @SerialName("remindBefore")
    val remindBefore: Int,
    @SerialName("repeatedBy")
    val repeatedBy: Int? = null,
    @SerialName("repeatedTill")
    val repeatedTill: String? = null,
    @SerialName("isPublic")
    val isPublic: Boolean,
    @SerialName("assigneesIDs")
    val assigneesIDs: String,
    @SerialName("watchersIDs")
    val watchersIDs: String,
    @SerialName("attachment")
    val attachment: NetworkFileAttachment? = null,
)

@Serializable
data class NetworkFileAttachment(
    @SerialName("attachment")
    val attachment: String,
    @SerialName("fileExt")
    val fileExt: String,
    @SerialName("fileURL")
    val fileURL: String? = null,
)

// ============== Domain Mappers ==============

fun NetworkCreateTaskResponse.toDomainModel(): CreateTaskData {
    return CreateTaskData(
        canCreateTaskList = createTaskList ?: false,
        taskLists = taskList?.map { it.toDomainModel() } ?: emptyList(),
        watchers = watchers?.map { it.toDomainModel() } ?: emptyList(),
    )
}

fun NetworkTaskListItem.toDomainModel(): TaskListItem {
    return TaskListItem(
        id = tlId ?: 0,
        title = title ?: "",
    )
}

fun NetworkTaskListAssigneeResponse.toDomainAssignees(): List<TaskAssignee> {
    return assignees?.map { it.toDomainModel() } ?: emptyList()
}
