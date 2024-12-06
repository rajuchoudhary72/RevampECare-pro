package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class WatchersDto(
    @SerializedName("createTaskList")
    val createTaskList: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("watchers")
    val watchers: List<Watcher>?,
    @SerializedName("taskList")
    val taskList: List<Title>?
)