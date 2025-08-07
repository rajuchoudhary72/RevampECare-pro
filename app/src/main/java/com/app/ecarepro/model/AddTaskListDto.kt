package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName

data class AddTaskListDto(
    @SerializedName("assignee")
    val assignee: String?,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String?
)


