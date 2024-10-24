package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class AddTaskDto(
    @SerializedName("assigneesIDs")
    val assigneesIDs: String?,
    @SerializedName("attachment")
    val attachment: Attachment?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("dueDate")
    val dueDate: String?,
    @SerializedName("isPublic")
    val isPublic: Boolean?,
    @SerializedName("priority")
    val priority: Int?,
    @SerializedName("remindBefore")
    val remindBefore: Int?,
    @SerializedName("repeatedBy")
    val repeatedBy: Int?,
    @SerializedName("startDate")
    val startDate: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("tlId")
    val tlId: Int?,
    @SerializedName("tskID")
    val tskID: Int?,
    @SerializedName("watchersIDs")
    val watchersIDs: String?
)

data class Attachment(
    @SerializedName("attachment")
    val attachment: String?,
    @SerializedName("fileExt")
    val fileExt: String?
)

data class AttachmentView(
    @SerializedName("attachment")
    val attachment: String?,
    @SerializedName("fileExt")
    val fileExt: String?,
    @SerializedName("fileURL")
    val fileURL: String?,

)