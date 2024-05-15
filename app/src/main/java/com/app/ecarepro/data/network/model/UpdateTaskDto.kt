package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class UpdateTaskDto(
    @SerializedName("fieldName")
    val fieldName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("newValue")
    val newValue: String?,
    @SerializedName("oldValue")
    val oldValue: String?
)

enum class TaskFiledName(val value: String) {
    TASK_TITLE("taskTitle"),
    START_DATE("startDate"),
    DUE_DATE("dueDate"),
    DESCRIPTION("description");
}
