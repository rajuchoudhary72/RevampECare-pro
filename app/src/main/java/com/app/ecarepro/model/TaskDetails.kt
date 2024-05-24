package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class TaskDetails(
    @SerializedName("activities")
    val activities: List<TaskActivity>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("myID")
    val myID: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("task")
    val task: TaskInfo?
)

data class TaskActivity(
    @SerializedName("actionOn")
    val actionOn: String?,
    @SerializedName("activity")
    val activity: String?,
    @SerializedName("actorID")
    val actorID: Int?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?
)

data class TaskInfo(
    @SerializedName("assignBy")
    val assignBy: String?,
    @SerializedName("assignTo")
    val assignTo: List<Assign>?,
    @SerializedName("attachment")
    val attachment: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("dueDate")
    val dueDate: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("imOwner")
    val imOwner: Boolean?,
    @SerializedName("imWatcher")
    val imWatcher: Boolean?,
    @SerializedName("priority")
    val priority: Int?,
    @SerializedName("startDate")
    val startDate: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("taskList")
    val taskList: String?,
    @SerializedName("taskTitle")
    val taskTitle: String?,
    @SerializedName("tlId")
    val tlId: Int?,
    @SerializedName("tskID")
    val tskID: Int?,
    @SerializedName("watchers")
    val watchers: List<Watcher>?
)

data class Assign(
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("id")
    val id: Any?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("userID")
    val userID: Int?
)

data class Watcher(
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("id")
    val id: Any?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("userID")
    val userID: Int?,
    var isSelected: Boolean = false
)