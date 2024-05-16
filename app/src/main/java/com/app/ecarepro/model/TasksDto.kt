package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class TasksDto(
    @SerializedName("canChangeStatus")
    val canChangeStatus: Int?,
    @SerializedName("closed")
    val closed: List<Task>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("myID")
    val myID: Int?,
    @SerializedName("overdue")
    val overdue: List<Task>?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("todays")
    val todays: List<Task>?,
    @SerializedName("upcoming")
    val upcoming: List<Task>?
)

data class Task(
    @SerializedName("assignBy")
    val assignBy: String?,
    @SerializedName("assignTo")
    val assignTo: List<AssignTo>?,
    @SerializedName("attachment")
    val attachment: String?,
    @SerializedName("description")
    val description: Any?,
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
    val watchers: Any?
)

data class AssignTo(
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