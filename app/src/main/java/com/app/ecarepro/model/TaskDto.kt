package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class TaskDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("titles")
    val titles: List<Title>?
)

data class Title(
    @SerializedName("assignees")
    val assignees: List<Assignee>?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("tlId")
    val tlId: Int?
)

data class Assignee(
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("id")
    val id: String?,
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