package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName


data class FormDataDepartmentResponseDto(
    @SerializedName("data")
    val `data`: List<Department>?,
    @SerializedName("status")
    val status: Boolean?
)

data class Department(
    @SerializedName("departmentID")
    val departmentID: Int?,
    @SerializedName("departmentName")
    val departmentName: String?
)