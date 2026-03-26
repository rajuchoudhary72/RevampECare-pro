package com.app.ecarepro.core.network.model.taskmanger

import com.app.ecarepro.core.domain.model.TaskActivityDomain
import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskCommentDomain
import com.app.ecarepro.core.domain.model.TaskDetailsDomain
import com.app.ecarepro.core.domain.model.TaskInfoDomain
import com.app.ecarepro.core.domain.model.TaskWatcherDomain
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== Response Models ==============

@Serializable
data class NetworkGetTaskDetails(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("task")
    val task: NetworkTaskInfo? = null,
    @SerialName("activities")
    val activities: List<NetworkTaskActivity>? = null,
    @SerialName("comments")
    val comments: List<NetworkTaskComment>? = null,
    @SerialName("myID")
    val myID: Int? = null,
) : NetworkResponse

@Serializable
data class NetworkTaskInfo(
    @SerialName("id")
    val id: String? = null,
    @SerialName("taskTitle")
    val taskTitle: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("assignBy")
    val assignBy: String? = null,
    @SerialName("assignTo")
    val assignTo: List<NetworkTaskAssignee>? = null,
    @SerialName("startDate")
    val startDate: String? = null,
    @SerialName("dueDate")
    val dueDate: String? = null,
    @SerialName("priority")
    val priority: Int? = null,
    @SerialName("status")
    val status: Int? = null,
    @SerialName("attachment")
    val attachment: String? = null,
    @SerialName("imOwner")
    val imOwner: Boolean? = null,
    @SerialName("imWatcher")
    val imWatcher: Boolean? = null,
    @SerialName("watchers")
    val watchers: List<NetworkTaskWatcher>? = null,
    @SerialName("tlId")
    val tlId: Int? = null,
    @SerialName("taskList")
    val taskList: String? = null,
    @SerialName("tskID")
    val tskID: Int? = null,
    @SerialName("canChangeStatus")
    val canChangeStatus: Boolean? = null,
)

@Serializable
data class NetworkTaskComment(
    @SerialName("comment")
    val comment: String? = null,
    @SerialName("commentBy")
    val commentBy: Int? = null,
    @SerialName("commentOn")
    val commentOn: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("photo")
    val photo: String? = null,
)

@Serializable
data class NetworkTaskActivity(
    @SerialName("activity")
    val activity: String? = null,
    @SerialName("actionOn")
    val actionOn: String? = null,
    @SerialName("actorID")
    val actorID: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("photo")
    val photo: String? = null,
)

@Serializable
data class NetworkTaskWatcher(
    @SerialName("userID")
    val userID: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("status")
    val status: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("id")
    val id: String? = null,
)

// ============== Request DTOs ==============

@Serializable
data class NetworkUpdateTaskDto(
    @SerialName("fieldName")
    val fieldName: String,
    @SerialName("id")
    val id: String,
    @SerialName("newValue")
    val newValue: String,
    @SerialName("oldValue")
    val oldValue: String,
)

@Serializable
data class NetworkSendCommentDto(
    @SerialName("comment")
    val comment: String,
    @SerialName("id")
    val id: String,
)

@Serializable
data class NetworkUpdateTaskAttachmentDto(
    @SerialName("action")
    val action: Int,
    @SerialName("attachment")
    val attachment: NetworkAttachment,
    @SerialName("id")
    val id: String,
)

@Serializable
data class NetworkAttachment(
    @SerialName("attachment")
    val attachment: String,
    @SerialName("fileExt")
    val fileExt: String,
)

@Serializable
data class NetworkCommonResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
) : NetworkResponse

// ============== Domain Mappers ==============

fun NetworkGetTaskDetails.toDomainModel(): TaskDetailsDomain {
    return TaskDetailsDomain(
        task = task?.toDomainModel(),
        activities = activities?.map { it.toDomainModel() } ?: emptyList(),
        comments = comments?.map { it.toDomainModel() } ?: emptyList(),
        myID = myID ?: 0,
    )
}

private fun NetworkTaskInfo.toDomainModel(): TaskInfoDomain {
    return TaskInfoDomain(
        id = id ?: "",
        taskTitle = taskTitle ?: "",
        description = description ?: "",
        assignBy = assignBy ?: "",
        assignTo = assignTo?.map { it.toDomainModel() } ?: emptyList(),
        startDate = startDate ?: "",
        dueDate = dueDate ?: "",
        priority = priority ?: 0,
        status = status ?: 0,
        attachment = attachment ?: "",
        imOwner = imOwner ?: false,
        imWatcher = imWatcher ?: false,
        watchers = watchers?.map { it.toDomainModel() } ?: emptyList(),
        tlId = tlId ?: 0,
        taskList = taskList ?: "",
        tskID = tskID ?: 0,
        canChangeStatus = canChangeStatus ?: false,
    )
}

private fun NetworkTaskComment.toDomainModel(): TaskCommentDomain {
    return TaskCommentDomain(
        comment = comment ?: "",
        commentBy = commentBy ?: 0,
        commentOn = commentOn ?: "",
        name = name ?: "",
        designation = designation ?: "",
        photo = photo?.takeIf { it.isNotEmpty() && !it.endsWith("/") } ?: "",
    )
}

private fun NetworkTaskActivity.toDomainModel(): TaskActivityDomain {
    return TaskActivityDomain(
        activity = activity ?: "",
        actionOn = actionOn ?: "",
        actorID = actorID ?: 0,
        name = name ?: "",
        designation = designation ?: "",
        photo = photo?.takeIf { it.isNotEmpty() && !it.endsWith("/") } ?: "",
    )
}

internal fun NetworkTaskWatcher.toDomainModel(): TaskWatcherDomain {
    return TaskWatcherDomain(
        userID = userID ?: 0,
        name = name ?: "",
        designation = designation ?: "",
        photo = photo?.takeIf { it.isNotEmpty() && !it.endsWith("/") } ?: "",
        status = status ?: 0,
        title = title ?: "",
        id = id ?: "",
    )
}
