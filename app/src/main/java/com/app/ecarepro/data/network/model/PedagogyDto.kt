package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

data class PedagogyDto(
    @SerializedName("accessControl")
    val accessControl: AccessControl?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("pedagogies")
    val pedagogies: List<Pedagogy>?,
    @SerializedName("status")
    val status: String?
)

data class AccessControl(
    @SerializedName("canAdd")
    val canAdd: Boolean?,
    @SerializedName("canDelete")
    val canDelete: Boolean?,
    @SerializedName("canEdit")
    val canEdit: Boolean?,
    @SerializedName("canView")
    val canView: Boolean?
)

data class Pedagogy(
    @SerializedName("name")
    val name: String,
    @SerializedName("pdgID")
    val pdgID: Int,
    @SerializedName("steps")
    val steps: List<PedagogyStep>,
    @SerializedName("updateRecord")
    val updateRecord: UpdateRecord?,
    val isExpanded: Boolean = false
)

data class PedagogyStep(
    @SerializedName("description")
    val description: String?,
    @SerializedName("instruction")
    val instruction: String?,
    @SerializedName("pdgID")
    val pdgID: Int?,
    @SerializedName("pdgStpID")
    val pdgStpID: Int?,
    @SerializedName("stepName")
    val stepName: String?,
    @SerializedName("stepNumber")
    val stepNumber: Int?
)

data class UpdateRecord(
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("modifiedBy")
    val modifiedBy: String?,
    @SerializedName("modifiedOn")
    val modifiedOn: String?
)


