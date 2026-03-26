package com.app.ecarepro.core.network.model.taskmanger

import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskItem
import com.app.ecarepro.core.domain.model.TaskOverviewData
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetTaskOverview(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("myID")
    val myID: Int? = null,
    @SerialName("canChangeStatus")
    val canChangeStatus: Int? = null,
    @SerialName("overdue")
    val overdue: List<NetworkTaskItem>? = null,
    @SerialName("todays")
    val todays: List<NetworkTaskItem>? = null,
    @SerialName("upcoming")
    val upcoming: List<NetworkTaskItem>? = null,
    @SerialName("closed")
    val closed: List<NetworkTaskItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkTaskItem(
    @SerialName("id")
    val id: String? = null,
    @SerialName("tlId")
    val tlId: Int? = null,
    @SerialName("tskID")
    val tskID: Int? = null,
    @SerialName("taskList")
    val taskList: String? = null,
    @SerialName("taskTitle")
    val taskTitle: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("assignTo")
    val assignTo: List<NetworkTaskAssignee>? = null,
    @SerialName("watchers")
    val watchers: List<NetworkTaskAssignee>? = null,
    @SerialName("assignBy")
    val assignBy: String? = null,
    @SerialName("priority")
    val priority: Int? = null,
    @SerialName("startDate")
    val startDate: String? = null,
    @SerialName("dueDate")
    val dueDate: String? = null,
    @SerialName("attachment")
    val attachment: String? = null,
    @SerialName("imOwner")
    val imOwner: Boolean? = null,
    @SerialName("imWatcher")
    val imWatcher: Boolean? = null,
    @SerialName("canChangeStatus")
    val canChangeStatus: Boolean? = null,
    @SerialName("status")
    val status: Int? = null,
    @SerialName("overallStatus")
    val overallStatus: Int? = null,
)

@Serializable
data class NetworkTaskAssignee(
    @SerialName("id")
    val id: String? = null,
    @SerialName("userID")
    val userID: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("status")
    val status: Int? = null,
)

fun NetworkGetTaskOverview.toDomainModel(): TaskOverviewData {
    return TaskOverviewData(
        myID = myID ?: 0,
        canChangeStatus = canChangeStatus ?: 0,
        overdue = overdue?.map { it.toDomainModel() } ?: emptyList(),
        todays = todays?.map { it.toDomainModel() } ?: emptyList(),
        upcoming = upcoming?.map { it.toDomainModel() } ?: emptyList(),
        closed = closed?.map { it.toDomainModel() } ?: emptyList(),
    )
}

fun NetworkTaskItem.toDomainModel(): TaskItem {
    return TaskItem(
        id = id ?: "",
        tlId = tlId ?: 0,
        tskID = tskID ?: 0,
        taskList = taskList ?: "",
        taskTitle = taskTitle ?: "",
        description = description ?: "",
        assignTo = assignTo?.map { it.toDomainModel() } ?: emptyList(),
        watchers = watchers?.map { it.toDomainModel() } ?: emptyList(),
        assignBy = assignBy ?: "",
        priority = priority ?: 0,
        startDate = startDate ?: "",
        dueDate = dueDate ?: "",
        attachment = attachment ?: "",
        imOwner = imOwner ?: false,
        imWatcher = imWatcher ?: false,
        canChangeStatus = canChangeStatus ?: false,
        status = status ?: 0,
        overallStatus = overallStatus ?: 0,
    )
}

fun NetworkTaskAssignee.toDomainModel(): TaskAssignee {
    val photoUrl = photo?.takeIf {
        it.isNotEmpty() && !it.endsWith("/")
    } ?: ""
    return TaskAssignee(
        id = id ?: "",
        userID = userID ?: 0,
        title = title ?: "",
        name = name ?: "",
        designation = designation ?: "",
        photo = photoUrl,
        status = status ?: 0,
    )
}

@Serializable
data class NetworkUpdateTaskStatusResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
) : NetworkResponse
