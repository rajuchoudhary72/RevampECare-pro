package com.app.ecarepro.model
import com.google.gson.annotations.SerializedName
data class AssigneeDto(
    @SerializedName("assignees")
    val assignees: List<Assignee>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)